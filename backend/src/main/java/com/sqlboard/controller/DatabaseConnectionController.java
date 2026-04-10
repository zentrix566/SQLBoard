package com.sqlboard.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqlboard.entity.DatabaseConnection;
import com.sqlboard.mapper.DatabaseConnectionMapper;
import com.sqlboard.service.DatabaseConnectionService;
import com.sqlboard.service.SqlExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据库连接管理控制器
 */
@RestController
@RequestMapping("/api/connection")
@CrossOrigin
public class DatabaseConnectionController {

    private final DatabaseConnectionService connectionService;
    private final DatabaseConnectionMapper connectionMapper;
    private final SqlExecutionService sqlExecutionService;

    public DatabaseConnectionController(DatabaseConnectionService connectionService,
                                        DatabaseConnectionMapper connectionMapper,
                                        SqlExecutionService sqlExecutionService) {
        this.connectionService = connectionService;
        this.connectionMapper = connectionMapper;
        this.sqlExecutionService = sqlExecutionService;
    }

    /**
     * 获取所有连接列表
     */
    @GetMapping("/list")
    public List<DatabaseConnection> list() {
        LambdaQueryWrapper<DatabaseConnection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DatabaseConnection::getEnabled, true)
                .orderByAsc(DatabaseConnection::getName);
        return connectionMapper.selectList(wrapper);
    }

    /**
     * 获取单个连接
     */
    @GetMapping("/{id}")
    public DatabaseConnection getById(@PathVariable Long id) {
        return connectionService.getById(id);
    }

    /**
     * 保存连接
     */
    @PostMapping("/save")
    public void save(@RequestBody DatabaseConnection connection) {
        connection.setEnabled(true);
        connectionService.saveOrUpdate(connection);
        // 清除缓存，下次使用新连接
        if (connection.getId() != null) {
            sqlExecutionService.clearCache(connection.getId());
        }
    }

    /**
     * 删除连接
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        connectionService.delete(id);
        sqlExecutionService.clearCache(id);
    }
}
