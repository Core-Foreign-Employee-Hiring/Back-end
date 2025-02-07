package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.member.dto.EmployeeBasicResumeResponseDTO;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class ApplicationResumeResponseDTO {
    private Long resumeId;
    private EmployeeBasicResumeResponseDTO employeeBasicResumeResponseDTO;
    private EmployeePortfolioDTO employeePortfolioDTO;
    private String messageToEmployer;
    private List<ResumePortfolioTestResponseDTO> texts;
    private List<ResumePortfolioFileResponseDTO> files;

    public ApplicationResumeResponseDTO(Long resumeId, EmployeeBasicResumeResponseDTO employeeBasicResumeResponseDTO,
                                        EmployeePortfolioDTO employeePortfolioDTO,
                                        String messageToEmployer, List<ResumePortfolioTestResponseDTO> texts, List<ResumePortfolioFileResponseDTO> files) {
        this.resumeId = resumeId;
        this.employeeBasicResumeResponseDTO = employeeBasicResumeResponseDTO;
        this.employeePortfolioDTO = employeePortfolioDTO;
        this.messageToEmployer = messageToEmployer;
        this.texts = texts;
        this.files = files;
    }
}
