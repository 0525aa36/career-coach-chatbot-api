package com.careercoach.api.controller;

import com.careercoach.api.domain.enums.JobRole;
import com.careercoach.api.dto.request.CreateResumeRequest;
import com.careercoach.api.dto.response.ResumeDto;
import com.careercoach.api.dto.response.InterviewQuestionsResponse;
import com.careercoach.api.dto.response.LearningPathResponse;
import com.careercoach.api.service.ResumeService;
import com.careercoach.api.service.ai.AIInterviewService;
import com.careercoach.api.service.ai.AILearningPathService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 이력서 관리 REST API 컨트롤러
 * 이력서 CRUD 작업과 관련된 엔드포인트를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final AIInterviewService aiInterviewService;
    private final AILearningPathService aiLearningPathService;

    /**
     * 이력서 생성
     * 
     * @param request 이력서 생성 요청
     * @return 생성된 이력서 정보
     */
    @PostMapping
    public ResponseEntity<ResumeDto> createResume(@Valid @RequestBody CreateResumeRequest request) {
        log.info("이력서 생성 요청: {}", request.getCareerSummary());
        ResumeDto createdResume = resumeService.createResume(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResume);
    }

    /**
     * 이력서 조회
     * 
     * @param resumeId 이력서 ID
     * @return 이력서 정보
     */
    @GetMapping("/{resumeId}")
    public ResponseEntity<ResumeDto> getResume(@PathVariable Long resumeId) {
        log.info("이력서 조회 요청: ID {}", resumeId);
        ResumeDto resume = resumeService.getResume(resumeId);
        return ResponseEntity.ok(resume);
    }

    /**
     * 이력서 수정
     * 
     * @param resumeId 이력서 ID
     * @param request 이력서 수정 요청
     * @return 수정된 이력서 정보
     */
    @PutMapping("/{resumeId}")
    public ResponseEntity<ResumeDto> updateResume(
            @PathVariable Long resumeId,
            @Valid @RequestBody CreateResumeRequest request) {
        log.info("이력서 수정 요청: ID {}", resumeId);
        ResumeDto updatedResume = resumeService.updateResume(resumeId, request);
        return ResponseEntity.ok(updatedResume);
    }

    /**
     * 이력서 삭제
     * 
     * @param resumeId 이력서 ID
     * @return 삭제 완료 응답
     */
    @DeleteMapping("/{resumeId}")
    public ResponseEntity<Void> deleteResume(@PathVariable Long resumeId) {
        log.info("이력서 삭제 요청: ID {}", resumeId);
        resumeService.deleteResume(resumeId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 모든 이력서 조회
     * 
     * @return 이력서 목록
     */
    @GetMapping
    public ResponseEntity<List<ResumeDto>> getAllResumes() {
        log.info("모든 이력서 조회 요청");
        List<ResumeDto> resumes = resumeService.getAllResumes();
        return ResponseEntity.ok(resumes);
    }

    /**
     * Gemini API 테스트
     * 
     * @return 테스트 결과
     */
    @GetMapping("/test-gemini")
    public ResponseEntity<String> testGemini() {
        log.info("Gemini API 테스트 요청");
        try {
            String result = aiInterviewService.getAiService().callAI("안녕하세요. 간단한 테스트입니다.");
            return ResponseEntity.ok("Gemini API 테스트 성공: " + result);
        } catch (Exception e) {
            log.error("Gemini API 테스트 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Gemini API 테스트 실패: " + e.getMessage());
        }
    }

    /**
     * 테스트 데이터 생성
     * 
     * @return 생성된 이력서 정보
     */
    @PostMapping("/test-data")
    public ResponseEntity<ResumeDto> createTestData() {
        log.info("테스트 데이터 생성 요청");
        
        CreateResumeRequest testRequest = new CreateResumeRequest();
        testRequest.setJobRole(JobRole.BACKEND_DEVELOPER);
        testRequest.setCareerSummary("Spring Boot와 JPA를 활용한 백엔드 개발 경험 3년. RESTful API 설계 및 구현, 마이크로서비스 아키텍처 경험 보유.");
        testRequest.setExperienceYears(3);
        testRequest.setTechSkills(List.of("Java", "Spring Boot", "JPA", "MySQL", "Redis", "Docker"));
        testRequest.setProjectExperience("""
            - 전자상거래 플랫폼 백엔드 API 개발 (2022-2023)
            - 사용자 인증 및 권한 관리 시스템 구축
            - 결제 시스템 연동 및 주문 처리 로직 구현
            - 성능 최적화를 통한 응답 시간 50% 단축
            """);
        
        ResumeDto createdResume = resumeService.createResume(testRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdResume);
    }

    /**
     * 직무 역할별 이력서 조회
     * 
     * @param jobRole 직무 역할
     * @return 이력서 목록
     */
    @GetMapping("/job-role/{jobRole}")
    public ResponseEntity<List<ResumeDto>> getResumesByJobRole(@PathVariable String jobRole) {
        log.info("직무 역할별 이력서 조회 요청: {}", jobRole);
        try {
            JobRole role = JobRole.valueOf(jobRole.toUpperCase());
            List<ResumeDto> resumes = resumeService.getResumesByJobRole(role);
            return ResponseEntity.ok(resumes);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 직무 역할: {}", jobRole);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 경력 연수 범위로 이력서 조회
     * 
     * @param minYears 최소 경력 연수
     * @param maxYears 최대 경력 연수
     * @return 이력서 목록
     */
    @GetMapping("/experience-range")
    public ResponseEntity<List<ResumeDto>> getResumesByExperienceRange(
            @RequestParam Integer minYears,
            @RequestParam Integer maxYears) {
        log.info("경력 연수 범위 이력서 조회 요청: {}~{}년", minYears, maxYears);
        List<ResumeDto> resumes = resumeService.getResumesByExperienceRange(minYears, maxYears);
        return ResponseEntity.ok(resumes);
    }

    /**
     * 기술 스택으로 이력서 검색
     * 
     * @param techSkill 기술 스택
     * @return 이력서 목록
     */
    @GetMapping("/tech-skill/{techSkill}")
    public ResponseEntity<List<ResumeDto>> getResumesByTechSkill(@PathVariable String techSkill) {
        log.info("기술 스택 이력서 검색 요청: {}", techSkill);
        List<ResumeDto> resumes = resumeService.getResumesByTechSkill(techSkill);
        return ResponseEntity.ok(resumes);
    }

    /**
     * AI 기반 맞춤형 인터뷰 질문 생성
     * 
     * @param resumeId 이력서 ID
     * @return 생성된 인터뷰 질문들
     */
    @PostMapping("/{resumeId}/interview-questions")
    public ResponseEntity<InterviewQuestionsResponse> generateInterviewQuestions(@PathVariable Long resumeId) {
        log.info("AI 인터뷰 질문 생성 요청: 이력서 ID {}", resumeId);
        try {
            ResumeDto resume = resumeService.getResume(resumeId);
            InterviewQuestionsResponse questions = aiInterviewService.generateQuestions(resume);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            log.error("인터뷰 질문 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * AI 기반 맞춤형 학습 경로 생성
     * 
     * @param resumeId 이력서 ID
     * @return 생성된 학습 경로
     */
    @PostMapping("/{resumeId}/learning-path")
    public ResponseEntity<LearningPathResponse> generateLearningPath(@PathVariable Long resumeId) {
        log.info("AI 학습 경로 생성 요청: 이력서 ID {}", resumeId);
        try {
            ResumeDto resume = resumeService.getResume(resumeId);
            LearningPathResponse learningPath = aiLearningPathService.generateLearningPath(resume);
            return ResponseEntity.ok(learningPath);
        } catch (Exception e) {
            log.error("학습 경로 생성 실패: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 