package com.example.demo.config;
 
import com.mybatisflex.core.audit.AuditManager;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 配置sql打印
 *
 * @author ***
 * @date 2023/11/09 0009 17:35
 */
@Configuration
public class MyBatisFlexConfiguration {
    private static final Logger logger = LoggerFactory.getLogger("mybatis-flex-sql");
 
    public MyBatisFlexConfiguration() {
        //开启审计功能
        AuditManager.setAuditEnable(true);
        //设置 SQL 审计收集器
        AuditManager.setMessageCollector(
                auditMessage -> logger.info(">>>>>>SQL：{};  {}ms", auditMessage.getFullSql().toLowerCase(), auditMessage.getElapsedTime())
        );
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        // 与 application.yml 中的 mapper 路径保持一致
        factory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:mapper/**/*.xml"));
        factory.setTypeAliasesPackage("com.example.demo.entity");
        return factory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}