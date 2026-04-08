package com.sqlboard.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.sqlboard.dto.ImportRequest;
import com.sqlboard.util.desensitization.DesensitizationType;
import com.sqlboard.util.desensitization.DesensitizationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据导入服务
 * 支持从Excel导入数据到目标数据库，支持脱敏处理和分批插入
 */
@Service
public class ImportService {

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    private final DatabaseConnectionService connectionService;
    private final OperationHistoryService historyService;

    public ImportService(DatabaseConnectionService connectionService,
                         OperationHistoryService historyService) {
        this.connectionService = connectionService;
        this.historyService = historyService;
    }

    /**
     * 从Excel导入数据
     * 使用流式读取处理大数据量，分批插入
     */
    public Map<String, Object> importFromExcel(MultipartFile file, ImportRequest request, String operator)
            throws Exception {
        long startTime = System.currentTimeMillis();
        Long connectionId = request.getConnectionId();
        String tableName = request.getTableName();

        DataSource dataSource = connectionService.createDataSource(connectionService.getById(connectionId));

        AtomicInteger totalRows = new AtomicInteger(0);
        AtomicInteger successRows = new AtomicInteger(0);
        List<String> columns = new ArrayList<>();
        List<Map<String, Object>> batchBuffer = Collections.synchronizedList(new ArrayList<>());
        int batchSize = request.getBatchSize() != null ? request.getBatchSize() : 1000;

        try {
            // PageReadListener 会自动分批，每批 batchSize 行
            // 使用匿名 ReadListener 来支持 doAfterAllAnalysed 回调
            ReadListener<Map<Integer, Object>> listener =
                new ReadListener<Map<Integer, Object>>() {
                    @Override
                    public void invoke(Map<Integer, Object> row, AnalysisContext context) {
                        // PageReadListener 已经处理了分页，这里我们直接按行处理
                        // 第一行是表头
                        if (totalRows.get() == 0) {
                            row.forEach((index, value) -> {
                                if (value != null) {
                                    columns.add(value.toString());
                                }
                            });
                            return;
                        }

                        List<Object> rowValues = new ArrayList<>(row.values());
                        Map<String, Object> dataRow = new LinkedHashMap<>();
                        for (int i = 0; i < columns.size() && i < rowValues.size(); i++) {
                            dataRow.put(columns.get(i), rowValues.get(i));
                        }

                        if (Boolean.TRUE.equals(request.getNeedDesensitization()) &&
                                request.getDesensitizationConfig() != null) {
                            desensitizeRow(dataRow, request);
                        }

                        batchBuffer.add(dataRow);
                        totalRows.incrementAndGet();

                        if (batchBuffer.size() >= batchSize) {
                            try {
                                int inserted = insertBatch(dataSource, tableName, columns, batchBuffer);
                                successRows.addAndGet(inserted);
                                batchBuffer.clear();
                            } catch (SQLException e) {
                                log.error("批量插入失败: {}", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void doAfterAllAnalysed(AnalysisContext context) {
                        if (!batchBuffer.isEmpty()) {
                            try {
                                int inserted = insertBatch(dataSource, tableName, columns, batchBuffer);
                                successRows.addAndGet(inserted);
                            } catch (SQLException e) {
                                log.error("最后批次插入失败: {}", e.getMessage());
                            }
                        }
                        log.info("导入完成，总计 {} 行，成功 {} 行", totalRows.get() - 1, successRows.get());
                    }
                };

            EasyExcel.read(file.getInputStream(), listener)
                .sheet()
                .doRead();

            // 所有数据读取完成后处理剩余数据
            if (!batchBuffer.isEmpty()) {
                try {
                    int inserted = insertBatch(dataSource, tableName, columns, batchBuffer);
                    successRows.addAndGet(inserted);
                } catch (SQLException e) {
                    log.error("最后批次插入失败: {}", e.getMessage());
                }
            }
            log.info("导入完成，总计 {} 行，成功 {} 行", totalRows.get() - 1, successRows.get());

            long executionTime = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("totalRows", totalRows.get() - 1);
            result.put("successRows", successRows.get());
            result.put("executionTime", executionTime);

            historyService.recordImportHistory(request, operator, "success", null,
                    successRows.get(), executionTime, Boolean.TRUE.equals(request.getNeedDesensitization()));

            return result;

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            historyService.recordImportHistory(request, operator, "fail", e.getMessage(),
                    totalRows.get(), executionTime, Boolean.TRUE.equals(request.getNeedDesensitization()));
            log.error("导入失败: {}", e.getMessage(), e);
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
     * 对单行数据进行脱敏
     */
    private void desensitizeRow(Map<String, Object> row, ImportRequest request) {
        for (Map.Entry<String, String> entry : request.getDesensitizationConfig().entrySet()) {
            String columnName = entry.getKey();
            String typeName = entry.getValue();
            if (row.containsKey(columnName) && row.get(columnName) != null) {
                Object value = row.get(columnName);
                if (value instanceof String strValue) {
                    DesensitizationType type = DesensitizationType.valueOf(typeName);
                    String desensitized = DesensitizationUtil.apply(strValue, type);
                    row.put(columnName, desensitized);
                }
            }
        }
    }

    /**
     * 批量插入数据
     */
    private int insertBatch(DataSource dataSource, String tableName, List<String> columns,
                            List<Map<String, Object>> data) throws SQLException {
        if (data.isEmpty()) {
            return 0;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        sql.append(String.join(",", columns));
        sql.append(") VALUES (");
        sql.append("?,".repeat(columns.size()));
        sql.setLength(sql.length() - 1);
        sql.append(")");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (Map<String, Object> row : data) {
                for (int i = 0; i < columns.size(); i++) {
                    Object value = row.get(columns.get(i));
                    pstmt.setObject(i + 1, value);
                }
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            return results.length;
        }
    }
}
