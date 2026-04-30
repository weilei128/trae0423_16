package com.warehouse.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!isDatabaseInitialized()) {
            logger.info("数据库表不存在，开始执行 schema.sql 初始化...");
            executeSchemaScript();
            logger.info("数据库表初始化完成！");
        } else {
            logger.info("数据库表已存在，跳过初始化");
        }
    }

    private boolean isDatabaseInitialized() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = 'sku'",
                Integer.class
            );
            return count != null && count > 0;
        } catch (Exception e) {
            logger.warn("检查数据库表是否存在失败: {}", e.getMessage());
            return false;
        }
    }

    private void executeSchemaScript() {
        try (Connection connection = dataSource.getConnection()) {
            ClassPathResource resource = new ClassPathResource("schema.sql");
            ScriptUtils.executeSqlScript(connection, resource);
            logger.info("schema.sql 执行成功");
        } catch (Exception e) {
            logger.error("执行 schema.sql 失败: {}", e.getMessage(), e);
        }
    }
}
