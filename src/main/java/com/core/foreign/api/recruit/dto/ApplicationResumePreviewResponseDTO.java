package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.recruit.entity.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ApplicationResumePreviewResponseDTO {
    private Long resumeId;

    private String name;
    private boolean isMail;
    private LocalDate birthday;  // 생년월일
    private String phoneNumber;  // 전화번호
    private ApplyMethod applyMethod;
    private RecruitmentStatus recruitmentStatus;
    private EvaluationStatus evaluationStatus;
    private ContractStatus contractStatus;


    public static ApplicationResumePreviewResponseDTO from(Resume resume){
        ApplicationResumePreviewResponseDTO dto = new ApplicationResumePreviewResponseDTO();
        Employee employee = resume.getEmployee();

        dto.resumeId = resume.getId();
        dto.name = employee.getName();
        dto.isMail= employee.isMale();
        dto.birthday = employee.getBirthday();
        dto.phoneNumber = employee.getPhoneNumber();
        dto.applyMethod=resume.getApplyMethod();
        dto.recruitmentStatus = resume.getRecruitmentStatus();
        dto.evaluationStatus = resume.getRecruitmentStatus()!=RecruitmentStatus.APPROVED?EvaluationStatus.NONE:resume.getIsEmployeeEvaluatedByEmployer();
        dto.contractStatus = resume.getContractStatus();
        return dto;
    }


}
