package com.careercoach.api.service.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gemini AI 서비스 구현체
 * Google AI의 Gemini API를 사용하여 AI 응답을 생성합니다.
 */
@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class GeminiAIService implements AIService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String apiUrl;

    @Override
    public String callAI(String prompt) {
        try {
            log.info("Gemini API 호출 시작 - 프롬프트 길이: {} 문자", prompt.length());
            log.info("API URL: {}", apiUrl);
            log.info("API Key: {}", apiKey.substring(0, Math.min(10, apiKey.length())) + "...");

            // 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 본문 구성
            Map<String, Object> requestBody = new HashMap<>();
            
            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(Map.of("text", prompt)));
            
            requestBody.put("contents", List.of(content));
            
            // 생성 설정
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("topK", 40);
            generationConfig.put("topP", 0.95);
            generationConfig.put("maxOutputTokens", 2048);
            requestBody.put("generationConfig", generationConfig);

            // API 호출
            String fullUrl = apiUrl + "?key=" + apiKey;
            log.info("전체 URL: {}", fullUrl);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            log.info("요청 본문: {}", requestBody);
            Map<String, Object> response = restTemplate.postForObject(fullUrl, request, Map.class);
            
            log.info("Gemini API 응답: {}", response);
            
            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> contentResponse = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) contentResponse.get("parts");
                    if (!parts.isEmpty()) {
                        String result = (String) parts.get(0).get("text");
                        log.info("Gemini API 응답 성공 - 응답 길이: {} 문자", result.length());
                        return result;
                    }
                }
            }
            
            log.error("Gemini API 응답 파싱 실패 - 응답: {}", response);
            throw new RuntimeException("AI 응답을 파싱할 수 없습니다.");
            
        } catch (Exception e) {
            log.error("Gemini API 호출 실패: {}", e.getMessage(), e);
            throw new RuntimeException("AI 서비스 호출에 실패했습니다.", e);
        }
    }

    @Override
    public String getServiceName() {
        return "Gemini AI";
    }

    @Override
    public boolean isAvailable() {
        try {
            // 간단한 테스트 호출로 서비스 가용성 확인
            String testPrompt = "안녕하세요. 간단한 테스트입니다.";
            String result = callAI(testPrompt);
            log.info("Gemini AI 서비스 테스트 성공: {}", result);
            return true;
        } catch (Exception e) {
            log.error("Gemini AI 서비스 사용 불가: {}", e.getMessage(), e);
            return false;
        }
    }
} 