package com.careercoach.api.service.ai;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * VectorDatabase 목업 구현체
 * 실제 벡터 데이터베이스 연동 전 테스트용 구현체입니다.
 */
@Service
public class MockVectorDatabase implements VectorDatabase {

    @Override
    public List<String> similaritySearch(String query, int limit) {
        // 목업 문서 반환
        return List.of(
                "Spring Boot 최신 버전 업데이트 가이드",
                "JPA 성능 최적화 베스트 프랙티스",
                "마이크로서비스 아키텍처 설계 패턴",
                "REST API 설계 원칙과 실무 적용",
                "데이터베이스 인덱싱 전략"
        ).subList(0, Math.min(limit, 5));
    }

    @Override
    public void storeDocument(String content, String metadata) {
        // 목업 저장 로직 (실제로는 아무것도 하지 않음)
        System.out.println("Mock document stored: " + metadata);
    }

    @Override
    public void createIndex() {
        // 목업 인덱스 생성 (실제로는 아무것도 하지 않음)
        System.out.println("Mock index created");
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
} 