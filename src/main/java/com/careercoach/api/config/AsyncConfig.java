package com.careercoach.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 비동기 처리 설정
 * 이벤트 기반 비동기 처리를 위한 설정을 제공합니다.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 비동기 작업을 위한 스레드 풀 설정
     */
    @Bean(name = "careerCoachTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 코어 스레드 수
        executor.setCorePoolSize(5);
        
        // 최대 스레드 수
        executor.setMaxPoolSize(10);
        
        // 큐 용량
        executor.setQueueCapacity(25);
        
        // 스레드 이름 접두사
        executor.setThreadNamePrefix("career-coach-async-");
        
        // 스레드 풀 종료 대기 시간
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        // 초기화
        executor.initialize();
        
        return executor;
    }

    /**
     * AI 서비스 전용 스레드 풀 설정
     */
    @Bean(name = "aiServiceTaskExecutor")
    public Executor aiServiceTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // AI 서비스는 더 많은 리소스 필요
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(15);
        executor.setThreadNamePrefix("ai-service-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(120);
        
        executor.initialize();
        
        return executor;
    }
} 