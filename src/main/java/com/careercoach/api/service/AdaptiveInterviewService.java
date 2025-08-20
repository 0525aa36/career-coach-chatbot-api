package com.careercoach.api.service;

import com.careercoach.api.dto.response.InterviewQuestionsResponse;
import com.careercoach.api.dto.response.ResumeDto;
import com.careercoach.api.domain.enums.InterviewDifficulty;
import com.careercoach.api.exception.AIServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 적응형 면접 서비스
 * 사용자의 이전 답변을 분석하여 다음 질문의 난이도를 동적으로 조절합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdaptiveInterviewService {

    /**
     * 이전 답변 정보를 담는 클래스
     */
    public static class PreviousAnswer {
        private String question;
        private String answer;
        private int answerTimeSeconds;
        private double confidenceScore;
        private boolean isCorrect;
        private String feedback;

        public PreviousAnswer(String question, String answer, int answerTimeSeconds, 
                            double confidenceScore, boolean isCorrect, String feedback) {
            this.question = question;
            this.answer = answer;
            this.answerTimeSeconds = answerTimeSeconds;
            this.confidenceScore = confidenceScore;
            this.isCorrect = isCorrect;
            this.feedback = feedback;
        }

        // Getters
        public String getQuestion() { return question; }
        public String getAnswer() { return answer; }
        public int getAnswerTimeSeconds() { return answerTimeSeconds; }
        public double getConfidenceScore() { return confidenceScore; }
        public boolean isCorrect() { return isCorrect; }
        public String getFeedback() { return feedback; }
    }

    /**
     * 적응형 면접 질문 생성
     * 이전 답변들의 정답률, 답변 시간 등을 분석하여 다음 질문의 난이도를 동적으로 조절
     */
    public InterviewQuestionsResponse generateAdaptiveQuestions(ResumeDto resume, List<PreviousAnswer> userAnswers) {
        log.info("적응형 면접 질문 생성 시작 - 이력서 ID: {}, 이전 답변 수: {}", resume.getId(), userAnswers.size());

        try {
            // 이전 답변 분석
            AnswerAnalysis analysis = analyzePreviousAnswers(userAnswers);
            
            // 난이도 조정
            InterviewDifficulty adjustedDifficulty = adjustDifficulty(resume.getInterviewDifficulty(), analysis);
            
            // 적응형 질문 생성
            List<String> adaptiveQuestions = generateQuestionsBasedOnAnalysis(resume, analysis, adjustedDifficulty);
            
            // 분석 결과 포함
            String analysisSummary = createAnalysisSummary(analysis, adjustedDifficulty);

            InterviewQuestionsResponse response = InterviewQuestionsResponse.builder()
                    .resumeId(resume.getId())
                    .difficulty(adjustedDifficulty)
                    .questions(adaptiveQuestions)
                    .analysis(analysisSummary)
                    .generatedAt(java.time.LocalDateTime.now())
                    .promptUsed("Adaptive Interview System")
                    .build();

            log.info("적응형 면접 질문 생성 완료 - 조정된 난이도: {}, 질문 수: {}", 
                    adjustedDifficulty, adaptiveQuestions.size());

            return response;

        } catch (Exception e) {
            log.error("적응형 면접 질문 생성 중 오류 발생: {}", e.getMessage(), e);
            throw new AIServiceException("AdaptiveInterviewService", "적응형 질문 생성 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 이전 답변 분석
     */
    private AnswerAnalysis analyzePreviousAnswers(List<PreviousAnswer> userAnswers) {
        if (userAnswers.isEmpty()) {
            return new AnswerAnalysis(0.0, 0.0, 0.0, 0.0, "첫 번째 질문입니다.");
        }

        // 정답률 계산
        long correctAnswers = userAnswers.stream().filter(PreviousAnswer::isCorrect).count();
        double accuracyRate = (double) correctAnswers / userAnswers.size();

        // 평균 답변 시간 계산
        double avgAnswerTime = userAnswers.stream()
                .mapToInt(PreviousAnswer::getAnswerTimeSeconds)
                .average()
                .orElse(0.0);

        // 평균 자신감 점수 계산
        double avgConfidence = userAnswers.stream()
                .mapToDouble(PreviousAnswer::getConfidenceScore)
                .average()
                .orElse(0.0);

        // 답변 품질 점수 계산 (시간과 정확도의 조합)
        double qualityScore = calculateQualityScore(userAnswers);

        // 분석 요약 생성
        String analysisSummary = createAnswerAnalysisSummary(accuracyRate, avgAnswerTime, avgConfidence, qualityScore);

        return new AnswerAnalysis(accuracyRate, avgAnswerTime, avgConfidence, qualityScore, analysisSummary);
    }

    /**
     * 답변 품질 점수 계산
     */
    private double calculateQualityScore(List<PreviousAnswer> userAnswers) {
        return userAnswers.stream()
                .mapToDouble(answer -> {
                    // 정확도 가중치: 60%
                    double accuracyWeight = answer.isCorrect() ? 0.6 : 0.0;
                    
                    // 시간 효율성 가중치: 20% (적절한 시간 내 답변)
                    double timeWeight = calculateTimeWeight(answer.getAnswerTimeSeconds());
                    
                    // 자신감 가중치: 20%
                    double confidenceWeight = answer.getConfidenceScore() * 0.2;
                    
                    return accuracyWeight + timeWeight + confidenceWeight;
                })
                .average()
                .orElse(0.0);
    }

    /**
     * 시간 가중치 계산
     */
    private double calculateTimeWeight(int answerTimeSeconds) {
        // 적절한 답변 시간: 30초 ~ 120초
        if (answerTimeSeconds < 10) {
            return 0.0; // 너무 빠른 답변
        } else if (answerTimeSeconds <= 30) {
            return 0.1; // 빠른 답변
        } else if (answerTimeSeconds <= 120) {
            return 0.2; // 적절한 답변
        } else if (answerTimeSeconds <= 300) {
            return 0.1; // 느린 답변
        } else {
            return 0.0; // 너무 느린 답변
        }
    }

    /**
     * 난이도 조정
     */
    private InterviewDifficulty adjustDifficulty(InterviewDifficulty currentDifficulty, AnswerAnalysis analysis) {
        double qualityScore = analysis.getQualityScore();
        double accuracyRate = analysis.getAccuracyRate();

        // 품질 점수와 정답률에 따른 난이도 조정
        if (qualityScore >= 0.8 && accuracyRate >= 0.8) {
            // 높은 성과: 난이도 상향 조정
            return upgradeDifficulty(currentDifficulty);
        } else if (qualityScore <= 0.4 || accuracyRate <= 0.4) {
            // 낮은 성과: 난이도 하향 조정
            return downgradeDifficulty(currentDifficulty);
        } else {
            // 적절한 성과: 현재 난이도 유지
            return currentDifficulty;
        }
    }

    /**
     * 난이도 상향 조정
     */
    private InterviewDifficulty upgradeDifficulty(InterviewDifficulty currentDifficulty) {
        return switch (currentDifficulty) {
            case JUNIOR -> InterviewDifficulty.MIDDLE;
            case MIDDLE -> InterviewDifficulty.SENIOR;
            case SENIOR -> InterviewDifficulty.SENIOR; // 최고 난이도
        };
    }

    /**
     * 난이도 하향 조정
     */
    private InterviewDifficulty downgradeDifficulty(InterviewDifficulty currentDifficulty) {
        return switch (currentDifficulty) {
            case JUNIOR -> InterviewDifficulty.JUNIOR; // 최저 난이도
            case MIDDLE -> InterviewDifficulty.JUNIOR;
            case SENIOR -> InterviewDifficulty.MIDDLE;
        };
    }

    /**
     * 분석 기반 질문 생성
     */
    private List<String> generateQuestionsBasedOnAnalysis(ResumeDto resume, AnswerAnalysis analysis, InterviewDifficulty difficulty) {
        // TODO: 이전 답변들의 정답률, 답변 시간 등을 분석하여 다음 질문의 난이도를 동적으로 조절하는 로직 구현
        
        // 현재는 기본 질문 목록 반환 (실제 구현에서는 AI 서비스를 활용)
        return List.of(
            "이전 답변을 바탕으로 더 구체적인 사례를 들어 설명해 주세요.",
            "실제 프로젝트에서 어떻게 이 기술을 적용하셨나요?",
            "이 기술의 한계점과 대안에 대해 어떻게 생각하시나요?",
            "팀 프로젝트에서 이 기술을 도입할 때 겪었던 어려움은 무엇이었나요?",
            "최신 트렌드와 비교했을 때 이 기술의 장단점은 무엇인가요?"
        );
    }

    /**
     * 분석 요약 생성
     */
    private String createAnalysisSummary(AnswerAnalysis analysis, InterviewDifficulty adjustedDifficulty) {
        return String.format("""
                [적응형 면접 분석 결과]
                - 정답률: %.1f%%
                - 평균 답변 시간: %.1f초
                - 평균 자신감 점수: %.2f
                - 종합 품질 점수: %.2f
                - 조정된 난이도: %s
                
                [조정 사유]
                %s
                """,
                analysis.getAccuracyRate() * 100,
                analysis.getAvgAnswerTime(),
                analysis.getAvgConfidence(),
                analysis.getQualityScore(),
                adjustedDifficulty.getDisplayName(),
                analysis.getAnalysisSummary()
        );
    }

    /**
     * 답변 분석 요약 생성
     */
    private String createAnswerAnalysisSummary(double accuracyRate, double avgAnswerTime, double avgConfidence, double qualityScore) {
        StringBuilder summary = new StringBuilder();
        
        if (accuracyRate >= 0.8) {
            summary.append("정답률이 높아 난이도를 상향 조정했습니다. ");
        } else if (accuracyRate <= 0.4) {
            summary.append("정답률이 낮아 난이도를 하향 조정했습니다. ");
        } else {
            summary.append("적절한 정답률로 현재 난이도를 유지합니다. ");
        }

        if (avgAnswerTime < 30) {
            summary.append("답변 시간이 빠르므로 더 깊이 있는 질문을 준비했습니다. ");
        } else if (avgAnswerTime > 120) {
            summary.append("답변 시간이 길어 더 명확하고 간결한 질문을 준비했습니다. ");
        }

        if (avgConfidence >= 0.8) {
            summary.append("자신감이 높아 도전적인 질문을 추가했습니다. ");
        } else if (avgConfidence <= 0.4) {
            summary.append("자신감이 낮아 격려와 함께 기본적인 질문을 준비했습니다. ");
        }

        return summary.toString();
    }

    /**
     * 답변 분석 결과 클래스
     */
    private static class AnswerAnalysis {
        private final double accuracyRate;
        private final double avgAnswerTime;
        private final double avgConfidence;
        private final double qualityScore;
        private final String analysisSummary;

        public AnswerAnalysis(double accuracyRate, double avgAnswerTime, double avgConfidence, 
                            double qualityScore, String analysisSummary) {
            this.accuracyRate = accuracyRate;
            this.avgAnswerTime = avgAnswerTime;
            this.avgConfidence = avgConfidence;
            this.qualityScore = qualityScore;
            this.analysisSummary = analysisSummary;
        }

        // Getters
        public double getAccuracyRate() { return accuracyRate; }
        public double getAvgAnswerTime() { return avgAnswerTime; }
        public double getAvgConfidence() { return avgConfidence; }
        public double getQualityScore() { return qualityScore; }
        public String getAnalysisSummary() { return analysisSummary; }
    }
} 