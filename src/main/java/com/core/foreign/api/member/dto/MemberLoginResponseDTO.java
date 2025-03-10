package com.core.foreign.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberLoginResponseDTO {
    private String name;
    private String userId;
    private String accessToken;
    private String refreshToken;
    private String role;
}
