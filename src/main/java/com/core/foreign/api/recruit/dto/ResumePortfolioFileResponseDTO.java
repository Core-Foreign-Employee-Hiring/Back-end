package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.recruit.entity.PortfolioType;
import lombok.Getter;

import java.util.List;

@Getter
public class ResumePortfolioFileResponseDTO {
    private PortfolioType portfolioType;
    private String title;
    private List<String> urls;

    public ResumePortfolioFileResponseDTO(PortfolioType portfolioType, String title, List<String> urls) {
        this.portfolioType = portfolioType;
        this.title = title;
        this.urls = urls;
    }
}
