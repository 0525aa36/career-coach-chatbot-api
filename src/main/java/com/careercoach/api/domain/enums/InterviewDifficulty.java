package com.careercoach.api.domain.enums;

/**
 * 면접 난이도 열거형
 * 지원자의 경력 수준에 따른 면접 난이도를 정의합니다.
 */
public enum InterviewDifficulty {
    JUNIOR("주니어"),
    MIDDLE("미들"),
    SENIOR("시니어");

    private final String displayName;

    InterviewDifficulty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 
 