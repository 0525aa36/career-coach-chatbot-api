package com.careercoach.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * AI 서비스 호출 이벤트
 * AI 서비스 호출 시 발생하는 이벤트입니다.
 */
@Getter
public class AIServiceCallEvent extends ApplicationEvent {

    private final String serviceName;
    private final long duration;
    private final String status;
    private final String errorMessage;
    private final LocalDateTime calledAt;

    public AIServiceCallEvent(Object source, String serviceName, long duration, String status) {
        super(source);
        this.serviceName = serviceName;
        this.duration = duration;
        this.status = status;
        this.errorMessage = null;
        this.calledAt = LocalDateTime.now();
    }

    public AIServiceCallEvent(Object source, String serviceName, long duration, String status, String errorMessage) {
        super(source);
        this.serviceName = serviceName;
        this.duration = duration;
        this.status = status;
        this.errorMessage = errorMessage;
        this.calledAt = LocalDateTime.now();
    }
} 