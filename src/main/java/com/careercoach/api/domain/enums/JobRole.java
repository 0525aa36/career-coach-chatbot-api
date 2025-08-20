package com.careercoach.api.domain.enums;

/**
 * 직무 역할 열거형
 * 지원자의 직무 분야를 정의합니다.
 */
public enum JobRole {
    BACKEND_DEVELOPER("백엔드 개발자"),
    FRONTEND_DEVELOPER("프론트엔드 개발자"),
    FULLSTACK_DEVELOPER("풀스택 개발자"),
    DEVOPS_ENGINEER("DevOps 엔지니어"),
    DATA_ENGINEER("데이터 엔지니어"),
    DATA_SCIENTIST("데이터 사이언티스트"),
    ML_ENGINEER("머신러닝 엔지니어"),
    AI_ENGINEER("AI 엔지니어"),
    SYSTEM_ARCHITECT("시스템 아키텍트"),
    PRODUCT_MANAGER("프로덕트 매니저"),
    QA_ENGINEER("QA 엔지니어"),
    SECURITY_ENGINEER("보안 엔지니어");

    private final String displayName;

    JobRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 