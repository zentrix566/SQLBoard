-- SQLBoard 初始化 SQL

-- 创建数据库
CREATE DATABASE IF NOT EXISTS sqlboard DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sqlboard;

-- 用户表（登录功能）
CREATE TABLE sb_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(200) NOT NULL COMMENT '密码（BCrypt加密）',
    nickname VARCHAR(50) COMMENT '昵称',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入默认用户 admin / admin123
-- 密码是 admin123 BCrypt加密后的值
INSERT INTO sb_user (username, password, nickname) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt0W.Q0y', '管理员');

-- 数据库连接表
CREATE TABLE sb_database_connection (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '连接名称',
    db_type VARCHAR(20) NOT NULL COMMENT '数据库类型: mysql, postgresql',
    host VARCHAR(200) NOT NULL COMMENT '主机地址',
    port INT NOT NULL DEFAULT 3306 COMMENT '端口',
    database_name VARCHAR(100) NOT NULL COMMENT '数据库名称',
    username VARCHAR(100) NOT NULL COMMENT '用户名',
    password VARCHAR(200) NOT NULL COMMENT '密码',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    environment VARCHAR(20) DEFAULT 'testing' COMMENT '环境标签: production, testing, development',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库连接配置';

-- 保存的常用操作表
CREATE TABLE sb_saved_operation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '操作名称',
    description VARCHAR(500) COMMENT '操作描述',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型: query, update, delete, stats',
    connection_id BIGINT NOT NULL COMMENT '关联的数据库连接ID',
    sql_content TEXT NOT NULL COMMENT 'SQL内容',
    parameters TEXT COMMENT '参数说明(JSON)',
    desensitization_config TEXT COMMENT '脱敏配置(JSON)',
    require_approval TINYINT(1) DEFAULT 0 COMMENT '是否需要审批',
    creator VARCHAR(50) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保存的常用操作';

-- 操作历史表
CREATE TABLE sb_operation_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    operator VARCHAR(50) NOT NULL COMMENT '操作人',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型: query, update, delete, import, export, stats',
    connection_id BIGINT NOT NULL COMMENT '关联的数据库连接ID',
    sql_content TEXT COMMENT 'SQL语句',
    parameters TEXT COMMENT '参数(JSON)',
    status VARCHAR(20) NOT NULL COMMENT '操作状态: success, fail',
    error_message TEXT COMMENT '错误信息',
    affected_rows INT COMMENT '影响行数',
    execution_time BIGINT COMMENT '执行耗时(毫秒)',
    desensitized TINYINT(1) DEFAULT 0 COMMENT '是否脱敏',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作历史记录';
