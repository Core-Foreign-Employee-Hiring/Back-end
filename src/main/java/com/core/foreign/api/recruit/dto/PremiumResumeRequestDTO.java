package com.core.foreign.api.recruit.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PremiumResumeRequestDTO {
    private GeneralResumeRequestDTO generalResumeRequestDTO;

    private List<ResumePortfolioRequestDTO> resumePortfolios;

    private boolean portfolioPublic;

}
