package com.careercoach.api.dto.request;

import com.careercoach.api.domain.enums.JobRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 이력서 생성 요청 DTO
 * API 요청 본문을 받는 데이터 전송 객체입니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateResumeRequest {

    @NotBlank(message = "경력 요약은 필수입니다.")
    @Size(max = 255, message = "경력 요약은 255자를 초과할 수 없습니다.")
    private String careerSummary;

    @NotNull(message = "직무 역할은 필수입니다.")
    private JobRole jobRole;

    @NotNull(message = "경력 연수는 필수입니다.")
    @PositiveOrZero(message = "경력 연수는 0 이상이어야 합니다.")
    private Integer experienceYears;

    @Size(max = 5000, message = "프로젝트 경험은 5000자를 초과할 수 없습니다.")
    private String projectExperience;

    private List<String> techSkills;
} 