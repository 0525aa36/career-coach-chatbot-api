package com.careercoach.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 학습 경로 응답 DTO
 * AI가 생성한 개인 맞춤형 학습 경로를 반환하는 응답 객체입니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathResponse {

    private Long resumeId;
    private String jobRole;
    private String experienceLevel;
    private List<LearningStep> learningSteps;
    private String overallStrategy;
    private LocalDateTime generatedAt;
    private String estimatedDuration;

    /**
     * 학습 단계를 나타내는 내부 클래스
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LearningStep {
        private String title;
        private String description;
        private String difficulty;
        private String estimatedTime;
        private List<String> resources;
        private String learningObjective;
    }

    /**
     * 총 학습 단계 수를 반환하는 메서드
     * 
     * @return 학습 단계 수
     */
    public int getTotalSteps() {
        return learningSteps != null ? learningSteps.size() : 0;
    }

    /**
     * 난이도별 학습 단계 수를 반환하는 메서드
     * 
     * @param difficulty 난이도
     * @return 해당 난이도의 학습 단계 수
     */
    public long getStepsByDifficulty(String difficulty) {
        return learningSteps != null ? 
            learningSteps.stream()
                .filter(step -> step.getDifficulty().equals(difficulty))
                .count() : 0;
    }
} 