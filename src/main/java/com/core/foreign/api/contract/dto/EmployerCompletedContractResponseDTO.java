package com.core.foreign.api.contract.dto;

import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.Resume;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class EmployerCompletedContractResponseDTO {
    private Long recruitId;
    private String title; // 공고 제목
    private LocalDate recruitStartDate; // 모집 시작일
    private LocalDate recruitEndDate; // 모집 종료일
    private String employerName;
    private List<String> workDuration; // 근무 기간 목록
    private List<String> workDays;      // 근무 요일 목록
    private List<String> workTime; // 근무 시간

    public static EmployerCompletedContractResponseDTO from(ContractMetadata contractMetadata){
        EmployerCompletedContractResponseDTO dto = new EmployerCompletedContractResponseDTO();

        Resume resume = contractMetadata.getResume();
        Recruit recruit = resume.getRecruit();
        Employer employer = (Employer) recruit.getEmployer();

        dto.recruitId = resume.getId();
        dto.title= recruit.getTitle();
        dto.recruitStartDate = recruit.getRecruitStartDate();
        dto.recruitEndDate = recruit.getRecruitEndDate();
        dto.employerName = employer.getName();
        dto.workDuration = null;
        dto.workDays = null;
        dto.workTime = null;

        return dto;

    }

}
