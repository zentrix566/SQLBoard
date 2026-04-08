package com.sqlboard.service;

import com.sqlboard.dto.ImportRequest;
import com.sqlboard.dto.SqlExecuteRequest;
import com.sqlboard.entity.OperationHistory;
import com.sqlboard.mapper.OperationHistoryMapper;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 操作历史记录服务
 */
@Service
public class OperationHistoryService {

    private final OperationHistoryMapper historyMapper;

    public OperationHistoryService(OperationHistoryMapper historyMapper) {
        this.historyMapper = historyMapper;
    }

    /**
     * 记录操作历史
     */
    public void recordHistory(SqlExecuteRequest request, String operator,
                              String status, String errorMessage,
                              Integer affectedRows, Long executionTime) {
        OperationHistory history = new OperationHistory();
        history.setOperator(operator);
        history.setOperationType(getOperationType(request.getSql()));
        history.setConnectionId(request.getConnectionId());
        history.setSqlContent(request.getSql());
        history.setParameters(request.getParameters() != null ? JSONUtil.toJsonStr(request.getParameters()) : null);
        history.setStatus(status);
        history.setErrorMessage(errorMessage);
        history.setAffectedRows(affectedRows);
        history.setExecutionTime(executionTime);
        history.setDesensitized(request.getNeedDesensitization());
        history.setCreateTime(LocalDateTime.now());
        historyMapper.insert(history);
    }

    /**
     * 记录导入操作历史
     */
    public void recordImportHistory(ImportRequest request, String operator,
                                    String status, String errorMessage,
                                    Integer affectedRows, Long executionTime,
                                    boolean desensitized) {
        OperationHistory history = new OperationHistory();
        history.setOperator(operator);
        history.setOperationType("import");
        history.setConnectionId(request.getConnectionId());
        history.setSqlContent("IMPORT TO " + request.getTableName());
        history.setParameters(request.getDesensitizationConfig() != null ? JSONUtil.toJsonStr(request.getDesensitizationConfig()) : null);
        history.setStatus(status);
        history.setErrorMessage(errorMessage);
        history.setAffectedRows(affectedRows);
        history.setExecutionTime(executionTime);
        history.setDesensitized(desensitized);
        history.setCreateTime(LocalDateTime.now());
        historyMapper.insert(history);
    }

    /**
     * 从SQL判断操作类型
     */
    private String getOperationType(String sql) {
        String trimmed = sql.trim().toLowerCase();
        if (trimmed.startsWith("select")) {
            return "query";
        } else if (trimmed.startsWith("update") || trimmed.startsWith("insert") || trimmed.startsWith("delete")) {
            return "write";
        } else {
            return "other";
        }
    }
}
