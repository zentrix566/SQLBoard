package com.sqlboard.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * 数据导入请求DTO
 */
public class ImportRequest {

    /**
     * 目标数据库连接ID
     */
    @NotNull(message = "数据库连接ID不能为空")
    private Long connectionId;

    /**
     * 目标表名
     */
    @NotNull(message = "目标表名不能为空")
    private String tableName;

    /**
     * 是否需要脱敏（导入时如果是从生产到测试，可能需要脱敏）
     */
    private Boolean needDesensitization;

    /**
     * 脱敏配置
     */
    private Map<String, String> desensitizationConfig;

    /**
     * 批次大小（分批插入避免内存溢出）
     */
    private Integer batchSize = 1000;

    public Long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Long connectionId) {
        this.connectionId = connectionId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
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

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }
}
