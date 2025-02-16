package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.recruit.entity.PortfolioType;
import com.core.foreign.api.recruit.entity.Resume;
import com.core.foreign.api.recruit.entity.ResumePortfolio;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResumePortfolioRequestDTO {
    private Long portfolioId;

    private PortfolioType portfolioType;

    private String title;
    private String content;



    public  ResumePortfolio toEntity(Resume resume){
        ResumePortfolio build = ResumePortfolio.builder()
                .portfolioType(portfolioType)
                .recruitPortfolioId(portfolioId)
                .title(title)
                .content(content)
                .resume(resume)
                .build();

        return build;

    }
}
