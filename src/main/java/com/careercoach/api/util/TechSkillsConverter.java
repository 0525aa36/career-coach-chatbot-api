package com.careercoach.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 기술 스택 리스트를 JSON으로 변환하는 컨버터
 * 데이터베이스에 JSON 형태로 저장하고, 엔티티에서는 List<String>으로 사용합니다.
 */
@Slf4j
@Converter
public class TechSkillsConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> techSkills) {
        if (techSkills == null || techSkills.isEmpty()) {
            return "[]";
        }
        
        try {
            return objectMapper.writeValueAsString(techSkills);
        } catch (JsonProcessingException e) {
            log.error("기술 스택을 JSON으로 변환하는 중 오류 발생: {}", e.getMessage());
            return "[]";
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty() || dbData.equals("[]")) {
            return List.of();
        }
        
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.error("JSON을 기술 스택 리스트로 변환하는 중 오류 발생: {}", e.getMessage());
            return List.of();
        }
    }
} 