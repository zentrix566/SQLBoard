package com.sqlboard.service;

import com.sqlboard.dto.SqlExecuteRequest;
import com.sqlboard.dto.SqlExecuteResponse;
import com.sqlboard.entity.DatabaseConnection;
import com.sqlboard.util.desensitization.DesensitizationType;
import com.sqlboard.util.desensitization.DesensitizationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SQL执行服务
 * 处理SQL查询、更新、删除等操作
 */
@Service
public class SqlExecutionService {

    private static final Logger log = LoggerFactory.getLogger(SqlExecutionService.class);

    private final DatabaseConnectionService connectionService;
    private final OperationHistoryService historyService;

    @Value("${sqlboard.block-danger-on-production:true}")
    private boolean blockDangerOnProduction;

    private final Map<Long, DataSource> dataSourceCache = new ConcurrentHashMap<>();

    public SqlExecutionService(DatabaseConnectionService connectionService,
                               OperationHistoryService historyService) {
        this.connectionService = connectionService;
        this.historyService = historyService;
    }

    /**
     * 执行SQL（自动判断是查询还是更新）
     */
    public SqlExecuteResponse execute(SqlExecuteRequest request, String operator) {
        long startTime = System.currentTimeMillis();
        String sql = request.getSql().trim();
        String sqlLower = sql.toLowerCase();

        try {
            DatabaseConnection connection = connectionService.getById(request.getConnectionId());
            DataSource dataSource = getDataSource(request.getConnectionId());
            String sqlType = getSqlType(sql);

            // 危险操作检查：生产环境禁止DROP/TRUNCATE等高危操作
            if (blockDangerOnProduction && "production".equals(connection.getEnvironment())) {
                if (sqlLower.contains("drop") || sqlLower.contains("truncate")) {
                    log.warn("拒绝在生产环境执行高危操作: {}", sql);
                    throw new IllegalArgumentException("⚠️ 生产环境禁止执行 DROP/TRUNCATE 高危操作，请谨慎操作");
                }
            }

            if ("select".equalsIgnoreCase(sqlType) || "show".equalsIgnoreCase(sqlType)) {
                return executeQuery(dataSource, request, startTime, operator);
            } else {
                return executeUpdate(dataSource, request, startTime, operator);
            }
        } catch (Exception e) {
            log.error("SQL执行失败: {}", e.getMessage(), e);
            long executionTime = System.currentTimeMillis() - startTime;
            historyService.recordHistory(request, operator, "fail", e.getMessage(), 0, executionTime);
            return SqlExecuteResponse.builder()
                    .success(false)
                    .message("执行失败: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 执行查询SQL
     */
    private SqlExecuteResponse executeQuery(DataSource dataSource, SqlExecuteRequest request,
                                             long startTime, String operator) throws SQLException {
        String sql = request.getSql();
        List<String> columns = new ArrayList<>();
        List<Map<String, Object>> data = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // 设置最大返回行数
            stmt.setMaxRows(10000);
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 获取列名
            for (int i = 1; i <= columnCount; i++) {
                columns.add(metaData.getColumnName(i));
            }

            // 获取数据
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                data.add(row);
            }

            // 脱敏处理
            if (Boolean.TRUE.equals(request.getNeedDesensitization()) &&
                    request.getDesensitizationConfig() != null) {
                data = desensitizeData(data, request.getDesensitizationConfig());
            }

            long executionTime = System.currentTimeMillis() - startTime;
            int rowCount = data.size();

            historyService.recordHistory(request, operator, "success", null, rowCount, executionTime);

            return SqlExecuteResponse.builder()
                    .success(true)
                    .message("查询成功，共返回 " + rowCount + " 行数据")
                    .executeType("query")
                    .columns(columns)
                    .data(data)
                    .affectedRows(rowCount)
                    .executionTime(executionTime)
                    .desensitized(request.getNeedDesensitization())
                    .build();
        }
    }

    /**
     * 执行更新SQL（update/insert/delete/create等）
     */
    private SqlExecuteResponse executeUpdate(DataSource dataSource, SqlExecuteRequest request,
                                               long startTime, String operator) throws SQLException {
        String sql = request.getSql();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            int affectedRows = stmt.executeUpdate(sql);
            long executionTime = System.currentTimeMillis() - startTime;

            historyService.recordHistory(request, operator, "success", null, affectedRows, executionTime);

            return SqlExecuteResponse.builder()
                    .success(true)
                    .message("执行成功，影响 " + affectedRows + " 行")
                    .executeType("update")
                    .affectedRows(affectedRows)
                    .executionTime(executionTime)
                    .build();
        }
    }

    /**
     * 对查询结果进行脱敏
     */
    private List<Map<String, Object>> desensitizeData(List<Map<String, Object>> data,
                                                      Map<String, String> desensitizationConfig) {
        for (Map<String, Object> row : data) {
            for (Map.Entry<String, String> entry : desensitizationConfig.entrySet()) {
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
        return data;
    }

    /**
     * 获取数据源（带缓存）
     */
    private DataSource getDataSource(Long connectionId) {
        if (dataSourceCache.containsKey(connectionId)) {
            return dataSourceCache.get(connectionId);
        }
        DatabaseConnection connection = connectionService.getById(connectionId);
        DataSource dataSource = connectionService.createDataSource(connection);
        dataSourceCache.put(connectionId, dataSource);
        return dataSource;
    }

    /**
     * 判断SQL类型
     */
    private String getSqlType(String sql) {
        String firstWord = sql.split("\\s+")[0];
        return firstWord.toLowerCase();
    }

    /**
     * 清除数据源缓存
     */
    public void clearCache(Long connectionId) {
        dataSourceCache.remove(connectionId);
    }
}
