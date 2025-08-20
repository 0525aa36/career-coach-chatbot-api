package com.careercoach.api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 캐시 설정
 * Caffeine 캐시를 사용한 성능 최적화 설정을 제공합니다.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 캐시 매니저 설정
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // 캐시별 설정
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000) // 최대 캐시 항목 수
                .expireAfterWrite(Duration.ofMinutes(30)) // 30분 후 만료
                .expireAfterAccess(Duration.ofMinutes(10)) // 10분간 접근 없으면 만료
                .recordStats()); // 통계 기록
        
        return cacheManager;
    }

    /**
     * 면접 질문 캐시 설정
     */
    @Bean
    public Caffeine<Object, Object> interviewQuestionsCacheConfig() {
        return Caffeine.newBuilder()
                .maximumSize(500) // 면접 질문은 더 적은 수로 제한
                .expireAfterWrite(1, TimeUnit.HOURS) // 1시간 후 만료
                .expireAfterAccess(30, TimeUnit.MINUTES) // 30분간 접근 없으면 만료
                .recordStats();
    }

    /**
     * 학습 경로 캐시 설정
     */
    @Bean
    public Caffeine<Object, Object> learningPathsCacheConfig() {
        return Caffeine.newBuilder()
                .maximumSize(200) // 학습 경로는 더 적은 수로 제한
                .expireAfterWrite(2, TimeUnit.HOURS) // 2시간 후 만료
                .expireAfterAccess(1, TimeUnit.HOURS) // 1시간간 접근 없으면 만료
                .recordStats();
    }

    /**
     * 이력서 캐시 설정
     */
    @Bean
    public Caffeine<Object, Object> resumeCacheConfig() {
        return Caffeine.newBuilder()
                .maximumSize(1000) // 이력서는 더 많은 수로 설정
                .expireAfterWrite(30, TimeUnit.MINUTES) // 30분 후 만료
                .expireAfterAccess(15, TimeUnit.MINUTES) // 15분간 접근 없으면 만료
                .recordStats();
    }

    /**
     * AI 서비스 응답 캐시 설정
     */
    @Bean
    public Caffeine<Object, Object> aiServiceCacheConfig() {
        return Caffeine.newBuilder()
                .maximumSize(300) // AI 응답은 중간 수준으로 설정
                .expireAfterWrite(45, TimeUnit.MINUTES) // 45분 후 만료
                .expireAfterAccess(20, TimeUnit.MINUTES) // 20분간 접근 없으면 만료
                .recordStats();
    }
} 