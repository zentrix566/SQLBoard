package com.sqlboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqlboard.entity.DatabaseConnection;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据库连接Mapper
 */
@Mapper
public interface DatabaseConnectionMapper extends BaseMapper<DatabaseConnection> {
}
