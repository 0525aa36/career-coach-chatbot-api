package com.careercoach.api.service.ai;

import com.careercoach.api.dto.response.LearningPathResponse;
import com.careercoach.api.dto.response.ResumeDto;
import com.careercoach.api.exception.AIServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 멀티 모델 AI 오케스트레이터
 * 여러 AI 모델을 조합하여 최적의 결과를 생성하는 서비스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MultiModelAIOrchestrator {

    private final OpenAIService openAIService;
    private final ClaudeService claudeService;
    private final ObjectMapper objectMapper;

    /**
     * 학습 경로 생성
     * 1차: OpenAI로 기술 강점/약점 분석
     * 2차: Claude로 학습 경로 초안 생성
     * 3차: 후처리로 개인화
     */
    public LearningPathResponse generateLearningPath(ResumeDto resume) {
        log.info("멀티 모델 학습 경로 생성 시작 - 이력서 ID: {}", resume.getId());

        try {
            // 1차 AI 모델: OpenAI로 기술 스택 분석
            String techAnalysis = openAIService.analyzeTechSkills(resume);
            log.debug("OpenAI 기술 분석 완료: {}", techAnalysis);

            // 2차 AI 모델: Claude로 학습 경로 초안 생성
            String learningPathDraft = claudeService.generateLearningPath(techAnalysis);
            log.debug("Claude 학습 경로 초안 완료: {}", learningPathDraft);

            // 3차: 후처리로 개인화
            LearningPathResponse personalizedPath = optimizeLearningPath(learningPathDraft, resume);

            log.info("멀티 모델 학습 경로 생성 완료 - 단계 수: {}", personalizedPath.getTotalSteps());

            return personalizedPath;

        } catch (Exception e) {
            log.error("멀티 모델 학습 경로 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new AIServiceException("MultiModelAIOrchestrator", "학습 경로 생성 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 학습 경로 최적화 및 개인화
     * AI가 생성한 초안을 사용자의 경력 수준에 맞게 후처리
     */
    private LearningPathResponse optimizeLearningPath(String learningPathDraft, ResumeDto resume) {
        try {
            // JSON 응답 파싱
            var pathMap = objectMapper.readValue(learningPathDraft, new TypeReference<Map<String, Object>>() {});

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rawSteps = (List<Map<String, Object>>) pathMap.get("learning_steps");
            String overallStrategy = (String) pathMap.get("overall_strategy");
            String estimatedDuration = (String) pathMap.get("estimated_duration");

            // 학습 단계 개인화
            List<LearningPathResponse.LearningStep> personalizedSteps = rawSteps.stream()
                    .map(step -> personalizeLearningStep(step, resume))
                    .filter(step -> isStepAppropriate(step, resume)) // 적절한 단계만 필터링
                    .toList();

            return LearningPathResponse.builder()
                    .resumeId(resume.getId())
                    .jobRole(resume.getJobRole().getDisplayName())
                    .experienceLevel(resume.getExperienceLevel())
                    .learningSteps(personalizedSteps)
                    .overallStrategy(overallStrategy)
                    .estimatedDuration(estimatedDuration)
                    .generatedAt(LocalDateTime.now())
                    .build();

        } catch (JsonProcessingException e) {
            log.error("학습 경로 파싱 실패: {}", e.getMessage());
            throw new AIServiceException("MultiModelAIOrchestrator", "학습 경로 파싱에 실패했습니다.", e);
        }
    }

    /**
     * 학습 단계 개인화
     */
    private LearningPathResponse.LearningStep personalizeLearningStep(Map<String, Object> rawStep, ResumeDto resume) {
        String title = (String) rawStep.get("title");
        String description = (String) rawStep.get("description");
        String difficulty = (String) rawStep.get("difficulty");
        String estimatedTime = (String) rawStep.get("estimated_time");
        String learningObjective = (String) rawStep.get("learning_objective");

        @SuppressWarnings("unchecked")
        List<String> resources = (List<String>) rawStep.get("resources");

        // 경력 수준에 따른 개인화
        String personalizedDescription = personalizeDescription(description, resume);
        String personalizedTime = adjustEstimatedTime(estimatedTime, resume);

        return LearningPathResponse.LearningStep.builder()
                .title(title)
                .description(personalizedDescription)
                .difficulty(difficulty)
                .estimatedTime(personalizedTime)
                .resources(resources)
                .learningObjective(learningObjective)
                .build();
    }

    /**
     * 설명 개인화
     */
    private String personalizeDescription(String description, ResumeDto resume) {
        if (description == null) return "";

        // 경력 수준에 따른 설명 조정
        if (resume.getExperienceYears() < 2) {
            // 주니어 개발자: 더 자세한 설명과 기초 개념 강조
            return description + " (기초 개념부터 차근차근 학습하세요)";
        } else if (resume.getExperienceYears() < 5) {
            // 미들 개발자: 실무 적용 중심
            return description + " (실무 프로젝트에 적용해보세요)";
        } else {
            // 시니어 개발자: 아키텍처 및 설계 관점
            return description + " (시스템 설계 관점에서 고려해보세요)";
        }
    }

    /**
     * 예상 학습 시간 조정
     */
    private String adjustEstimatedTime(String estimatedTime, ResumeDto resume) {
        if (estimatedTime == null) return "2-3주";

        // 경력에 따른 학습 시간 조정
        if (resume.getExperienceYears() < 2) {
            // 주니어: 더 많은 시간 필요
            return estimatedTime + " (추가 1-2주 권장)";
        } else if (resume.getExperienceYears() < 5) {
            // 미들: 기본 시간
            return estimatedTime;
        } else {
            // 시니어: 더 빠른 학습 가능
            return estimatedTime + " (집중 학습 시 단축 가능)";
        }
    }

    /**
     * 학습 단계 적절성 검증
     * 주니어에게 너무 어려운 강의 제외
     */
    private boolean isStepAppropriate(LearningPathResponse.LearningStep step, ResumeDto resume) {
        String difficulty = step.getDifficulty();
        String title = step.getTitle().toLowerCase();

        // 주니어 개발자 필터링
        if (resume.getExperienceYears() < 2) {
            // 주니어에게 부적절한 키워드들
            List<String> seniorKeywords = List.of(
                    "아키텍처", "설계", "시스템", "분산", "마이크로서비스", 
                    "성능 튜닝", "최적화", "고급", "심화", "전문가"
            );

            // 난이도가 높거나 시니어 키워드가 포함된 경우 제외
            if ("SENIOR".equals(difficulty) || "ADVANCED".equals(difficulty)) {
                return false;
            }

            // 제목에 시니어 키워드가 포함된 경우 제외
            return seniorKeywords.stream().noneMatch(title::contains);
        }

        // 미들/시니어는 모든 단계 허용
        return true;
    }

    /**
     * 기술 스택 분석 결과 조합
     */
    public String combineAnalysisResults(ResumeDto resume) {
        log.info("기술 스택 분석 결과 조합 시작");

        try {
            // OpenAI 분석
            String openAIAnalysis = openAIService.analyzeTechSkills(resume);
            
            // Claude 분석 (다른 관점)
            String claudeAnalysis = claudeService.analyzeDocument(resume.getCareerSummary());

            // 두 분석 결과 조합
            String combinedAnalysis = String.format("""
                    [OpenAI 분석]
                    %s
                    
                    [Claude 분석]
                    %s
                    
                    [종합 평가]
                    두 AI 모델의 분석을 종합한 결과, 지원자는 %s 분야에서 %s 수준의 역량을 보유하고 있습니다.
                    """, 
                    openAIAnalysis, 
                    claudeAnalysis,
                    resume.getJobRole().getDisplayName(),
                    resume.getExperienceLevel()
            );

            log.info("기술 스택 분석 결과 조합 완료");
            return combinedAnalysis;

        } catch (Exception e) {
            log.error("기술 스택 분석 결과 조합 중 오류 발생: {}", e.getMessage());
            throw new AIServiceException("MultiModelAIOrchestrator", "분석 결과 조합 중 오류가 발생했습니다.", e);
        }
    }
} 