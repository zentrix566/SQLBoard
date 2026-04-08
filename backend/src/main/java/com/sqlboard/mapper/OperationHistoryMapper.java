package com.sqlboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqlboard.entity.OperationHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作历史Mapper
 */
@Mapper
public interface OperationHistoryMapper extends BaseMapper<OperationHistory> {
}
