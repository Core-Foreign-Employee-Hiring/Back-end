package com.core.foreign.api.member.dto;

import lombok.Getter;

public class SmsRequestDTO {
    @Getter
    public static class SmsVerificationRequest {
        private String phoneNumber;
    }

    @Getter
    public static class VerificationCodeRequest {
        private String code;
    }
}
