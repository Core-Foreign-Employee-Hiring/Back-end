package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.recruit.entity.PortfolioType;
import com.core.foreign.api.recruit.entity.ResumePortfolio;
import lombok.Getter;

@Getter
public class ResumePortfolioTextResponseDTO {
    private PortfolioType PortfolioType;
    private String title;
    private String content;


    public static ResumePortfolioTextResponseDTO from(ResumePortfolio resumePortfolio){
        ResumePortfolioTextResponseDTO dto = new ResumePortfolioTextResponseDTO();
        dto.PortfolioType=resumePortfolio.getPortfolioType();
        dto.title=resumePortfolio.getTitle();
        dto.content=resumePortfolio.getContent();
        return dto;
    }
}
