package com.careercoach.api.repository;

import com.careercoach.api.domain.entity.Resume;
import com.careercoach.api.domain.enums.JobRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 이력서 데이터 접근 계층
 * JPA Repository를 통한 데이터베이스 접근을 담당합니다.
 */
@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    /**
     * 직무 역할별 이력서 조회
     * 
     * @param jobRole 직무 역할
     * @return 이력서 목록
     */
    List<Resume> findByJobRole(JobRole jobRole);

    /**
     * 경력 연수 범위로 이력서 조회
     * 
     * @param minYears 최소 경력 연수
     * @param maxYears 최대 경력 연수
     * @return 이력서 목록
     */
    List<Resume> findByExperienceYearsBetween(Integer minYears, Integer maxYears);

    /**
     * 직무 역할과 경력 연수로 이력서 조회 (페이징)
     * 
     * @param jobRole 직무 역할
     * @param experienceYears 경력 연수
     * @param pageable 페이징 정보
     * @return 이력서 페이지
     */
    Page<Resume> findByJobRoleAndExperienceYears(JobRole jobRole, Integer experienceYears, Pageable pageable);

    /**
     * 특정 기술 스택을 포함하는 이력서 조회 (JSON 검색)
     * H2 데이터베이스용으로 수정된 쿼리
     * 
     * @param techSkill 기술 스택
     * @return 이력서 목록
     */
    @Query("SELECT r FROM Resume r WHERE r.techSkills LIKE %:techSkill%")
    List<Resume> findByTechSkill(@Param("techSkill") String techSkill);

    /**
     * 경력 연수별 통계 조회
     * 
     * @return 경력 연수별 이력서 수
     */
    @Query("SELECT r.experienceYears, COUNT(r) FROM Resume r GROUP BY r.experienceYears ORDER BY r.experienceYears")
    List<Object[]> getExperienceYearStatistics();

    /**
     * 직무 역할별 평균 경력 연수 조회
     * 
     * @return 직무 역할별 평균 경력 연수
     */
    @Query("SELECT r.jobRole, AVG(r.experienceYears) FROM Resume r GROUP BY r.jobRole")
    List<Object[]> getAverageExperienceByJobRole();

    /**
     * 최근 생성된 이력서 조회
     * 
     * @param limit 조회할 개수
     * @return 최근 이력서 목록
     */
    @Query("SELECT r FROM Resume r ORDER BY r.createdAt DESC")
    List<Resume> findRecentResumes(Pageable pageable);

    /**
     * 경력 요약으로 이력서 검색 (LIKE 검색)
     * 
     * @param careerSummary 경력 요약 키워드
     * @return 이력서 목록
     */
    List<Resume> findByCareerSummaryContainingIgnoreCase(String careerSummary);

    /**
     * 프로젝트 경험으로 이력서 검색 (LIKE 검색)
     * 
     * @param projectExperience 프로젝트 경험 키워드
     * @return 이력서 목록
     */
    List<Resume> findByProjectExperienceContainingIgnoreCase(String projectExperience);

    /**
     * 복합 조건으로 이력서 검색
     * 
     * @param jobRole 직무 역할
     * @param minExperience 최소 경력 연수
     * @param maxExperience 최대 경력 연수
     * @return 이력서 목록
     */
    @Query("SELECT r FROM Resume r WHERE r.jobRole = :jobRole AND r.experienceYears BETWEEN :minExperience AND :maxExperience")
    List<Resume> findByJobRoleAndExperienceRange(
            @Param("jobRole") JobRole jobRole,
            @Param("minExperience") Integer minExperience,
            @Param("maxExperience") Integer maxExperience
    );

    /**
     * 이력서 존재 여부 확인
     * 
     * @param id 이력서 ID
     * @return 존재 여부
     */
    boolean existsById(Long id);

    /**
     * 직무 역할별 이력서 수 조회
     * 
     * @return 직무 역할별 이력서 수
     */
    @Query("SELECT r.jobRole, COUNT(r) FROM Resume r GROUP BY r.jobRole")
    List<Object[]> getResumeCountByJobRole();
} 