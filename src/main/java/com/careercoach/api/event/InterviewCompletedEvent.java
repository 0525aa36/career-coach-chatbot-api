package com.careercoach.api.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * 면접 완료 이벤트
 * 면접이 완료될 때 발생하는 이벤트입니다.
 */
@Getter
public class InterviewCompletedEvent extends ApplicationEvent {

    private final Long resumeId;
    private final String difficulty;
    private final int questionCount;
    private final double averageScore;
    private final LocalDateTime completedAt;

    public InterviewCompletedEvent(Object source, Long resumeId, String difficulty, 
                                 int questionCount, double averageScore) {
        super(source);
        this.resumeId = resumeId;
        this.difficulty = difficulty;
        this.questionCount = questionCount;
        this.averageScore = averageScore;
        this.completedAt = LocalDateTime.now();
    }
} 