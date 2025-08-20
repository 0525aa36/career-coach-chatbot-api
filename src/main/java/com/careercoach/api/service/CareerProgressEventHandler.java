package com.careercoach.api.service;

import com.careercoach.api.dto.response.ResumeDto;
import com.careercoach.api.event.ResumeUpdatedEvent;
import com.careercoach.api.event.InterviewCompletedEvent;
import com.careercoach.api.event.AIServiceCallEvent;
import com.careercoach.api.service.ai.MultiModelAIOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 커리어 진행 이벤트 핸들러
 * 이벤트 기반 비동기 처리를 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CareerProgressEventHandler {

    private final ResumeService resumeService;
    private final MultiModelAIOrchestrator aiOrchestrator;

    /**
     * 이력서 업데이트 이벤트 처리
     * 비동기적으로 새로운 학습 경로를 생성합니다.
     */
    @EventListener
    @Async("careerCoachTaskExecutor")
    public void handleResumeUpdatedEvent(ResumeUpdatedEvent event) {
        log.info("이력서 업데이트 이벤트 처리 시작 - 이력서 ID: {}", event.getResumeId());

        try {
            // 이력서 정보 조회
            ResumeDto resume = resumeService.getResume(event.getResumeId());
            
            // 새로운 학습 경로 생성
            aiOrchestrator.generateLearningPath(resume);
            
            log.info("이력서 업데이트 이벤트 처리 완료 - 이력서 ID: {}", event.getResumeId());

        } catch (Exception e) {
            log.error("이력서 업데이트 이벤트 처리 중 오류 발생 - 이력서 ID: {}, 오류: {}", 
                    event.getResumeId(), e.getMessage(), e);
        }
    }

    /**
     * 면접 완료 이벤트 처리
     * 면접 결과를 분석하고 피드백을 생성합니다.
     */
    @EventListener
    @Async("careerCoachTaskExecutor")
    public void handleInterviewCompletedEvent(InterviewCompletedEvent event) {
        log.info("면접 완료 이벤트 처리 시작 - 이력서 ID: {}, 난이도: {}", 
                event.getResumeId(), event.getDifficulty());

        try {
            // 면접 결과 분석
            analyzeInterviewResults(event);
            
            // 개선 제안 생성
            generateImprovementSuggestions(event);
            
            log.info("면접 완료 이벤트 처리 완료 - 이력서 ID: {}", event.getResumeId());

        } catch (Exception e) {
            log.error("면접 완료 이벤트 처리 중 오류 발생 - 이력서 ID: {}, 오류: {}", 
                    event.getResumeId(), e.getMessage(), e);
        }
    }

    /**
     * AI 서비스 호출 이벤트 처리
     * AI 서비스 성능을 모니터링합니다.
     */
    @EventListener
    @Async("aiServiceTaskExecutor")
    public void handleAIServiceCallEvent(AIServiceCallEvent event) {
        log.info("AI 서비스 호출 이벤트 처리 - 서비스: {}, 지속시간: {}ms, 상태: {}", 
                event.getServiceName(), event.getDuration(), event.getStatus());

        try {
            // 성능 메트릭 수집
            collectPerformanceMetrics(event);
            
            // 오류 발생 시 알림
            if ("ERROR".equals(event.getStatus())) {
                handleAIServiceError(event);
            }
            
            log.debug("AI 서비스 호출 이벤트 처리 완료");

        } catch (Exception e) {
            log.error("AI 서비스 호출 이벤트 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 면접 결과 분석
     */
    private void analyzeInterviewResults(InterviewCompletedEvent event) {
        log.info("면접 결과 분석 - 평균 점수: {:.2f}, 질문 수: {}", 
                event.getAverageScore(), event.getQuestionCount());

        // 점수에 따른 난이도 조정 제안
        if (event.getAverageScore() >= 8.0) {
            log.info("높은 성과로 인한 난이도 상향 조정 제안");
        } else if (event.getAverageScore() <= 4.0) {
            log.info("낮은 성과로 인한 난이도 하향 조정 제안");
        } else {
            log.info("적절한 성과로 현재 난이도 유지 제안");
        }
    }

    /**
     * 개선 제안 생성
     */
    private void generateImprovementSuggestions(InterviewCompletedEvent event) {
        log.info("개선 제안 생성 시작");

        // 점수에 따른 개선 제안
        if (event.getAverageScore() < 6.0) {
            log.info("기본 개념 학습 강화 제안");
        } else if (event.getAverageScore() < 8.0) {
            log.info("실무 적용 능력 향상 제안");
        } else {
            log.info("고급 기술 및 아키텍처 학습 제안");
        }
    }

    /**
     * 성능 메트릭 수집
     */
    private void collectPerformanceMetrics(AIServiceCallEvent event) {
        // 실제 구현에서는 Prometheus, Datadog 등의 모니터링 툴로 메트릭 전송
        log.info("AI Service Call: {}, Duration: {}ms, Status: {}", 
                event.getServiceName(), event.getDuration(), event.getStatus());
        
        // 성능 임계값 체크
        if (event.getDuration() > 5000) { // 5초 이상
            log.warn("AI 서비스 응답 시간이 길어짐 - 서비스: {}, 지속시간: {}ms", 
                    event.getServiceName(), event.getDuration());
        }
    }

    /**
     * AI 서비스 오류 처리
     */
    private void handleAIServiceError(AIServiceCallEvent event) {
        log.error("AI 서비스 오류 발생 - 서비스: {}, 오류: {}", 
                event.getServiceName(), event.getErrorMessage());
        
        // 실제 구현에서는 알림 시스템(슬랙, 이메일 등)으로 알림 전송
        // 예: SlackNotificationService.sendAlert("AI 서비스 오류", event.getServiceName(), event.getErrorMessage());
    }

    /**
     * 학습 경로 재생성
     * 이력서 업데이트 시 호출됩니다.
     */
    @Async("careerCoachTaskExecutor")
    public void regenerateLearningPath(Long resumeId) {
        log.info("학습 경로 재생성 시작 - 이력서 ID: {}", resumeId);

        try {
            ResumeDto resume = resumeService.getResume(resumeId);
            aiOrchestrator.generateLearningPath(resume);
            
            log.info("학습 경로 재생성 완료 - 이력서 ID: {}", resumeId);

        } catch (Exception e) {
            log.error("학습 경로 재생성 중 오류 발생 - 이력서 ID: {}, 오류: {}", 
                    resumeId, e.getMessage(), e);
        }
    }
} 