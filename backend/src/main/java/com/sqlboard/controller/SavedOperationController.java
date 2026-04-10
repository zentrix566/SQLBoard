package com.sqlboard.controller;

import com.sqlboard.entity.SavedOperation;
import com.sqlboard.service.SavedOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 保存的常用操作控制器
 */
@RestController
@RequestMapping("/api/operation")
@CrossOrigin
public class SavedOperationController {

    private final SavedOperationService savedOperationService;

    public SavedOperationController(SavedOperationService savedOperationService) {
        this.savedOperationService = savedOperationService;
    }

    /**
     * 获取连接下的所有保存操作
     */
    @GetMapping("/list/{connectionId}")
    public List<SavedOperation> list(@PathVariable Long connectionId) {
        return savedOperationService.listByConnection(connectionId);
    }

    /**
     * 获取操作详情
     */
    @GetMapping("/{id}")
    public SavedOperation get(@PathVariable Long id) {
        return savedOperationService.getById(id);
    }

    /**
     * 保存操作
     */
    @PostMapping("/save")
    public void save(@RequestBody SavedOperation operation) {
        savedOperationService.save(operation);
    }

    /**
     * 删除操作
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        savedOperationService.delete(id);
    }
}
