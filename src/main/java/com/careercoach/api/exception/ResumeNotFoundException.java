package com.careercoach.api.exception;

/**
 * 이력서를 찾을 수 없을 때 발생하는 예외
 * 요청한 ID의 이력서가 존재하지 않을 경우 발생합니다.
 */
public class ResumeNotFoundException extends RuntimeException {

    private final Long resumeId;

    public ResumeNotFoundException(Long resumeId) {
        super(String.format("이력서를 찾을 수 없습니다. ID: %d", resumeId));
        this.resumeId = resumeId;
    }

    public ResumeNotFoundException(String message) {
        super(message);
        this.resumeId = null;
    }

    public ResumeNotFoundException(String message, Throwable cause) {
        super(message, cause);
        this.resumeId = null;
    }

    public Long getResumeId() {
        return resumeId;
    }
} 