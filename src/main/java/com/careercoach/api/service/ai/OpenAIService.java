package com.careercoach.api.service.ai;

import com.careercoach.api.dto.response.ResumeDto;

/**
 * OpenAI 서비스 인터페이스
 * OpenAI 모델과의 연동을 위한 인터페이스입니다.
 */
public interface OpenAIService extends AIService {
    
    /**
     * 기술 스택 분석
     * 
     * @param resume 이력서 정보
     * @return 기술 강점/약점 분석 결과
     */
    String analyzeTechSkills(ResumeDto resume);
    
    /**
     * 코드 리뷰 생성
     * 
     * @param code 코드
     * @param language 프로그래밍 언어
     * @return 코드 리뷰 결과
     */
    String generateCodeReview(String code, String language);
} 