package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.recruit.entity.*;
import lombok.Getter;

@Getter
public class EmployeeApplicationStatusResponseDTO {
    private Long resumeId;

    private String companyName;
    private RecruitmentStatus recruitmentStatus;
    private ContractStatus contractStatus;
    private ApplyMethod applyMethod;


    public static EmployeeApplicationStatusResponseDTO from (Resume resume  ){
        EmployeeApplicationStatusResponseDTO dto = new EmployeeApplicationStatusResponseDTO();
        Recruit recruit = resume.getRecruit();
        Member employer = recruit.getEmployer();

        dto.resumeId = resume.getId();
        dto.companyName=employer.getName();
        dto.recruitmentStatus=resume.getRecruitmentStatus();
        dto.contractStatus = (resume.getRecruitmentStatus() == RecruitmentStatus.APPROVED) ? resume.getContractStatus() : null;
        dto.applyMethod = resume.getApplyMethod();

        return dto;
    }


}
