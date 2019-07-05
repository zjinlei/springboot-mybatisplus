package com.helios.seata.common.config;

import com.helios.seata.common.SeataProperties;
import io.seata.spring.annotation.GlobalTransactionScanner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties({SeataProperties.class})
public class GlobalTransactionAutoConfiguration {
    private final ApplicationContext applicationContext;
    private final SeataProperties seataProperties;

    public GlobalTransactionAutoConfiguration(ApplicationContext applicationContext, SeataProperties seataProperties) {
        this.applicationContext = applicationContext;
        this.seataProperties = seataProperties;
    }

    @Bean
    public GlobalTransactionScanner globalTransactionScanner() {
        String applicationName = this.applicationContext.getEnvironment().getProperty("spring.application.name");
        String txServiceGroup = this.seataProperties.getTxServiceGroup();
        if (StringUtils.isEmpty(txServiceGroup)) {
            txServiceGroup = applicationName + "-fescar-service-group";
            this.seataProperties.setTxServiceGroup(txServiceGroup);
        }

        return new GlobalTransactionScanner(applicationName, txServiceGroup);
    }

    public static void main(String[] args) {
        new GlobalTransactionScanner("ap", "-fescar-service-group");
    }
}
