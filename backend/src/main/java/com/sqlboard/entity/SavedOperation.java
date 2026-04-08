package com.sqlboard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 保存的常用操作实体
 * 包括常用查询和常用修改操作，可以直接复用
 */
@TableName("sb_saved_operation")
public class SavedOperation {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作名称
     */
    private String name;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作类型: query, update, delete, stats
     */
    private String operationType;

    /**
     * 关联的数据库连接ID
     */
    private Long connectionId;

    /**
     * SQL内容
     */
    private String sqlContent;

    /**
     * 参数说明（JSON格式）
     */
    private String parameters;

    /**
     * 脱敏配置（JSON格式）
     */
    private String desensitizationConfig;

    /**
     * 是否需要审批
     */
    private Boolean requireApproval;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Long connectionId) {
        this.connectionId = connectionId;
    }

    public String getSqlContent() {
        return sqlContent;
    }

    public void setSqlContent(String sqlContent) {
        this.sqlContent = sqlContent;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getDesensitizationConfig() {
        return desensitizationConfig;
    }

    public void setDesensitizationConfig(String desensitizationConfig) {
        this.desensitizationConfig = desensitizationConfig;
    }

    public Boolean getRequireApproval() {
        return requireApproval;
    }

    public void setRequireApproval(Boolean requireApproval) {
        this.requireApproval = requireApproval;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
