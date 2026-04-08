package com.sqlboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqlboard.entity.SavedOperation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 保存操作Mapper
 */
@Mapper
public interface SavedOperationMapper extends BaseMapper<SavedOperation> {
}
