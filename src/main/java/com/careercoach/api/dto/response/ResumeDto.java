package com.careercoach.api.dto.response;

import com.careercoach.api.domain.enums.InterviewDifficulty;
import com.careercoach.api.domain.enums.JobRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 이력서 응답 DTO
 * 서비스 계층과 컨트롤러 계층 간 데이터 전달용 객체입니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeDto {

    private Long id;
    private String careerSummary;
    private JobRole jobRole;
    private Integer experienceYears;
    private String projectExperience;
    private List<String> techSkills;
    private LocalDateTime createdAt;
    private InterviewDifficulty interviewDifficulty;
    private String experienceLevel;

    /**
     * Resume 엔티티로부터 ResumeDto를 생성하는 정적 팩토리 메서드
     * 
     * @param resume Resume 엔티티
     * @return ResumeDto
     */
    public static ResumeDto from(com.careercoach.api.domain.entity.Resume resume) {
        return ResumeDto.builder()
                .id(resume.getId())
                .careerSummary(resume.getCareerSummary())
                .jobRole(resume.getJobRole())
                .experienceYears(resume.getExperienceYears())
                .projectExperience(resume.getProjectExperience())
                .techSkills(resume.getTechSkills())
                .createdAt(resume.getCreatedAt())
                .interviewDifficulty(resume.calculateInterviewDifficulty())
                .experienceLevel(resume.getExperienceLevel())
                .build();
    }
} 