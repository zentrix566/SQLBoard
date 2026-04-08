package com.sqlboard.service;

import com.sqlboard.entity.DatabaseConnection;
import com.sqlboard.mapper.DatabaseConnectionMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * 数据库连接服务
 */
@Service
public class DatabaseConnectionService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConnectionService.class);

    private final DatabaseConnectionMapper connectionMapper;

    public DatabaseConnectionService(DatabaseConnectionMapper connectionMapper) {
        this.connectionMapper = connectionMapper;
    }

    /**
     * 根据ID获取连接配置
     */
    public DatabaseConnection getById(Long id) {
        return connectionMapper.selectById(id);
    }

    /**
     * 创建数据源
     */
    public HikariDataSource createDataSource(DatabaseConnection connection) {
        HikariConfig config = new HikariConfig();
        String url = buildJdbcUrl(connection);
        config.setJdbcUrl(url);
        config.setUsername(connection.getUsername());
        config.setPassword(connection.getPassword());
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(30000);
        return new HikariDataSource(config);
    }

    /**
     * 构建JDBC URL
     */
    private String buildJdbcUrl(DatabaseConnection connection) {
        String dbType = connection.getDbType().toLowerCase();
        if ("mysql".equals(dbType)) {
            return String.format(
                "jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai",
                connection.getHost(), connection.getPort(), connection.getDatabaseName()
            );
        } else if ("postgresql".equals(dbType)) {
            return String.format(
                "jdbc:postgresql://%s:%d/%s",
                connection.getHost(), connection.getPort(), connection.getDatabaseName()
            );
        }
        throw new IllegalArgumentException("不支持的数据库类型: " + dbType);
    }

    /**
     * 保存或更新连接
     */
    public void saveOrUpdate(DatabaseConnection connection) {
        if (connection.getId() == null) {
            connectionMapper.insert(connection);
        } else {
            connectionMapper.updateById(connection);
        }
    }

    /**
     * 删除连接
     */
    public void delete(Long id) {
        connectionMapper.deleteById(id);
    }
}
