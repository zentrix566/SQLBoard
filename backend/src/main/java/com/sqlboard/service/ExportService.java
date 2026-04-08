package com.sqlboard.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.sqlboard.dto.ExportRequest;
import com.sqlboard.util.desensitization.DesensitizationType;
import com.sqlboard.util.desensitization.DesensitizationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.OutputStream;
import java.sql.*;
import java.util.*;

/**
 * 数据导出服务
 * 支持大数据量导出，自带脱敏功能
 */
@Service
public class ExportService {

    private static final Logger log = LoggerFactory.getLogger(ExportService.class);

    private final DatabaseConnectionService connectionService;
    private final OperationHistoryService historyService;

    public ExportService(DatabaseConnectionService connectionService,
                         OperationHistoryService historyService) {
        this.connectionService = connectionService;
        this.historyService = historyService;
    }

    /**
     * 导出数据到Excel
     * 使用流式写入处理大数据量
     */
    public void exportToExcel(ExportRequest request, OutputStream outputStream, String operator)
            throws SQLException {
        long startTime = System.currentTimeMillis();
        Long connectionId = request.getConnectionId();
        String sql = request.getSql();

        DataSource dataSource = connectionService.createDataSource(connectionService.getById(connectionId));

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<String> columns = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                columns.add(metaData.getColumnName(i));
            }

            // 创建Excel writer
            ExcelWriter excelWriter = EasyExcel.write(outputStream).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("数据").head(buildHead(columns)).build();

            // 分批写入，每批1000行
            List<List<Object>> dataBatch = new ArrayList<>(1000);
            int totalRows = 0;

            while (rs.next()) {
                List<Object> row = new ArrayList<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    String columnName = metaData.getColumnName(i);

                    // 转换日期时间类型，EasyExcel 不支持 Timestamp
                    if (value instanceof java.sql.Timestamp) {
                        value = ((java.sql.Timestamp) value).toLocalDateTime().toString();
                    } else if (value instanceof java.sql.Date) {
                        value = ((java.sql.Date) value).toString();
                    } else if (value instanceof java.sql.Time) {
                        value = ((java.sql.Time) value).toString();
                    } else if (value instanceof java.time.LocalDateTime) {
                        value = ((java.time.LocalDateTime) value).toString();
                    } else if (value instanceof java.time.LocalDate) {
                        value = ((java.time.LocalDate) value).toString();
                    } else if (value instanceof java.time.LocalTime) {
                        value = ((java.time.LocalTime) value).toString();
                    }

                    // 脱敏处理
                    if (Boolean.TRUE.equals(request.getNeedDesensitization()) &&
                            request.getDesensitizationConfig() != null &&
                            request.getDesensitizationConfig().containsKey(columnName)) {
                        if (value instanceof String strValue) {
                            String typeName = request.getDesensitizationConfig().get(columnName);
                            DesensitizationType type = DesensitizationType.valueOf(typeName);
                            value = DesensitizationUtil.apply(strValue, type);
                        }
                    }
                    row.add(value);
                }
                dataBatch.add(row);
                totalRows++;

                // 达到批次大小，写入
                if (dataBatch.size() >= 1000) {
                    excelWriter.write(dataBatch, writeSheet);
                    dataBatch.clear();
                }
            }

            // 写入剩余数据
            if (!dataBatch.isEmpty()) {
                excelWriter.write(dataBatch, writeSheet);
            }

            excelWriter.finish();

            long executionTime = System.currentTimeMillis() - startTime;
            historyService.recordHistory(request, operator, "success", null, totalRows, executionTime);

            log.info("导出完成，共 {} 行，耗时 {}ms", totalRows, executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            historyService.recordHistory(request, operator, "fail", e.getMessage(), 0, executionTime);
            log.error("导出失败: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (dataSource instanceof AutoCloseable closeable) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    log.warn("关闭数据源失败: {}", e.getMessage());
                }
            }
        }
    }

    /**
     * 构建表头
     */
    private List<List<String>> buildHead(List<String> columns) {
        List<List<String>> head = new ArrayList<>();
        for (String column : columns) {
            head.add(Collections.singletonList(column));
        }
        return head;
    }
}
