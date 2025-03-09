package com.core.foreign.api.recruit.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ResumePortfolioDTO {
    private List<ResumePortfolioTextResponseDTO> texts;
    private List<ResumePortfolioFileResponseDTO> files;

    public static ResumePortfolioDTO from(List<ResumePortfolioTextResponseDTO> texts, List<ResumePortfolioFileResponseDTO> files) {
        ResumePortfolioDTO dto = new ResumePortfolioDTO();
        dto.texts = texts;
        dto.files = files;

        return dto;
    }

}
