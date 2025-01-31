package com.core.foreign.api.recruit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralResumeRequestDTO {
    private String messageToEmployer;
    private boolean thirdPartyConsent;
}

