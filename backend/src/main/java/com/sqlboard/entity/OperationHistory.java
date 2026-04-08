package com.sqlboard.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 操作历史记录实体
 * 用于审计和追踪所有的数据操作
 */
@TableName("sb_operation_history")
public class OperationHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作类型: query, update, delete, import, export, stats
     */
    private String operationType;

    /**
     * 关联的数据库连接ID
     */
    private Long connectionId;

    /**
     * SQL语句
     */
    private String sqlContent;

    /**
     * 参数
     */
    private String parameters;

    /**
     * 操作状态: success, fail
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 影响行数/结果行数
     */
    private Integer affectedRows;

    /**
     * 执行耗时(毫秒)
     */
    private Long executionTime;

    /**
     * 是否脱敏
     */
    private Boolean desensitized;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
