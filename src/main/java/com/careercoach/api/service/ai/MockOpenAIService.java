package com.careercoach.api.service.ai;

import com.careercoach.api.dto.response.ResumeDto;
import org.springframework.stereotype.Service;

/**
 * OpenAI 서비스 목업 구현체
 * 실제 OpenAI API 연동 전 테스트용 구현체입니다.
 */
@Service
public class MockOpenAIService implements OpenAIService {

    @Override
    public String callAI(String prompt) {
        return "Mock OpenAI response: " + prompt.substring(0, Math.min(prompt.length(), 50)) + "...";
    }

    @Override
    public String getServiceName() {
        return "MockOpenAI";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String analyzeTechSkills(ResumeDto resume) {
        return String.format("Mock tech analysis for %s with %d years experience", 
                resume.getJobRole().getDisplayName(), resume.getExperienceYears());
    }

    @Override
    public String generateCodeReview(String code, String language) {
        return String.format("Mock code review for %s: %s", language, 
                code.substring(0, Math.min(code.length(), 100)) + "...");
    }
} 