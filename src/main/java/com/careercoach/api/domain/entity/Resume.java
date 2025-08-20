package com.careercoach.api.domain.entity;

import com.careercoach.api.domain.enums.InterviewDifficulty;
import com.careercoach.api.domain.enums.JobRole;
import com.careercoach.api.util.TechSkillsConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 이력서 엔티티
 * DDD 원칙에 따라 비즈니스 로직을 포함한 도메인 엔티티입니다.
 */
@Entity
@Table(name = "resumes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "career_summary", nullable = false, length = 255)
    private String careerSummary;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_role", nullable = false, length = 50)
    private JobRole jobRole;

    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears;

    @Column(name = "project_experience", columnDefinition = "TEXT")
    private String projectExperience;

    @Convert(converter = TechSkillsConverter.class)
    @Column(name = "tech_skills", columnDefinition = "JSON")
    private List<String> techSkills;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Resume(String careerSummary, JobRole jobRole, Integer experienceYears, 
                  String projectExperience, List<String> techSkills) {
        this.careerSummary = careerSummary;
        this.jobRole = jobRole;
        this.experienceYears = experienceYears;
        this.projectExperience = projectExperience;
        this.techSkills = techSkills;
    }

    /**
     * 면접 난이도를 계산하는 비즈니스 로직
     * 경력 5년 이상은 SENIOR, 미만은 JUNIOR를 반환합니다.
     * 
     * @return 면접 난이도
     */
    public InterviewDifficulty calculateInterviewDifficulty() {
        if (experienceYears >= 5) {
            return InterviewDifficulty.SENIOR;
        } else if (experienceYears >= 2) {
            return InterviewDifficulty.MIDDLE;
        } else {
            return InterviewDifficulty.JUNIOR;
        }
    }

    /**
     * 이력서 정보를 업데이트하는 메서드
     * 
     * @param careerSummary 경력 요약
     * @param jobRole 직무 역할
     * @param experienceYears 경력 연수
     * @param projectExperience 프로젝트 경험
     * @param techSkills 기술 스택
     */
    public void updateResume(String careerSummary, JobRole jobRole, Integer experienceYears,
                           String projectExperience, List<String> techSkills) {
        this.careerSummary = careerSummary;
        this.jobRole = jobRole;
        this.experienceYears = experienceYears;
        this.projectExperience = projectExperience;
        this.techSkills = techSkills;
    }

    /**
     * 기술 스택에 특정 기술이 포함되어 있는지 확인하는 메서드
     * 
     * @param skill 확인할 기술
     * @return 포함 여부
     */
    public boolean hasSkill(String skill) {
        return techSkills != null && techSkills.stream()
                .anyMatch(s -> s.equalsIgnoreCase(skill));
    }

    /**
     * 경력 수준을 문자열로 반환하는 메서드
     * 
     * @return 경력 수준 문자열
     */
    public String getExperienceLevel() {
        if (experienceYears >= 5) {
            return "시니어";
        } else if (experienceYears >= 2) {
            return "미들";
        } else {
            return "주니어";
        }
    }
} 