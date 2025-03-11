package com.core.foreign.api.recruit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PremiumResumeRequestDTO {
    private GeneralResumeRequestDTO generalResumeRequestDTO;

    private List<ResumePortfolioRequestDTO> resumePortfolios;

    @JsonProperty("public")
    private boolean isPublic;


    public boolean isPublic() {
        return isPublic;
    }
}
