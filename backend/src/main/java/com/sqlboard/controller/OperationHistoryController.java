package com.sqlboard.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqlboard.entity.OperationHistory;
import com.sqlboard.mapper.OperationHistoryMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 操作历史控制器
 */
@RestController
@RequestMapping("/api/history")
@CrossOrigin
public class OperationHistoryController {

    private final OperationHistoryMapper historyMapper;

    public OperationHistoryController(OperationHistoryMapper historyMapper) {
        this.historyMapper = historyMapper;
    }

    /**
     * 分页获取操作历史
     */
    @GetMapping("/list")
    public ResponseEntity<Page<OperationHistory>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long connectionId) {
        Page<OperationHistory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OperationHistory> wrapper = new LambdaQueryWrapper<>();
        if (connectionId != null) {
            wrapper.eq(OperationHistory::getConnectionId, connectionId);
        }
        wrapper.orderByDesc(OperationHistory::getCreateTime);
        Page<OperationHistory> result = historyMapper.selectPage(page, wrapper);
        return ResponseEntity.ok(result);
    }
}
