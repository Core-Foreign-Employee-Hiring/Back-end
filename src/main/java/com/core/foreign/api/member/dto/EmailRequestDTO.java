package com.core.foreign.api.member.dto;

import lombok.Getter;

public class EmailRequestDTO {
    @Getter
    public static class EmailVerificationRequest {
        private String email;
    }

    @Getter
    public static class VerificationCodeRequest {
        private String code;
    }
}

