package com.careercoach.api.exception;

/**
 * AI 서비스 호출 실패 예외
 * AI 서비스 호출 중 오류가 발생할 경우 사용됩니다.
 */
public class AIServiceException extends RuntimeException {

    private final String serviceName;
    private final String errorCode;

    public AIServiceException(String serviceName, String message) {
        super(String.format("AI 서비스 호출 실패 - 서비스: %s, 오류: %s", serviceName, message));
        this.serviceName = serviceName;
        this.errorCode = null;
    }

    public AIServiceException(String serviceName, String errorCode, String message) {
        super(String.format("AI 서비스 호출 실패 - 서비스: %s, 코드: %s, 오류: %s", serviceName, errorCode, message));
        this.serviceName = serviceName;
        this.errorCode = errorCode;
    }

    public AIServiceException(String serviceName, String message, Throwable cause) {
        super(String.format("AI 서비스 호출 실패 - 서비스: %s, 오류: %s", serviceName, message), cause);
        this.serviceName = serviceName;
        this.errorCode = null;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getErrorCode() {
        return errorCode;
    }
} 