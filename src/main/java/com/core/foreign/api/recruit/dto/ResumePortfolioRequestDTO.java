package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.recruit.entity.Portfolio;
import com.core.foreign.api.recruit.entity.Resume;
import com.core.foreign.api.recruit.entity.ResumePortfolio;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ResumePortfolioRequestDTO {
    private Long portfolioId;
    private String title;
    private String content;

    public  ResumePortfolio toEntity(Resume resume, Map<Long, Portfolio> portfolios){
        ResumePortfolio build = ResumePortfolio.builder()
                .recruitPortfolioId(portfolioId)
                .title(title)
                .content(content)
                .resume(resume)
                .portfolioType(portfolios.get(portfolioId).getType())
                .build();

        return build;
    }
}
