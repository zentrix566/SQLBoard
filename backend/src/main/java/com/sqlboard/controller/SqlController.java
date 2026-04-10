package com.sqlboard.controller;

import com.sqlboard.dto.ExportRequest;
import com.sqlboard.dto.SqlExecuteRequest;
import com.sqlboard.dto.SqlExecuteResponse;
import com.sqlboard.service.ExportService;
import com.sqlboard.service.SqlExecutionService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * SQL执行控制器
 * 处理SQL查询、更新和导出
 */
@RestController
@RequestMapping("/api/sql")
@CrossOrigin
public class SqlController {

    private static final Logger log = LoggerFactory.getLogger(SqlController.class);

    private final SqlExecutionService sqlExecutionService;
    private final ExportService exportService;

    public SqlController(SqlExecutionService sqlExecutionService,
                         ExportService exportService) {
        this.sqlExecutionService = sqlExecutionService;
        this.exportService = exportService;
    }

    /**
     * 执行SQL
     */
    @PostMapping("/execute")
    public SqlExecuteResponse execute(@RequestBody SqlExecuteRequest request) {
        // 这里operator可以从当前登录用户获取，暂时先写为system
        return sqlExecutionService.execute(request, "system");
    }

    /**
     * 导出数据
     */
    @PostMapping("/export")
    public void export(@RequestBody ExportRequest request, HttpServletResponse response)
            throws IOException {
        String fileName = request.getFileName() != null ? request.getFileName() : "export";
        if (!fileName.endsWith(".xlsx")) {
            fileName += ".xlsx";
        }

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition",
                "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        try {
            exportService.exportToExcel(request, response.getOutputStream(), "system");
            response.flushBuffer();
        } catch (Exception e) {
            log.error("导出失败", e);
            response.sendError(500, "导出失败: " + e.getMessage());
        }
    }
}
