package com.careercoach.api.service;

import com.careercoach.api.domain.entity.Resume;
import com.careercoach.api.domain.enums.JobRole;
import com.careercoach.api.dto.request.CreateResumeRequest;
import com.careercoach.api.dto.response.ResumeDto;
import com.careercoach.api.exception.InvalidResumeDataException;
import com.careercoach.api.exception.ResumeNotFoundException;
import com.careercoach.api.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

/**
 * 이력서 비즈니스 로직 서비스
 * 실무 수준의 견고한 로직을 구현한 서비스 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResumeService {

    private final ResumeRepository resumeRepository;

    /**
     * 이력서 생성
     * @Transactional과 @Retryable을 적용하여 견고한 트랜잭션 처리를 구현합니다.
     */
    @Transactional
    @Retryable(value = {DataAccessException.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public ResumeDto createResume(CreateResumeRequest request) {
        log.info("이력서 생성 시작 - 직무: {}, 경력: {}년", request.getJobRole(), request.getExperienceYears());

        // 요청 데이터 검증
        validateResumeData(request);

        // 이력서 엔티티 생성
        Resume resume = Resume.builder()
                .careerSummary(request.getCareerSummary())
                .jobRole(request.getJobRole())
                .experienceYears(request.getExperienceYears())
                .projectExperience(request.getProjectExperience())
                .techSkills(request.getTechSkills())
                .build();

        try {
            // 데이터베이스에 저장
            Resume savedResume = resumeRepository.save(resume);
            log.info("이력서 생성 완료 - ID: {}", savedResume.getId());

            return ResumeDto.from(savedResume);
        } catch (DataAccessException e) {
            log.error("이력서 저장 중 데이터베이스 오류 발생: {}", e.getMessage());
            throw new InvalidResumeDataException("이력서 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 이력서 조회 (캐싱 적용)
     */
    @Cacheable(value = "resume-cache", key = "#resumeId")
    public ResumeDto getResume(Long resumeId) {
        log.info("이력서 조회 - ID: {}", resumeId);

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));

        return ResumeDto.from(resume);
    }

    /**
     * 이력서 수정
     * 캐시 무효화를 통해 데이터 정합성을 유지합니다.
     */
    @Transactional
    @CacheEvict(value = {"resume-cache", "interview-questions", "learning-paths"}, key = "#resumeId")
    public ResumeDto updateResume(Long resumeId, CreateResumeRequest request) {
        log.info("이력서 수정 시작 - ID: {}", resumeId);

        // 요청 데이터 검증
        validateResumeData(request);

        // 기존 이력서 조회
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResumeNotFoundException(resumeId));

        // 이력서 정보 업데이트
        resume.updateResume(
                request.getCareerSummary(),
                request.getJobRole(),
                request.getExperienceYears(),
                request.getProjectExperience(),
                request.getTechSkills()
        );

        try {
            Resume updatedResume = resumeRepository.save(resume);
            log.info("이력서 수정 완료 - ID: {}", updatedResume.getId());

            return ResumeDto.from(updatedResume);
        } catch (DataAccessException e) {
            log.error("이력서 수정 중 데이터베이스 오류 발생: {}", e.getMessage());
            throw new InvalidResumeDataException("이력서 수정 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 이력서 삭제
     */
    @Transactional
    @CacheEvict(value = {"resume-cache", "interview-questions", "learning-paths"}, key = "#resumeId")
    public void deleteResume(Long resumeId) {
        log.info("이력서 삭제 시작 - ID: {}", resumeId);

        if (!resumeRepository.existsById(resumeId)) {
            throw new ResumeNotFoundException(resumeId);
        }

        try {
            resumeRepository.deleteById(resumeId);
            log.info("이력서 삭제 완료 - ID: {}", resumeId);
        } catch (DataAccessException e) {
            log.error("이력서 삭제 중 데이터베이스 오류 발생: {}", e.getMessage());
            throw new InvalidResumeDataException("이력서 삭제 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 모든 이력서 조회
     */
    public List<ResumeDto> getAllResumes() {
        log.info("모든 이력서 조회");
        
        List<Resume> resumes = resumeRepository.findAll();
        return resumes.stream()
                .map(ResumeDto::from)
                .toList();
    }

    /**
     * 직무별 이력서 조회
     */
    public List<ResumeDto> getResumesByJobRole(JobRole jobRole) {
        log.info("직무별 이력서 조회 - 직무: {}", jobRole);

        List<Resume> resumes = resumeRepository.findByJobRole(jobRole);
        return resumes.stream()
                .map(ResumeDto::from)
                .toList();
    }

    /**
     * 경력 연수 범위로 이력서 조회
     */
    public List<ResumeDto> getResumesByExperienceRange(Integer minYears, Integer maxYears) {
        log.info("경력 연수 범위로 이력서 조회 - {}년 ~ {}년", minYears, maxYears);

        validateExperienceRange(minYears, maxYears);

        List<Resume> resumes = resumeRepository.findByExperienceYearsBetween(minYears, maxYears);
        return resumes.stream()
                .map(ResumeDto::from)
                .toList();
    }

    /**
     * 페이징을 통한 이력서 조회
     */
    public Page<ResumeDto> getResumesWithPaging(Pageable pageable) {
        log.info("페이징을 통한 이력서 조회 - 페이지: {}, 크기: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Resume> resumePage = resumeRepository.findAll(pageable);
        return resumePage.map(ResumeDto::from);
    }

    /**
     * 기술 스택으로 이력서 검색
     */
    public List<ResumeDto> getResumesByTechSkill(String techSkill) {
        log.info("기술 스택으로 이력서 검색 - 기술: {}", techSkill);

        if (!StringUtils.hasText(techSkill)) {
            throw new InvalidResumeDataException("techSkill", "기술 스택은 비어있을 수 없습니다.");
        }

        List<Resume> resumes = resumeRepository.findByTechSkill(techSkill);
        return resumes.stream()
                .map(ResumeDto::from)
                .toList();
    }

    /**
     * 경력 요약으로 이력서 검색
     */
    public List<ResumeDto> searchResumesByCareerSummary(String keyword) {
        log.info("경력 요약으로 이력서 검색 - 키워드: {}", keyword);

        if (!StringUtils.hasText(keyword)) {
            throw new InvalidResumeDataException("keyword", "검색 키워드는 비어있을 수 없습니다.");
        }

        List<Resume> resumes = resumeRepository.findByCareerSummaryContainingIgnoreCase(keyword);
        return resumes.stream()
                .map(ResumeDto::from)
                .toList();
    }

    /**
     * 이력서 데이터 검증
     */
    private void validateResumeData(CreateResumeRequest request) {
        // 경력 연수 검증
        if (request.getExperienceYears() < 0 || request.getExperienceYears() > 50) {
            throw new InvalidResumeDataException("experienceYears", "경력 연수는 0~50년 범위여야 합니다.");
        }

        // 경력 요약 검증
        if (!StringUtils.hasText(request.getCareerSummary())) {
            throw new InvalidResumeDataException("careerSummary", "경력 요약은 필수입니다.");
        }

        if (request.getCareerSummary().length() > 255) {
            throw new InvalidResumeDataException("careerSummary", "경력 요약은 255자를 초과할 수 없습니다.");
        }

        // 프로젝트 경험 검증
        if (request.getProjectExperience() != null && request.getProjectExperience().length() > 5000) {
            throw new InvalidResumeDataException("projectExperience", "프로젝트 경험은 5000자를 초과할 수 없습니다.");
        }

        // 기술 스택 검증
        if (request.getTechSkills() != null && request.getTechSkills().size() > 20) {
            throw new InvalidResumeDataException("techSkills", "기술 스택은 최대 20개까지 입력 가능합니다.");
        }

        log.debug("이력서 데이터 검증 완료");
    }

    /**
     * 경력 연수 범위 검증
     */
    private void validateExperienceRange(Integer minYears, Integer maxYears) {
        if (minYears == null || maxYears == null) {
            throw new InvalidResumeDataException("experienceRange", "최소/최대 경력 연수는 필수입니다.");
        }

        if (minYears < 0 || maxYears < 0) {
            throw new InvalidResumeDataException("experienceRange", "경력 연수는 0 이상이어야 합니다.");
        }

        if (minYears > maxYears) {
            throw new InvalidResumeDataException("experienceRange", "최소 경력 연수는 최대 경력 연수보다 클 수 없습니다.");
        }

        if (maxYears > 50) {
            throw new InvalidResumeDataException("experienceRange", "최대 경력 연수는 50년을 초과할 수 없습니다.");
        }
    }

    /**
     * 이력서 존재 여부 확인
     */
    public boolean existsResume(Long resumeId) {
        return resumeRepository.existsById(resumeId);
    }

    /**
     * 이력서 통계 조회
     */
    public List<Object[]> getResumeStatistics() {
        log.info("이력서 통계 조회");
        return resumeRepository.getResumeCountByJobRole();
    }
} 