package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.recruit.entity.ContractStatus;
import com.core.foreign.api.recruit.entity.EvaluationStatus;
import com.core.foreign.api.recruit.entity.RecruitmentStatus;
import com.core.foreign.api.recruit.entity.Resume;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ApplicationResumePreviewResponseDTO {
    private Long resumeId;

    private String name;
    private boolean isMail;
    private LocalDate birthday;  // 생년월일
    private String phoneNumber;  // 전화번호
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
        dto.recruitmentStatus = resume.getRecruitmentStatus();
        dto.evaluationStatus = resume.getRecruitmentStatus()!=RecruitmentStatus.APPROVED?EvaluationStatus.NONE:resume.getEvaluationStatus();
        dto.contractStatus = resume.getContractStatus();
        return dto;
    }


}
