package com.careercoach.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 설정 클래스
 * JPA Auditing 기능을 활성화합니다.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
} 