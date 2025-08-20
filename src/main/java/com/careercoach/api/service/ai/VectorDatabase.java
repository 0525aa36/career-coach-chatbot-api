package com.careercoach.api.service.ai;

import java.util.List;

/**
 * 벡터 데이터베이스 인터페이스
 * RAG 시스템을 위한 벡터 검색 기능을 제공합니다.
 */
public interface VectorDatabase {
    
    /**
     * 유사도 검색
     * 
     * @param query 검색 쿼리
     * @param limit 반환할 결과 수
     * @return 유사한 문서 목록
     */
    List<String> similaritySearch(String query, int limit);
    
    /**
     * 문서 저장
     * 
     * @param content 문서 내용
     * @param metadata 메타데이터
     */
    void storeDocument(String content, String metadata);
    
    /**
     * 인덱스 생성
     */
    void createIndex();
    
    /**
     * 데이터베이스 상태 확인
     * 
     * @return 사용 가능 여부
     */
    boolean isAvailable();
} 