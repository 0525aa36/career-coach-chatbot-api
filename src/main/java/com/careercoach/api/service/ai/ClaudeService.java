package com.careercoach.api.service.ai;

/**
 * Claude 서비스 인터페이스
 * Claude 모델과의 연동을 위한 인터페이스입니다.
 */
public interface ClaudeService extends AIService {
    
    /**
     * 학습 경로 생성
     * 
     * @param analysis 기술 분석 결과
     * @return 학습 경로 초안
     */
    String generateLearningPath(String analysis);
    
    /**
     * 문서 분석
     * 
     * @param document 분석할 문서
     * @return 분석 결과
     */
    String analyzeDocument(String document);
} 