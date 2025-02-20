package com.core.foreign.api.recruit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopJumpCountResponseDTO {
    private int premiumJumpCount;
    private int normalJumpCount;
}
