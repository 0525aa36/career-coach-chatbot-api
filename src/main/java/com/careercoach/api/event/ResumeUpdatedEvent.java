package com.careercoach.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * 이력서 업데이트 이벤트
 * 이력서가 업데이트될 때 발생하는 이벤트입니다.
 */
@Getter
public class ResumeUpdatedEvent extends ApplicationEvent {

    private final Long resumeId;
    private final String jobRole;
    private final Integer experienceYears;
    private final LocalDateTime updatedAt;

    public ResumeUpdatedEvent(Object source, Long resumeId, String jobRole, Integer experienceYears) {
        super(source);
        this.resumeId = resumeId;
        this.jobRole = jobRole;
        this.experienceYears = experienceYears;
        this.updatedAt = LocalDateTime.now();
    }
} 