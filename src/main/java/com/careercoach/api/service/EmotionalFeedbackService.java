package com.careercoach.api.service;

import com.careercoach.api.exception.AIServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 감정 분석 피드백 서비스
 * 사용자의 답변에서 감정 상태를 분석하고 개선점을 제시합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmotionalFeedbackService {

    /**
     * 감정 분석 결과
     */
    public static class EmotionalAnalysis {
        private final String primaryEmotion;
        private final double confidenceLevel;
        private final double stressLevel;
        private final String improvementSuggestion;
        private final Map<String, Double> emotionScores;

        public EmotionalAnalysis(String primaryEmotion, double confidenceLevel, double stressLevel,
                               String improvementSuggestion, Map<String, Double> emotionScores) {
            this.primaryEmotion = primaryEmotion;
            this.confidenceLevel = confidenceLevel;
            this.stressLevel = stressLevel;
            this.improvementSuggestion = improvementSuggestion;
            this.emotionScores = emotionScores;
        }

        // Getters
        public String getPrimaryEmotion() { return primaryEmotion; }
        public double getConfidenceLevel() { return confidenceLevel; }
        public double getStressLevel() { return stressLevel; }
        public String getImprovementSuggestion() { return improvementSuggestion; }
        public Map<String, Double> getEmotionScores() { return emotionScores; }
    }

    /**
     * 면접 성과 감정 분석
     * AI 서비스(Sentiment Analysis API)를 호출하여 사용자의 답변에서 감정 상태 분석
     */
    public EmotionalAnalysis analyzeInterviewPerformance(String userAnswerText) {
        log.info("면접 성과 감정 분석 시작 - 답변 길이: {} 문자", userAnswerText.length());

        try {
            // 감정 분석 수행 (실제로는 AI 서비스 호출)
            Map<String, Double> emotionScores = performEmotionAnalysis(userAnswerText);
            
            // 주요 감정 식별
            String primaryEmotion = identifyPrimaryEmotion(emotionScores);
            
            // 자신감 수준 계산
            double confidenceLevel = calculateConfidenceLevel(emotionScores);
            
            // 스트레스 수준 계산
            double stressLevel = calculateStressLevel(emotionScores);
            
            // 개선 제안 생성
            String improvementSuggestion = generateImprovementSuggestion(primaryEmotion, confidenceLevel, stressLevel);

            EmotionalAnalysis analysis = new EmotionalAnalysis(
                    primaryEmotion, confidenceLevel, stressLevel, improvementSuggestion, emotionScores
            );

            log.info("감정 분석 완료 - 주요 감정: {}, 자신감: {:.2f}, 스트레스: {:.2f}", 
                    primaryEmotion, confidenceLevel, stressLevel);

            return analysis;

        } catch (Exception e) {
            log.error("감정 분석 중 오류 발생: {}", e.getMessage(), e);
            throw new AIServiceException("EmotionalFeedbackService", "감정 분석 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 감정 분석 수행 (목업 구현)
     */
    private Map<String, Double> performEmotionAnalysis(String userAnswerText) {
        // 실제 구현에서는 AI 서비스(Sentiment Analysis API) 호출
        // 현재는 텍스트 내용에 따른 목업 분석
        
        Map<String, Double> emotions = Map.of(
                "자신감", 0.0,
                "불안감", 0.0,
                "열정", 0.0,
                "긴장", 0.0,
                "평온함", 0.0
        );

        String lowerText = userAnswerText.toLowerCase();

        // 자신감 지표 분석
        double confidence = 0.0;
        if (lowerText.contains("확실히") || lowerText.contains("분명히") || lowerText.contains("당연히")) {
            confidence += 0.3;
        }
        if (lowerText.contains("경험이 있습니다") || lowerText.contains("구현했습니다") || lowerText.contains("성공했습니다")) {
            confidence += 0.4;
        }
        if (lowerText.contains("자신있습니다") || lowerText.contains("잘 알고 있습니다")) {
            confidence += 0.3;
        }

        // 불안감 지표 분석
        double anxiety = 0.0;
        if (lowerText.contains("잘 모르겠습니다") || lowerText.contains("불확실합니다")) {
            anxiety += 0.4;
        }
        if (lowerText.contains("어려울 것 같습니다") || lowerText.contains("힘들 것 같습니다")) {
            anxiety += 0.3;
        }
        if (lowerText.contains("시도해보겠습니다") || lowerText.contains("노력하겠습니다")) {
            anxiety += 0.2;
        }

        // 열정 지표 분석
        double passion = 0.0;
        if (lowerText.contains("관심이 많습니다") || lowerText.contains("흥미롭습니다")) {
            passion += 0.4;
        }
        if (lowerText.contains("학습하고 있습니다") || lowerText.contains("연구하고 있습니다")) {
            passion += 0.3;
        }
        if (lowerText.contains("도전하고 싶습니다") || lowerText.contains("새로운 기술")) {
            passion += 0.3;
        }

        // 긴장 지표 분석
        double tension = 0.0;
        if (lowerText.contains("어려운") || lowerText.contains("복잡한") || lowerText.contains("도전적인")) {
            tension += 0.3;
        }
        if (lowerText.contains("시간이 걸렸습니다") || lowerText.contains("고민했습니다")) {
            tension += 0.2;
        }

        // 평온함 지표 분석
        double calmness = 0.0;
        if (lowerText.contains("차근차근") || lowerText.contains("단계별로") || lowerText.contains("체계적으로")) {
            calmness += 0.4;
        }
        if (lowerText.contains("잘 해결했습니다") || lowerText.contains("성공적으로")) {
            calmness += 0.3;
        }

        return Map.of(
                "자신감", Math.min(confidence, 1.0),
                "불안감", Math.min(anxiety, 1.0),
                "열정", Math.min(passion, 1.0),
                "긴장", Math.min(tension, 1.0),
                "평온함", Math.min(calmness, 1.0)
        );
    }

    /**
     * 주요 감정 식별
     */
    private String identifyPrimaryEmotion(Map<String, Double> emotionScores) {
        return emotionScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("중립");
    }

    /**
     * 자신감 수준 계산
     */
    private double calculateConfidenceLevel(Map<String, Double> emotionScores) {
        double confidence = emotionScores.getOrDefault("자신감", 0.0);
        double anxiety = emotionScores.getOrDefault("불안감", 0.0);
        double tension = emotionScores.getOrDefault("긴장", 0.0);

        // 자신감에서 불안감과 긴장을 차감
        return Math.max(0.0, Math.min(1.0, confidence - (anxiety + tension) * 0.5));
    }

    /**
     * 스트레스 수준 계산
     */
    private double calculateStressLevel(Map<String, Double> emotionScores) {
        double anxiety = emotionScores.getOrDefault("불안감", 0.0);
        double tension = emotionScores.getOrDefault("긴장", 0.0);
        double calmness = emotionScores.getOrDefault("평온함", 0.0);

        // 불안감과 긴장에서 평온함을 차감
        return Math.max(0.0, Math.min(1.0, (anxiety + tension) * 0.7 - calmness * 0.3));
    }

    /**
     * 개선 제안 생성
     */
    private String generateImprovementSuggestion(String primaryEmotion, double confidenceLevel, double stressLevel) {
        StringBuilder suggestion = new StringBuilder();

        // 자신감 수준에 따른 제안
        if (confidenceLevel < 0.3) {
            suggestion.append("자신감을 높이기 위해 구체적인 성공 사례를 준비해보세요. ");
        } else if (confidenceLevel < 0.6) {
            suggestion.append("적당한 자신감을 유지하면서도 겸손한 태도를 보여주세요. ");
        } else {
            suggestion.append("높은 자신감이 좋습니다. 이를 바탕으로 더 도전적인 질문에 답변해보세요. ");
        }

        // 스트레스 수준에 따른 제안
        if (stressLevel > 0.7) {
            suggestion.append("긴장을 줄이기 위해 심호흡을 하고 천천히 답변해보세요. ");
        } else if (stressLevel > 0.4) {
            suggestion.append("적당한 긴장감은 좋습니다. 이를 동기부여로 활용해보세요. ");
        } else {
            suggestion.append("편안한 상태를 잘 유지하고 있습니다. ");
        }

        // 주요 감정에 따른 제안
        switch (primaryEmotion) {
            case "불안감" -> suggestion.append("불안감을 줄이기 위해 충분한 준비를 하고 긍정적인 마인드를 유지하세요. ");
            case "열정" -> suggestion.append("열정이 좋습니다. 이를 바탕으로 구체적인 계획과 목표를 제시해보세요. ");
            case "긴장" -> suggestion.append("긴장을 완화하기 위해 체계적으로 답변을 구성해보세요. ");
            case "평온함" -> suggestion.append("차분한 태도가 좋습니다. 이를 바탕으로 깊이 있는 답변을 해보세요. ");
            default -> suggestion.append("균형잡힌 감정 상태를 유지하고 있습니다. ");
        }

        return suggestion.toString();
    }

    /**
     * 종합 피드백 생성
     */
    public String generateComprehensiveFeedback(EmotionalAnalysis analysis) {
        return String.format("""
                [감정 분석 피드백]
                
                주요 감정: %s
                자신감 수준: %.1f%%
                스트레스 수준: %.1f%%
                
                [감정 점수]
                %s
                
                [개선 제안]
                %s
                
                [면접 팁]
                • 명확하고 구체적인 답변을 준비하세요
                • 자신의 경험을 바탕으로 한 사례를 제시하세요
                • 적절한 속도로 천천히 답변하세요
                • 긍정적인 마인드를 유지하세요
                """,
                analysis.getPrimaryEmotion(),
                analysis.getConfidenceLevel() * 100,
                analysis.getStressLevel() * 100,
                formatEmotionScores(analysis.getEmotionScores()),
                analysis.getImprovementSuggestion()
        );
    }

    /**
     * 감정 점수 포맷팅
     */
    private String formatEmotionScores(Map<String, Double> emotionScores) {
        return emotionScores.entrySet().stream()
                .map(entry -> String.format("• %s: %.1f%%", entry.getKey(), entry.getValue() * 100))
                .reduce("", (a, b) -> a + "\n" + b);
    }

    /**
     * 감정 트렌드 분석 (여러 답변에 대한)
     */
    public String analyzeEmotionalTrends(List<EmotionalAnalysis> analyses) {
        if (analyses.isEmpty()) {
            return "분석할 답변이 없습니다.";
        }

        // 평균 감정 점수 계산
        double avgConfidence = analyses.stream()
                .mapToDouble(EmotionalAnalysis::getConfidenceLevel)
                .average()
                .orElse(0.0);

        double avgStress = analyses.stream()
                .mapToDouble(EmotionalAnalysis::getStressLevel)
                .average()
                .orElse(0.0);

        // 트렌드 분석
        String trend = "";
        if (analyses.size() >= 2) {
            EmotionalAnalysis first = analyses.get(0);
            EmotionalAnalysis last = analyses.get(analyses.size() - 1);

            if (last.getConfidenceLevel() > first.getConfidenceLevel()) {
                trend = "자신감이 점진적으로 향상되고 있습니다. ";
            } else if (last.getConfidenceLevel() < first.getConfidenceLevel()) {
                trend = "자신감이 다소 감소하고 있습니다. 긍정적인 마인드를 유지하세요. ";
            } else {
                trend = "자신감이 안정적으로 유지되고 있습니다. ";
            }
        }

        return String.format("""
                [감정 트렌드 분석]
                
                총 답변 수: %d개
                평균 자신감: %.1f%%
                평균 스트레스: %.1f%%
                
                [트렌드]
                %s
                
                [권장사항]
                • 일관된 자신감을 유지하세요
                • 긴장감을 적절히 관리하세요
                • 긍정적인 피드백을 받아들이세요
                """,
                analyses.size(),
                avgConfidence * 100,
                avgStress * 100,
                trend
        );
    }
} 