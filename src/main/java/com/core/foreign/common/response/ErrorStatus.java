package com.core.foreign.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)

public enum ErrorStatus {
    /**
     * 400 BAD_REQUEST
     */
    VALIDATION_REQUEST_MISSING_EXCEPTION(HttpStatus.BAD_REQUEST, "요청 값이 입력되지 않았습니다."),
    ALREADY_REGISTER_USERID_EXCPETION(HttpStatus.BAD_REQUEST, "이미 등록된 사용자 ID 입니다."),
    ALREADY_REGISTER_EMAIL_EXCPETION(HttpStatus.BAD_REQUEST, "이미 등록된 이메일 입니다."),
    WRONG_PASSWORD_EXCEPTION(HttpStatus.BAD_REQUEST,"비밀번호가 올바르지 않습니다."),
    MISSING_REFRESH_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST,"리프레시 토큰이 입력되지 않았습니다."),
    WRONG_EMAIL_VERIFICATION_CODE_EXCEPTION(HttpStatus.BAD_REQUEST,"이메일 인증코드가 올바르지 않습니다."),
    VALIDATION_EMAIL_FORMAT_EXCEPTION(HttpStatus.BAD_REQUEST,"올바른 이메일 형식이 아닙니다."),
    MISSING_EMAIL_VERIFICATION_EXCPETION(HttpStatus.BAD_REQUEST,"이메일 인증을 진행해주세요."),

    /**
     * 401 UNAUTHORIZED
     */
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"인증되지 않은 사용자입니다."),
    UNAUTHORIZED_REFRESH_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"유효하지 않은 리프레시 토큰입니다."),
    UNAUTHORIZED_EMAIL_VERIFICATION_CODE_EXCEPTION(HttpStatus.UNAUTHORIZED,"이메일 인증코드가 만료되었습니다, 재인증 해주세요."),

    /**
     * 404 NOT_FOUND
     */

    USERID_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 아이디를 찾을 수 없습니다."),

    /**
     * 500 SERVER_ERROR
     */

    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}