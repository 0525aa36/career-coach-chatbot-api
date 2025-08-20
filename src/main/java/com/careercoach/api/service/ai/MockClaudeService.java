package com.careercoach.api.service.ai;

import org.springframework.stereotype.Service;

/**
 * Claude 서비스 목업 구현체
 * 실제 Claude API 연동 전 테스트용 구현체입니다.
 */
@Service
public class MockClaudeService implements ClaudeService {

    @Override
    public String callAI(String prompt) {
        return "Mock Claude response: " + prompt.substring(0, Math.min(prompt.length(), 50)) + "...";
    }

    @Override
    public String getServiceName() {
        return "MockClaude";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String generateLearningPath(String analysis) {
        return """
                {
                    "learning_steps": [
                        {
                            "title": "기본 개념 학습",
                            "description": "핵심 개념을 이해합니다",
                            "difficulty": "BEGINNER",
                            "estimated_time": "2주",
                            "resources": ["온라인 강의", "책"],
                            "learning_objective": "기본 개념 습득"
                        }
                    ],
                    "overall_strategy": "단계별 학습 전략",
                    "estimated_duration": "3개월"
                }
                """;
    }

    @Override
    public String analyzeDocument(String document) {
        return String.format("Mock document analysis: %s", 
                document.substring(0, Math.min(document.length(), 100)) + "...");
    }
} 