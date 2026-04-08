package com.sqlboard.dto;

import java.util.List;
import java.util.Map;

/**
 * SQL执行响应DTO
 */
public class SqlExecuteResponse {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 消息
     */
    private String message;

    /**
     * 执行类型: query, update, delete
     */
    private String executeType;

    /**
     * 查询结果列名
     */
    private List<String> columns;

    /**
     * 查询结果数据
     */
    private List<Map<String, Object>> data;

    /**
     * 影响行数（用于update/delete/insert）
     */
    private Integer affectedRows;

    /**
     * 执行耗时（毫秒）
     */
    private Long executionTime;

    /**
     * 是否已经脱敏
     */
    private Boolean desensitized;

    public static SqlExecuteResponseBuilder builder() {
        return new SqlExecuteResponseBuilder();
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExecuteType() {
        return executeType;
    }

    public void setExecuteType(String executeType) {
        this.executeType = executeType;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public Integer getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(Integer affectedRows) {
        this.affectedRows = affectedRows;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Boolean getDesensitized() {
        return desensitized;
    }

    public void setDesensitized(Boolean desensitized) {
        this.desensitized = desensitized;
    }

    public static class SqlExecuteResponseBuilder {
        private Boolean success;
        private String message;
        private String executeType;
        private List<String> columns;
        private List<Map<String, Object>> data;
        private Integer affectedRows;
        private Long executionTime;
        private Boolean desensitized;

        public SqlExecuteResponseBuilder success(Boolean success) {
            this.success = success;
            return this;
        }

        public SqlExecuteResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public SqlExecuteResponseBuilder executeType(String executeType) {
            this.executeType = executeType;
            return this;
        }

        public SqlExecuteResponseBuilder columns(List<String> columns) {
            this.columns = columns;
            return this;
        }

        public SqlExecuteResponseBuilder data(List<Map<String, Object>> data) {
            this.data = data;
            return this;
        }

        public SqlExecuteResponseBuilder affectedRows(Integer affectedRows) {
            this.affectedRows = affectedRows;
            return this;
        }

        public SqlExecuteResponseBuilder executionTime(Long executionTime) {
            this.executionTime = executionTime;
            return this;
        }

        public SqlExecuteResponseBuilder desensitized(Boolean desensitized) {
            this.desensitized = desensitized;
            return this;
        }

        public SqlExecuteResponse build() {
            SqlExecuteResponse response = new SqlExecuteResponse();
            response.success = this.success;
            response.message = this.message;
            response.executeType = this.executeType;
            response.columns = this.columns;
            response.data = this.data;
            response.affectedRows = this.affectedRows;
            response.executionTime = this.executionTime;
            response.desensitized = this.desensitized;
            return response;
        }
    }
}
