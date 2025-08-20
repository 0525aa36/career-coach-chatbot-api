package com.careercoach.api.config;

import org.springframework.context.annotation.Configuration;

/**
 * Gemini API 설정
 * Google AI의 Gemini 모델을 사용하기 위한 설정입니다.
 * RestTemplate을 통해 HTTP API로 직접 호출합니다.
 */
@Configuration
public class GeminiConfig {
    // RestTemplate은 CorsConfig에서 Bean으로 등록되어 있습니다.
} 