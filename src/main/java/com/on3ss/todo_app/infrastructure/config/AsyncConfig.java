package com.on3ss.todo_app.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "imageProcessorExecutor")
    Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 1. Core Pool Size: Number of threads kept alive always (the base staff)
        executor.setCorePoolSize(3);

        // 2. Max Pool Size: Maximum threads allowed if the queue gets full
        executor.setMaxPoolSize(10);

        // 3. Queue Capacity: How many tasks wait in line before spawning extra threads
        executor.setQueueCapacity(100);

        // 4. Thread Name Prefix: Makes debugging/logs much easier
        executor.setThreadNamePrefix("ImageWorker-");

        // 5. Shutdown behavior: Wait for active tasks to finish before stopping the app
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();
        return executor;
    }
}