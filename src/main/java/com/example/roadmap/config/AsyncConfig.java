package com.example.roadmap.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean(name = "roadMapAsyncExecutor")
  public ThreadPoolTaskExecutor roadMapAsyncExecutor(
      @Value("${app.async.executor.core-pool-size:32}") int corePoolSize,
      @Value("${app.async.executor.max-pool-size:64}") int maxPoolSize,
      @Value("${app.async.executor.queue-capacity:4096}") int queueCapacity) {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(corePoolSize);
    executor.setMaxPoolSize(maxPoolSize);
    executor.setQueueCapacity(queueCapacity);
    executor.setThreadNamePrefix("roadmap-async-");
    executor.initialize();
    return executor;
  }
}
