package com.sqlboard.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sqlboard.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
