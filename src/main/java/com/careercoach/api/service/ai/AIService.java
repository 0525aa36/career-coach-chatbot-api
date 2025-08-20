package com.careercoach.api.service.ai;

/**
 * AI 서비스 기본 인터페이스
 * 다양한 AI 모델과의 연동을 위한 공통 인터페이스입니다.
 */
public interface AIService {
    
    /**
     * AI 서비스 호출
     * 
     * @param prompt AI에게 전달할 프롬프트
     * @return AI 응답
     */
    String callAI(String prompt);
    
    /**
     * AI 서비스명 반환
     * 
     * @return 서비스명
     */
    String getServiceName();
    
    /**
     * AI 서비스 상태 확인
     * 
     * @return 서비스 상태
     */
    boolean isAvailable();
} 