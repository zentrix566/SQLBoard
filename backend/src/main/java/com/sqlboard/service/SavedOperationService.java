package com.sqlboard.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqlboard.entity.SavedOperation;
import com.sqlboard.mapper.SavedOperationMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 保存的常用操作服务
 * 将常用的查询/修改/统计操作保存下来，方便重复使用
 */
@Service
public class SavedOperationService {

    private final SavedOperationMapper savedOperationMapper;

    public SavedOperationService(SavedOperationMapper savedOperationMapper) {
        this.savedOperationMapper = savedOperationMapper;
    }

    /**
     * 获取连接下的所有保存操作
     */
    public List<SavedOperation> listByConnection(Long connectionId) {
        LambdaQueryWrapper<SavedOperation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SavedOperation::getConnectionId, connectionId)
                .orderByDesc(SavedOperation::getCreateTime);
        return savedOperationMapper.selectList(wrapper);
    }

    /**
     * 保存操作
     */
    public void save(SavedOperation operation) {
        if (operation.getId() == null) {
            operation.setCreateTime(LocalDateTime.now());
            operation.setUpdateTime(LocalDateTime.now());
            savedOperationMapper.insert(operation);
        } else {
            operation.setUpdateTime(LocalDateTime.now());
            savedOperationMapper.updateById(operation);
        }
    }

    /**
     * 删除操作
     */
    public void delete(Long id) {
        savedOperationMapper.deleteById(id);
    }

    /**
     * 根据ID获取
     */
    public SavedOperation getById(Long id) {
        return savedOperationMapper.selectById(id);
    }
}
