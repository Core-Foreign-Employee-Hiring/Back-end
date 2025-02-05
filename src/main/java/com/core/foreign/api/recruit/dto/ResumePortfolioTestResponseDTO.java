package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.recruit.entity.PortfolioType;
import com.core.foreign.api.recruit.entity.ResumePortfolio;
import lombok.Getter;

@Getter
public class ResumePortfolioTestResponseDTO {
    private PortfolioType PortfolioType;
    private String title;
    private String content;


    public static ResumePortfolioTestResponseDTO from(ResumePortfolio resumePortfolio){
        ResumePortfolioTestResponseDTO dto = new ResumePortfolioTestResponseDTO();
        dto.PortfolioType=resumePortfolio.getPortfolioType();
        dto.title=resumePortfolio.getTitle();
        dto.content=resumePortfolio.getContent();
        return dto;
    }
}
