package com.sqlboard.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 数据导出请求DTO
 */
public class ExportRequest extends SqlExecuteRequest {

    /**
     * 导出文件格式: excel, csv
     */
    private String format;

    /**
     * 文件名
     */
    private String fileName;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
