package com.careercoach.api.service.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 지식 베이스 서비스
 * RAG(Retrieval-Augmented Generation)를 위한 컨텍스트 강화 서비스
 */
@Slf4j
@Service
public class KnowledgeBaseService {

    /**
     * 프롬프트에 컨텍스트를 강화합니다.
     * 현재는 기본 구현으로, 향후 벡터 데이터베이스 연동 가능
     */
    public String enhancePromptWithContext(String basePrompt, String jobRole) {
        log.debug("프롬프트 컨텍스트 강화 - 직무: {}", jobRole);
        
        // 현재는 기본 프롬프트를 그대로 반환
        // 향후 벡터 데이터베이스에서 관련 문서를 검색하여 컨텍스트를 추가할 수 있음
        return basePrompt;
    }
} 