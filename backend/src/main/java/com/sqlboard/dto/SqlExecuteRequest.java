package com.sqlboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/**
 * SQL执行请求DTO
 */
public class SqlExecuteRequest {

    /**
     * 数据库连接ID
     */
    @NotNull(message = "数据库连接ID不能为空")
    private Long connectionId;

    /**
     * SQL语句
     */
    @NotBlank(message = "SQL语句不能为空")
    private String sql;

    /**
     * 参数（参数化查询）
     */
    private Map<String, Object> parameters;

    /**
     * 是否需要脱敏
     */
    private Boolean needDesensitization;

    /**
     * 脱敏配置
     */
    private Map<String, String> desensitizationConfig;

    public Long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Long connectionId) {
        this.connectionId = connectionId;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Boolean getNeedDesensitization() {
        return needDesensitization;
    }

    public void setNeedDesensitization(Boolean needDesensitization) {
        this.needDesensitization = needDesensitization;
    }

    public Map<String, String> getDesensitizationConfig() {
        return desensitizationConfig;
    }

    public void setDesensitizationConfig(Map<String, String> desensitizationConfig) {
        this.desensitizationConfig = desensitizationConfig;
    }
}
