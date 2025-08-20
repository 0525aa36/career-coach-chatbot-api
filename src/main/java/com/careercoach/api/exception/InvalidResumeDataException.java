package com.careercoach.api.exception;

/**
 * 이력서 데이터 검증 실패 예외
 * 이력서 생성/수정 시 데이터 검증에 실패할 경우 발생합니다.
 */
public class InvalidResumeDataException extends RuntimeException {

    private final String field;
    private final String reason;

    public InvalidResumeDataException(String message) {
        super(message);
        this.field = null;
        this.reason = null;
    }

    public InvalidResumeDataException(String field, String reason) {
        super(String.format("이력서 데이터 검증 실패 - 필드: %s, 사유: %s", field, reason));
        this.field = field;
        this.reason = reason;
    }

    public InvalidResumeDataException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.reason = null;
    }

    public String getField() {
        return field;
    }

    public String getReason() {
        return reason;
    }
} 