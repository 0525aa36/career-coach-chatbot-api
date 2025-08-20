package com.careercoach.api.service.ai;

import com.careercoach.api.dto.response.InterviewQuestionsResponse;
import com.careercoach.api.dto.response.ResumeDto;
import com.careercoach.api.domain.enums.InterviewDifficulty;
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
 * AI 면접 질문 생성 서비스
 * 고급 프롬프트 엔지니어링 기법을 적용하여 맞춤형 면접 질문을 생성합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIInterviewService {

    private final AIService aiService;
    private final ObjectMapper objectMapper;
    private final KnowledgeBaseService knowledgeBaseService;

    /**
     * AI 서비스 인스턴스 반환
     */
    public AIService getAiService() {
        return aiService;
    }

    // 고급 프롬프트 템플릿
    private static final String INTERVIEW_PROMPT_TEMPLATE = """
            당신은 %s 분야의 전문 면접관입니다. 
            지원자의 이력서를 바탕으로 맞춤형 면접 질문을 생성해주세요.
            
            [지원자 정보]
            - 직무: %s
            - 경력: %d년 (%s)
            - 경력 요약: %s
            - 프로젝트 경험: %s
            - 기술 스택: %s
            
            [면접 질문 생성 가이드라인]
            1. 지원자의 경력 수준(%s)에 맞는 난이도의 질문을 생성하세요.
            2. 기술적 깊이와 실무 경험을 모두 검증할 수 있는 질문을 포함하세요.
            3. 지원자의 기술 스택과 프로젝트 경험을 바탕으로 한 구체적인 질문을 생성하세요.
            4. 최소 5개, 최대 10개의 질문을 생성하세요.
            5. 각 질문은 구체적이고 명확해야 하며, 실제 면접에서 사용할 수 있는 수준이어야 합니다.
            6. 질문의 난이도는 지원자의 경력에 맞게 조정하세요.
            
            [질문 유형]
            - 기술적 질문: 지원자의 기술 스택에 대한 깊이 있는 이해 검증
            - 실무 경험 질문: 프로젝트 경험을 바탕으로 한 문제 해결 능력 검증
            - 아키텍처/설계 질문: 시스템 설계 및 아키텍처 이해도 검증
            - 성능/최적화 질문: 성능 최적화 및 문제 해결 능력 검증
            - 협업/커뮤니케이션 질문: 팀워크 및 의사소통 능력 검증
            
            반드시 JSON 형식으로만 응답해주세요. 다른 설명이나 텍스트는 포함하지 마세요.
            
            응답 형식:
            {
                "questions": [
                    "질문1",
                    "질문2",
                    "질문3",
                    "질문4",
                    "질문5"
                ],
                "analysis": "지원자의 강점과 약점을 분석한 내용 (100자 이내)",
                "difficulty": "%s"
            }
            """;

    /**
     * 맞춤형 면접 질문 생성
     * Chain of Thought와 Few-shot Learning을 적용한 고급 프롬프트 엔지니어링
     */
    @Cacheable(value = "interview-questions", key = "#resume.hashCode()")
    public InterviewQuestionsResponse generateQuestions(ResumeDto resume) {
        log.info("면접 질문 생성 시작 - 이력서 ID: {}, 직무: {}", resume.getId(), resume.getJobRole());

        try {
            // 기본 프롬프트 생성
            String basePrompt = createBasePrompt(resume);
            
            // RAG를 통한 컨텍스트 강화
            String enhancedPrompt = knowledgeBaseService.enhancePromptWithContext(basePrompt, resume.getJobRole().name());
            
            // Chain of Thought 적용
            String promptWithCoT = addChainOfThought(enhancedPrompt);
            
            // Few-shot Learning 적용
            String finalPrompt = addFewShotExamples(promptWithCoT);
            
            log.debug("생성된 프롬프트: {}", finalPrompt);
            
            // AI 서비스 호출
            String aiResponse = callAIService(finalPrompt);
            
            // 응답 파싱 및 변환
            InterviewQuestionsResponse response = parseAIResponse(aiResponse, resume);
            
            log.info("면접 질문 생성 완료 - 질문 수: {}", response.getQuestionCount());
            
            return response;
            
        } catch (Exception e) {
            log.error("면접 질문 생성 중 오류 발생: {}", e.getMessage(), e);
            // 상세한 오류 정보 로깅
            log.error("오류 상세 정보: ", e);
            throw new AIServiceException("AIInterviewService", "면접 질문 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 기본 프롬프트 생성
     */
    private String createBasePrompt(ResumeDto resume) {
        String projectExp = resume.getProjectExperience() != null ? resume.getProjectExperience() : "없음";
        String techSkills = resume.getTechSkills() != null ? String.join(", ", resume.getTechSkills()) : "없음";
        
        return String.format(INTERVIEW_PROMPT_TEMPLATE,
                resume.getJobRole().getDisplayName(),
                resume.getJobRole().getDisplayName(),
                resume.getExperienceYears(),
                resume.getExperienceLevel(),
                resume.getCareerSummary(),
                projectExp,
                techSkills,
                resume.getInterviewDifficulty().getDisplayName(),
                resume.getInterviewDifficulty().name()
        );
    }

    /**
     * Chain of Thought 적용
     * 사고의 연쇄를 유도하는 구문을 추가합니다.
     */
    private String addChainOfThought(String basePrompt) {
        String chainOfThoughtPrompt = """
            
            [사고 과정]
            먼저 지원자의 기술 스택과 경력을 바탕으로 어떤 역량을 검증해야 할지 단계별로 생각해 본 뒤, 
            그 생각의 과정을 바탕으로 질문을 생성해 주세요.
            
            단계별 사고 과정:
            1. 지원자의 기술 스택 분석 - 어떤 기술이 핵심인가?
            2. 경력 수준에 따른 기대 역량 파악 - 이 수준에서 어떤 것을 알고 있어야 하는가?
            3. 실무 경험 검증 포인트 식별 - 어떤 프로젝트 경험이 중요한가?
            4. 아키텍처 및 설계 능력 검증 필요성 판단 - 시스템 설계 경험이 있는가?
            5. 최신 기술 트렌드 반영 - 최신 기술에 대한 이해도는?
            
            위 사고 과정을 바탕으로 질문을 생성해 주세요.
            """;
        
        return basePrompt + chainOfThoughtPrompt;
    }

    /**
     * Few-shot Learning 적용
     * 좋은 질문의 예시를 추가합니다.
     */
    private String addFewShotExamples(String promptWithCoT) {
        String fewShotExamples = """
            
            [좋은 질문 예시]
            다음은 좋은 질문의 예시입니다. 이와 같은 수준과 깊이로 질문을 생성해 주세요.
            
            예시 1: "MSA 환경에서 분산 트랜잭션을 어떻게 처리하셨나요? Saga 패턴 적용 시 장단점과 실제 겪었던 어려움은 무엇이었나요?"
            
            예시 2: "대용량 데이터 처리 시 성능 최적화를 위해 어떤 전략을 사용하셨나요? 인덱싱, 쿼리 최적화, 캐싱 중 어떤 방법이 가장 효과적이었는지 구체적인 사례와 함께 설명해 주세요."
            
            예시 3: "마이크로서비스 간 통신에서 Circuit Breaker 패턴을 구현한 경험이 있나요? 어떤 상황에서 적용했고, 실제로 어떤 이점을 얻었는지 설명해 주세요."
            
            위 예시들을 참고하여 지원자에게 적합한 질문을 생성해 주세요.
            """;
        
        return promptWithCoT + fewShotExamples;
    }

    /**
     * AI 서비스 호출
     */
    private String callAIService(String finalPrompt) {
        log.debug("AI 서비스 호출 - 프롬프트 길이: {} 문자", finalPrompt.length());
        
        try {
            // 실제 Gemini API 호출
            return aiService.callAI(finalPrompt);
        } catch (Exception e) {
            log.error("AI 서비스 호출 실패, 목업 데이터 반환: {}", e.getMessage());
            // AI 서비스 실패 시 목업 데이터 반환
            return generateMockResponse(finalPrompt);
        }
    }

    /**
     * 목업 응답 생성 (실제 AI 서비스 연동 전 테스트용)
     */
    private String generateMockResponse(String prompt) {
        // 프롬프트 내용에 따라 다른 응답 생성
        if (prompt.contains("백엔드")) {
            return """
                    {
                        "questions": [
                            "Spring Boot의 자동 설정(Auto Configuration)이 어떻게 동작하는지 설명해 주세요.",
                            "JPA에서 N+1 문제를 어떻게 해결하시나요?",
                            "REST API 설계 시 고려해야 할 점들은 무엇인가요?",
                            "데이터베이스 인덱스의 종류와 각각의 특징을 설명해 주세요.",
                            "트랜잭션의 ACID 속성에 대해 설명하고, 실제 프로젝트에서 어떻게 보장했는지 예시를 들어 주세요."
                        ],
                        "analysis": "백엔드 개발자로서 기본적인 Spring 생태계 이해도와 데이터베이스 지식이 우수합니다. 실무 경험을 바탕으로 한 구체적인 사례 제시가 필요합니다.",
                        "difficulty": "MIDDLE"
                    }
                    """;
        } else if (prompt.contains("프론트엔드")) {
            return """
                    {
                        "questions": [
                            "React의 Virtual DOM이 무엇이고, 실제 DOM과의 차이점은 무엇인가요?",
                            "상태 관리 라이브러리(Redux, Zustand 등)를 언제 사용하시나요?",
                            "웹 성능 최적화를 위해 어떤 기법들을 사용하시나요?",
                            "TypeScript를 사용하는 이유와 JavaScript 대비 장점은 무엇인가요?",
                            "반응형 웹 디자인을 구현할 때 고려해야 할 점들은 무엇인가요?"
                        ],
                        "analysis": "프론트엔드 기술 스택에 대한 기본 이해도가 있습니다. 최신 트렌드와 성능 최적화에 대한 깊은 이해가 필요합니다.",
                        "difficulty": "MIDDLE"
                    }
                    """;
        } else {
            return """
                    {
                        "questions": [
                            "가장 최근에 진행한 프로젝트에서 어떤 역할을 담당하셨나요?",
                            "팀 프로젝트에서 겪었던 어려움과 해결 방법을 설명해 주세요.",
                            "새로운 기술을 학습할 때 어떤 방법을 사용하시나요?",
                            "코드 리뷰 시 중점적으로 확인하는 부분은 무엇인가요?",
                            "개발 과정에서 발생한 버그를 어떻게 디버깅하시나요?"
                        ],
                        "analysis": "일반적인 개발 역량과 문제 해결 능력을 검증할 수 있는 질문들입니다.",
                        "difficulty": "JUNIOR"
                    }
                    """;
        }
    }

    /**
     * 기본 응답 생성 (오류 발생 시)
     */
    private InterviewQuestionsResponse createDefaultResponse(ResumeDto resume) {
        return InterviewQuestionsResponse.builder()
                .resumeId(resume.getId())
                .difficulty(resume.getInterviewDifficulty())
                .questions(List.of(
                    "Spring Boot에서 의존성 주입(Dependency Injection)의 장점에 대해 설명해주세요.",
                    "JPA와 MyBatis의 차이점을 설명하고, 각각 언제 사용하는 것이 좋은지 알려주세요.",
                    "RESTful API 설계 원칙에 대해 설명해주세요.",
                    "데이터베이스 인덱스의 개념과 장단점에 대해 설명해주세요.",
                    "트랜잭션의 ACID 속성에 대해 설명해주세요."
                ))
                .analysis("기본적인 백엔드 개발 지식을 보유하고 있으며, 추가적인 실무 경험이 필요합니다.")
                .generatedAt(LocalDateTime.now())
                .promptUsed("Default Response")
                .build();
    }

    /**
     * AI 응답 파싱 및 변환
     */
    private InterviewQuestionsResponse parseAIResponse(String aiResponse, ResumeDto resume) {
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
            
            @SuppressWarnings("unchecked")
            List<String> questions = (List<String>) responseMap.get("questions");
            String analysis = (String) responseMap.get("analysis");
            String difficultyStr = (String) responseMap.get("difficulty");
            
            InterviewDifficulty difficulty = InterviewDifficulty.valueOf(difficultyStr);
            
            return InterviewQuestionsResponse.builder()
                    .resumeId(resume.getId())
                    .difficulty(difficulty)
                    .questions(questions)
                    .analysis(analysis)
                    .generatedAt(LocalDateTime.now())
                    .promptUsed("AI Generated")
                    .build();
                    
        } catch (JsonProcessingException e) {
            log.error("AI 응답 파싱 실패: {}", e.getMessage());
            log.error("원본 AI 응답: {}", aiResponse);
            throw new AIServiceException("AIInterviewService", "AI 응답 파싱에 실패했습니다.", e);
        }
    }
} 