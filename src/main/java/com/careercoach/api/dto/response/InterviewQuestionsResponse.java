package com.careercoach.api.dto.response;

import com.careercoach.api.domain.enums.InterviewDifficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 면접 질문 응답 DTO
 * AI가 생성한 맞춤형 면접 질문을 반환하는 응답 객체입니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestionsResponse {

    private Long resumeId;
    private InterviewDifficulty difficulty;
    private List<String> questions;
    private String analysis;
    private LocalDateTime generatedAt;
    private String promptUsed;

    /**
     * 생성된 질문 수를 반환하는 메서드
     * 
     * @return 질문 수
     */
    public int getQuestionCount() {
        return questions != null ? questions.size() : 0;
    }

    /**
     * 난이도별 설명을 반환하는 메서드
     * 
     * @return 난이도 설명
     */
    public String getDifficultyDescription() {
        return switch (difficulty) {
            case JUNIOR -> "주니어 개발자 수준의 기본적인 기술 질문들";
            case MIDDLE -> "미들급 개발자 수준의 실무 중심 질문들";
            case SENIOR -> "시니어 개발자 수준의 아키텍처 및 설계 질문들";
        };
    }
} 