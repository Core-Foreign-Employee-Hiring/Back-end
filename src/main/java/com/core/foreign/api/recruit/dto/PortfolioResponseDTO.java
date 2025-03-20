package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.recruit.entity.Portfolio;
import com.core.foreign.api.recruit.entity.PortfolioType;
import lombok.Getter;

@Getter
public class PortfolioResponseDTO {
    private Long portfolioId;
    private String title;
    private PortfolioType portfolioType;
    private boolean required;
    private Integer maxFileCount;

    public static PortfolioResponseDTO from(Portfolio portfolio) {
        PortfolioResponseDTO dto = new PortfolioResponseDTO();
        dto.portfolioId = portfolio.getId();
        dto.title = portfolio.getTitle();
        dto.portfolioType = portfolio.getType();
        dto.required = portfolio.isRequired();
        dto.maxFileCount = portfolio.getMaxFileCount();

        return dto;

    }


}
