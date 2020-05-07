package com.helios.seata.account.config;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import io.seata.rm.datasource.xa.DataSourceProxyXA;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
public class DataBaseConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }

    @Primary
    @Bean("dataSourceProxy")
    public DataSource dataSource(DataSource dataSource) {
        return new DataSourceProxyXA(dataSource);
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
            .getResources("classpath*:/mapper/*.xml"));
        return factoryBean.getObject();
    }
}
