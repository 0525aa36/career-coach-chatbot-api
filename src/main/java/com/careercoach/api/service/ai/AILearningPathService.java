package com.careercoach.api.service.ai;

import com.careercoach.api.dto.response.LearningPathResponse;
import com.careercoach.api.dto.response.ResumeDto;
import com.careercoach.api.exception.AIServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AI 학습 경로 생성 서비스
 * 이력서 정보를 바탕으로 개인 맞춤형 학습 경로를 생성합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AILearningPathService {

    private final AIService aiService;
    private final ObjectMapper objectMapper;
    private final KnowledgeBaseService knowledgeBaseService;

    // 학습 경로 생성 프롬프트 템플릿
    private static final String LEARNING_PATH_PROMPT_TEMPLATE = """
            당신은 %s 분야의 전문 커리어 코치입니다.
            지원자의 현재 상황을 바탕으로 맞춤형 학습 경로를 제시해주세요.
            
            [지원자 정보]
            - 직무: %s
            - 경력: %d년 (%s)
            - 경력 요약: %s
            - 프로젝트 경험: %s
            - 기술 스택: %s
            
            [학습 경로 생성 가이드라인]
            1. 지원자의 현재 수준에서 다음 단계로 발전할 수 있는 실현 가능한 학습 경로를 제시하세요.
            2. 각 학습 단계는 구체적이고 명확한 목표를 가져야 합니다.
            3. 학습 목표, 예상 소요 시간, 추천 리소스를 포함하세요.
            4. 최소 3개, 최대 5개의 학습 단계를 제시하세요.
            5. 지원자의 기술 스택과 프로젝트 경험을 고려하여 개인화된 학습 경로를 제시하세요.
            6. 각 단계는 이전 단계를 기반으로 한 논리적인 순서로 구성하세요.
            
            [학습 단계 구성 요소]
            - title: 명확하고 구체적인 학습 단계 제목
            - description: 해당 단계에서 학습할 내용의 상세 설명
            - difficulty: BEGINNER, INTERMEDIATE, ADVANCED 중 선택
            - estimatedTime: 실제 가능한 예상 소요 시간 (예: "2주", "1개월")
            - resources: 구체적인 학습 리소스 (책, 강의, 실습 프로젝트 등)
            - learningObjective: 해당 단계를 완료했을 때 달성할 수 있는 구체적인 목표
            
            반드시 JSON 형식으로만 응답해주세요. 다른 설명이나 텍스트는 포함하지 마세요.
            
            응답 형식:
            {
                "jobRole": "%s",
                "experienceLevel": "%s",
                "learningSteps": [
                    {
                        "title": "학습 단계 제목",
                        "description": "상세 설명",
                        "difficulty": "BEGINNER",
                        "estimatedTime": "2주",
                        "resources": ["추천 리소스1", "추천 리소스2"],
                        "learningObjective": "학습 목표"
                    }
                ],
                "overallStrategy": "전체 학습 전략 (100자 이내)",
                "estimatedDuration": "전체 예상 기간"
            }
            """;

    /**
     * 맞춤형 학습 경로 생성
     */
    @Cacheable(value = "learning-paths", key = "#resume.hashCode()")
    public LearningPathResponse generateLearningPath(ResumeDto resume) {
        log.info("학습 경로 생성 시작 - 이력서 ID: {}, 직무: {}", resume.getId(), resume.getJobRole());

        try {
            // 기본 프롬프트 생성
            String basePrompt = createBasePrompt(resume);
            
            // RAG를 통한 컨텍스트 강화
            String enhancedPrompt = knowledgeBaseService.enhancePromptWithContext(basePrompt, resume.getJobRole().name());
            
            // AI 서비스 호출
            String aiResponse = callAIService(enhancedPrompt);
            
            // 응답 파싱 및 변환
            LearningPathResponse response = parseAIResponse(aiResponse, resume);
            
            log.info("학습 경로 생성 완료 - 단계 수: {}", response.getTotalSteps());
            
            return response;
            
        } catch (Exception e) {
            log.error("학습 경로 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new AIServiceException("AILearningPathService", "학습 경로 생성 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 기본 프롬프트 생성
     */
    private String createBasePrompt(ResumeDto resume) {
        String projectExp = resume.getProjectExperience() != null ? resume.getProjectExperience() : "없음";
        String techSkills = resume.getTechSkills() != null ? String.join(", ", resume.getTechSkills()) : "없음";
        
        return String.format(LEARNING_PATH_PROMPT_TEMPLATE,
                resume.getJobRole().getDisplayName(),
                resume.getJobRole().getDisplayName(),
                resume.getExperienceYears(),
                resume.getExperienceLevel(),
                resume.getCareerSummary(),
                projectExp,
                techSkills,
                resume.getJobRole().getDisplayName(),
                resume.getExperienceLevel()
        );
    }

    /**
     * AI 서비스 호출
     */
    private String callAIService(String prompt) {
        log.debug("AI 서비스 호출 - 프롬프트 길이: {} 문자", prompt.length());
        
        try {
            // 실제 Gemini API 호출
            return aiService.callAI(prompt);
        } catch (Exception e) {
            log.error("AI 서비스 호출 실패, 목업 데이터 반환: {}", e.getMessage());
            // AI 서비스 실패 시 목업 데이터 반환
            return generateMockResponse(prompt);
        }
    }

    /**
     * 목업 응답 생성 (실제 AI 서비스 연동 전 테스트용)
     */
    private String generateMockResponse(String prompt) {
        if (prompt.contains("백엔드")) {
            return """
                    {
                        "jobRole": "백엔드 개발자",
                        "experienceLevel": "미들급",
                        "learningSteps": [
                            {
                                "title": "Spring Boot 심화 학습",
                                "description": "Spring Boot의 고급 기능들을 학습합니다.",
                                "difficulty": "INTERMEDIATE",
                                "estimatedTime": "4주",
                                "resources": ["Spring Boot 공식 문서", "인프런 강의", "실습 프로젝트"],
                                "learningObjective": "Spring Boot 고급 기능 마스터"
                            },
                            {
                                "title": "마이크로서비스 아키텍처",
                                "description": "마이크로서비스 설계와 구현을 학습합니다.",
                                "difficulty": "ADVANCED",
                                "estimatedTime": "6주",
                                "resources": ["마이크로서비스 패턴", "Docker & Kubernetes", "실무 프로젝트"],
                                "learningObjective": "마이크로서비스 아키텍처 설계 능력 향상"
                            },
                            {
                                "title": "데이터베이스 최적화",
                                "description": "성능 튜닝과 최적화 기법을 학습합니다.",
                                "difficulty": "INTERMEDIATE",
                                "estimatedTime": "3주",
                                "resources": ["SQL 튜닝 가이드", "인덱스 설계", "실제 성능 측정"],
                                "learningObjective": "데이터베이스 성능 최적화 능력 향상"
                            }
                        ],
                        "overallStrategy": "단계별 실무 중심 학습을 통한 백엔드 개발자 역량 강화",
                        "estimatedDuration": "3개월"
                    }
                    """;
        } else if (prompt.contains("프론트엔드")) {
            return """
                    {
                        "jobRole": "프론트엔드 개발자",
                        "experienceLevel": "미들급",
                        "learningSteps": [
                            {
                                "title": "React 고급 패턴",
                                "description": "React의 고급 패턴과 최적화 기법을 학습합니다.",
                                "difficulty": "INTERMEDIATE",
                                "estimatedTime": "4주",
                                "resources": ["React 공식 문서", "고급 패턴 가이드", "성능 최적화"],
                                "learningObjective": "React 고급 패턴 마스터"
                            },
                            {
                                "title": "상태 관리 심화",
                                "description": "복잡한 상태 관리 패턴을 학습합니다.",
                                "difficulty": "ADVANCED",
                                "estimatedTime": "3주",
                                "resources": ["Redux Toolkit", "Zustand", "실무 사례"],
                                "learningObjective": "효율적인 상태 관리 설계 능력 향상"
                            }
                        ],
                        "overallStrategy": "현대적인 프론트엔드 개발 역량 강화",
                        "estimatedDuration": "2개월"
                    }
                    """;
        } else {
            return """
                    {
                        "jobRole": "개발자",
                        "experienceLevel": "주니어",
                        "learningSteps": [
                            {
                                "title": "기본 개발 역량 강화",
                                "description": "프로그래밍 기본기를 다집니다.",
                                "difficulty": "BEGINNER",
                                "estimatedTime": "6주",
                                "resources": ["프로그래밍 기초", "알고리즘 문제 풀이", "코딩 테스트"],
                                "learningObjective": "기본 프로그래밍 역량 향상"
                            }
                        ],
                        "overallStrategy": "기본기를 바탕으로 한 단계적 성장",
                        "estimatedDuration": "2개월"
                    }
                    """;
        }
    }

    /**
     * 기본 응답 생성 (오류 발생 시)
     */
    private LearningPathResponse createDefaultResponse(ResumeDto resume) {
        return LearningPathResponse.builder()
                .resumeId(resume.getId())
                .jobRole(resume.getJobRole().getDisplayName())
                .experienceLevel(resume.getExperienceLevel())
                .learningSteps(List.of(
                    LearningPathResponse.LearningStep.builder()
                            .title("기본 개발 역량 강화")
                            .description("프로그래밍 기본기를 다집니다.")
                            .difficulty("BEGINNER")
                            .estimatedTime("6주")
                            .resources(List.of("프로그래밍 기초", "알고리즘 문제 풀이", "코딩 테스트"))
                            .learningObjective("기본 프로그래밍 역량 향상")
                            .build()
                ))
                .overallStrategy("기본기를 바탕으로 한 단계적 성장")
                .estimatedDuration("2개월")
                .generatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * AI 응답 파싱 및 변환
     */
    private LearningPathResponse parseAIResponse(String aiResponse, ResumeDto resume) {
        try {
            // 코드 블록 제거 (```json ... ```)
            String cleanedResponse = aiResponse;
            if (cleanedResponse.contains("```json")) {
                cleanedResponse = cleanedResponse.replaceAll("```json\\s*", "");
                cleanedResponse = cleanedResponse.replaceAll("```\\s*$", "");
            }
            
            log.debug("정리된 AI 응답: {}", cleanedResponse);
            
            // JSON 응답을 파싱
            var responseMap = objectMapper.readValue(cleanedResponse, new TypeReference<Map<String, Object>>() {});
            
            String jobRole = (String) responseMap.get("jobRole");
            String experienceLevel = (String) responseMap.get("experienceLevel");
            String overallStrategy = (String) responseMap.get("overallStrategy");
            String estimatedDuration = (String) responseMap.get("estimatedDuration");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> stepsData = (List<Map<String, Object>>) responseMap.get("learningSteps");
            
            List<LearningPathResponse.LearningStep> learningSteps = stepsData.stream()
                    .map(step -> LearningPathResponse.LearningStep.builder()
                            .title((String) step.get("title"))
                            .description((String) step.get("description"))
                            .difficulty((String) step.get("difficulty"))
                            .estimatedTime((String) step.get("estimatedTime"))
                            .resources((List<String>) step.get("resources"))
                            .learningObjective((String) step.get("learningObjective"))
                            .build())
                    .toList();
            
            return LearningPathResponse.builder()
                    .resumeId(resume.getId())
                    .jobRole(jobRole)
                    .experienceLevel(experienceLevel)
                    .learningSteps(learningSteps)
                    .overallStrategy(overallStrategy)
                    .estimatedDuration(estimatedDuration)
                    .generatedAt(LocalDateTime.now())
                    .build();
                    
        } catch (JsonProcessingException e) {
            log.error("AI 응답 파싱 실패: {}", e.getMessage());
            log.error("원본 AI 응답: {}", aiResponse);
            throw new AIServiceException("AILearningPathService", "AI 응답 파싱에 실패했습니다.", e);
        }
    }
} 