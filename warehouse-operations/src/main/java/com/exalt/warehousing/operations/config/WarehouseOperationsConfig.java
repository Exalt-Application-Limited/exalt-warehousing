package com.exalt.warehousing.operations.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for Advanced Warehouse Operations Service
 * 
 * @author Micro Social Ecommerce Platform
 * @version 1.0
 */
@Configuration
@EnableAsync
@EnableScheduling
public class WarehouseOperationsConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("warehouse-ops-async-");
        executor.initialize();
        return executor;
    }
}

