package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.member.dto.EmployeeBasicResumeResponseDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.member.entity.Role;
import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.recruit.entity.EvaluationStatus;
import com.core.foreign.api.recruit.entity.RecruitmentStatus;
import com.core.foreign.api.recruit.entity.Resume;
import lombok.Getter;

import java.util.List;

@Getter
public class ApplicationResumeResponseDTO {
    private Long resumeId;
    private EmployeeBasicResumeResponseDTO employeeBasicResumeResponseDTO;
    private EmployeePortfolioDTO employeePortfolioDTO;
    private String messageToEmployer;
    private List<ResumePortfolioTextResponseDTO> texts;
    private List<ResumePortfolioFileResponseDTO> files;

    private Role role;
    private RecruitmentStatus recruitmentStatus;
    private ContractStatus contractStatus;
    private EvaluationStatus isEmployeeEvaluatedByEmployer; // 고용인이 피고용인을 평가했는지 여부

    private boolean isPublic;

    public ApplicationResumeResponseDTO(Resume resume, EmployeeBasicResumeResponseDTO employeeBasicResumeResponseDTO,
                                        EmployeePortfolioDTO employeePortfolioDTO,
                                        String messageToEmployer, List<ResumePortfolioTextResponseDTO> texts, List<ResumePortfolioFileResponseDTO> files) {
        this.resumeId = resume.getId();
        this.employeeBasicResumeResponseDTO = employeeBasicResumeResponseDTO;
        this.employeePortfolioDTO = employeePortfolioDTO;
        this.messageToEmployer = messageToEmployer;
        this.texts = texts;
        this.files = files;

        this.isEmployeeEvaluatedByEmployer = resume.getIsEmployeeEvaluatedByEmployer();
        this.contractStatus = resume.getContractStatus();
        this.recruitmentStatus = resume.getRecruitmentStatus();

        this.isPublic = resume.isPublic();
    }


    public void setRole(Role role) {
        this.role = role;
    }

    public Long getEmployeeId(){
        return employeeBasicResumeResponseDTO.getEmployeeId();
    }
}
