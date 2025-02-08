package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.recruit.entity.ApplyMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralResumeRequestDTO {
    private String messageToEmployer;
    private boolean thirdPartyConsent;
    private ApplyMethod applyMethod;
}

