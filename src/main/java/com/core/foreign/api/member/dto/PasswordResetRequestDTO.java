package com.core.foreign.api.member.dto;

import lombok.Getter;

public class PasswordResetRequestDTO {

    @Getter
    public static class PasswordResetRequest {
        private String userId;
        private String name;
        private String email;
    }

    @Getter
    public static class PasswordResetConfirm {
        private String code;
        private String newPassword;
    }
}