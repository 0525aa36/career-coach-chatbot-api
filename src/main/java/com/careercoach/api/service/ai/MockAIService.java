package com.careercoach.api.service.ai;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * AIService 목업 구현체
 * 실제 AI 서비스 연동 전 테스트용 구현체입니다.
 */
@Service
public class MockAIService implements AIService {

    @Override
    public String callAI(String prompt) {
        // 목업 응답 반환 - 프롬프트에 따라 다른 응답 생성
        if (prompt.contains("인터뷰")) {
            return """
                {
                    "questions": [
                        {
                            "question": "Spring Boot에서 의존성 주입(Dependency Injection)의 장점에 대해 설명해주세요.",
                            "difficulty": "INTERMEDIATE",
                            "category": "BACKEND"
                        },
                        {
                            "question": "JPA와 MyBatis의 차이점을 설명하고, 각각 언제 사용하는 것이 좋은지 알려주세요.",
                            "difficulty": "INTERMEDIATE",
                            "category": "BACKEND"
                        },
                        {
                            "question": "RESTful API 설계 원칙에 대해 설명하고, 실제 프로젝트에서 어떻게 적용했는지 예시를 들어주세요.",
                            "difficulty": "INTERMEDIATE",
                            "category": "BACKEND"
                        }
                    ]
                }
                """;
        } else if (prompt.contains("학습")) {
            return """
                {
                    "learningSteps": [
                        {
                            "step": 1,
                            "title": "Spring Boot 기초 학습",
                            "description": "Spring Boot의 핵심 개념과 기본 구조를 이해합니다.",
                            "resources": ["Spring Boot 공식 문서", "인프런 Spring Boot 강의"],
                            "estimatedTime": "2주"
                        },
                        {
                            "step": 2,
                            "title": "JPA와 데이터베이스 연동",
                            "description": "JPA를 사용한 데이터베이스 연동 방법을 학습합니다.",
                            "resources": ["JPA 프로그래밍 책", "실습 프로젝트"],
                            "estimatedTime": "3주"
                        },
                        {
                            "step": 3,
                            "title": "RESTful API 설계 및 구현",
                            "description": "RESTful API 설계 원칙을 학습하고 실제 API를 구현합니다.",
                            "resources": ["REST API 설계 가이드", "Postman을 이용한 API 테스트"],
                            "estimatedTime": "2주"
                        }
                    ]
                }
                """;
        } else {
            return "This is a mock AI response for testing purposes.";
        }
    }

    @Override
    public String getServiceName() {
        return "MockAIService";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
} 