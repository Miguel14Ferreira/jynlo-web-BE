package com.example.desporto24.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class JynloPushAsyncConfiguration {
    // Spring Boot backs off its default executor when the push executor exists.
    // Keep the executor expected by asynchronous SNID source validation explicit.
    @Bean("applicationTaskExecutor")
    public TaskExecutor applicationTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(1);
        pool.setMaxPoolSize(1);
        pool.setQueueCapacity(25);
        pool.setThreadNamePrefix("jynlo-app-");
        pool.initialize();
        return pool;
    }

    @Bean("jynloPushExecutor")
    public Executor jynloPushExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(1);
        pool.setMaxPoolSize(2);
        pool.setQueueCapacity(100);
        pool.setThreadNamePrefix("jynlo-push-");
        pool.initialize();
        return pool;
    }
}
