package com.core.foreign.api.portfolio.dto;

import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.recruit.dto.ResumePortfolioFileResponseDTO;
import com.core.foreign.api.recruit.dto.ResumePortfolioTextResponseDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class ApplicationPortfolioResponseDTO {
    private Long resumeId;
    private BasicPortfolioResponseDTO basicPortfolioResponseDTO;
    private EmployeePortfolioDTO employeePortfolioDTO;
    private List<ResumePortfolioTextResponseDTO> texts;
    private List<ResumePortfolioFileResponseDTO> files;


    public ApplicationPortfolioResponseDTO(Long resumeId, BasicPortfolioResponseDTO basicPortfolioResponseDTO,
                                           EmployeePortfolioDTO employeePortfolioDTO,
                                           List<ResumePortfolioTextResponseDTO> texts, List<ResumePortfolioFileResponseDTO> files) {
        this.resumeId = resumeId;
        this.basicPortfolioResponseDTO = basicPortfolioResponseDTO;
        this.employeePortfolioDTO = employeePortfolioDTO;
        this.texts = texts;
        this.files = files;

    }
}
