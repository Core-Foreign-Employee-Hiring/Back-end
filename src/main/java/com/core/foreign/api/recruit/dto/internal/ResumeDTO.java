package com.core.foreign.api.recruit.dto.internal;

import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.recruit.dto.ResumePortfolioDTO;
import com.core.foreign.api.recruit.dto.ResumePortfolioFileResponseDTO;
import com.core.foreign.api.recruit.dto.ResumePortfolioTextResponseDTO;
import com.core.foreign.api.recruit.entity.ApplyMethod;
import com.core.foreign.api.recruit.entity.EvaluationStatus;
import com.core.foreign.api.recruit.entity.RecruitmentStatus;
import com.core.foreign.api.recruit.entity.Resume;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class ResumeDTO {
    private Long resumeId;
    private Long employeeId;
    private Long recruitId;
    private String messageToEmployer;
    private ApplyMethod applyMethod;
    private RecruitmentStatus recruitmentStatus;
    private LocalDate approvedAt;
    private EvaluationStatus isEmployeeEvaluatedByEmployer; // 고용인이 피고용인을 평가했는지 여부
    private LocalDate employeeEvaluationDate; // 고용인이 피고용인을 평가한 날짜
    private EvaluationStatus isEmployerEvaluatedByEmployee; // 피고용인이 고용인을 평가했는지 여부
    private LocalDate employerEvaluationDate; // 피고용인이 고용인을 평가한 날짜
    private boolean isPublic;  // 프리미엄 공고일 때만 유효함.
    private Integer viewCount;  // 프리미엄 공고일 때만 유효함.
    private ContractStatus contractStatus;
    private LocalDate contractCompletionDate;
    private List<ResumePortfolioTextResponseDTO> texts;
    private List<ResumePortfolioFileResponseDTO> files;

    public static ResumeDTO from(Resume resume, ResumePortfolioDTO resumePortfolioDTO){
        ResumeDTO resumeDTO = new ResumeDTO();
        resumeDTO.resumeId = resume.getId();
        resumeDTO.employeeId = resume.getEmployee().getId();
        resumeDTO.recruitId = resume.getRecruit().getId();
        resumeDTO.messageToEmployer = resume.getMessageToEmployer();
        resumeDTO.applyMethod = resume.getApplyMethod();
        resumeDTO.recruitmentStatus = resume.getRecruitmentStatus();
        resumeDTO.approvedAt = resume.getApprovedAt();
        resumeDTO.isEmployeeEvaluatedByEmployer= resume.getIsEmployeeEvaluatedByEmployer();
        resumeDTO.employeeEvaluationDate = resume.getEmployeeEvaluationDate();
        resumeDTO.isEmployerEvaluatedByEmployee= resume.getIsEmployerEvaluatedByEmployee();
        resumeDTO.employerEvaluationDate = resume.getEmployerEvaluationDate();
        resumeDTO.isPublic = resume.isPublic();
        resumeDTO.viewCount = resume.getViewCount();
        resumeDTO.contractStatus = resume.getContractStatus();
        resumeDTO.contractCompletionDate = resume.getContractCompletionDate();
        resumeDTO.texts = resumePortfolioDTO.getTexts();
        resumeDTO.files = resumePortfolioDTO.getFiles();

        return resumeDTO;

    }

}


