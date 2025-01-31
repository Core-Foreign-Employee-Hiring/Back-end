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
    ALREADY_REGISTER_PHONENUMBER_EXCPETION(HttpStatus.BAD_REQUEST, "이미 등록된 전화번호 입니다."),
    WRONG_PASSWORD_EXCEPTION(HttpStatus.BAD_REQUEST,"비밀번호가 올바르지 않습니다."),
    MISSING_REFRESH_TOKEN_EXCEPTION(HttpStatus.BAD_REQUEST,"리프레시 토큰이 입력되지 않았습니다."),
    WRONG_EMAIL_VERIFICATION_CODE_EXCEPTION(HttpStatus.BAD_REQUEST,"이메일 인증코드가 올바르지 않습니다."),
    VALIDATION_EMAIL_FORMAT_EXCEPTION(HttpStatus.BAD_REQUEST,"올바른 이메일 형식이 아닙니다."),
    MISSING_EMAIL_VERIFICATION_EXCEPTION(HttpStatus.BAD_REQUEST,"이메일 인증을 진행해주세요."),
    MISSING_PHONENUMBER_VERIFICATION_EXCEPTION(HttpStatus.BAD_REQUEST,"전화번호 인증을 진행해주세요."),
    ONLY_ADD_RECRUIT_ARTICLE_EMPLOYER_EXCEPTION(HttpStatus.BAD_REQUEST,"고용주만 공고 등록이 가능합니다."),
    NOT_ENOUGH_PREMIUM_COUNT_EXCEPTION(HttpStatus.BAD_REQUEST,"프리미엄 공고 등록 가능 횟수가 부족합니다."),
    ALEADY_PUBLISHED_RECRUIT_ARTICLE_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 퍼블리싱된 공고 입니다."),
    VALIDATION_PHONE_FORMAT_EXCEPTION(HttpStatus.BAD_REQUEST, "휴대폰 번호 형식이 올바르지 않습니다."),
    WRONG_SMS_VERIFICATION_CODE_EXCEPTION(HttpStatus.BAD_REQUEST,"SMS 인증코드가 올바르지 않습니다."),
    MISSING_BUSINESS_REGISTRATION_VERIFICATION_EXCEPTION(HttpStatus.BAD_REQUEST, "사업자등록번호 인증을 진행해주세요."),
    WRONG_PAY_AMOUNT_EXCEPTION(HttpStatus.BAD_REQUEST,"결제 금액 불일치: 요청된 금액과 초기 금액이 다릅니다."),
    ALREADY_CANCELED_PAYMENT_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 취소된 결제입니다."),
    ALREADY_DONE_PAYMENT_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 승인된 결제입니다."),
    ALREADY_READY_PAYMENT_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 등록된 주문입니다."),
    LEAK_PREMIUM_RECRUIT_PUBLISH_COUNT_EXCEPTION(HttpStatus.BAD_REQUEST,"프리미엄 공고 등록 가능 횟수가 부족합니다."),
    INVALID_PASSWORD_RESET_CODE_EXCEPTION(HttpStatus.BAD_REQUEST,"유효하지 않은 비밀번호 초기화 인증코드 입니다."),

    /**
     * 401 UNAUTHORIZED
     */
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"인증되지 않은 사용자입니다."),
    UNAUTHORIZED_REFRESH_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"유효하지 않은 리프레시 토큰입니다."),
    UNAUTHORIZED_EMAIL_VERIFICATION_CODE_EXCEPTION(HttpStatus.UNAUTHORIZED,"이메일 인증코드가 만료되었습니다, 재인증 해주세요."),
    UNAUTHORIZED_SMS_VERIFICATION_CODE_EXCEPTION(HttpStatus.UNAUTHORIZED,"SMS 인증코드가 만료되었습니다, 재인증 해주세요."),
    EXPIRED_PASSWORD_RESET_CODE_EXCEPTION(HttpStatus.UNAUTHORIZED,"비밀번호 초기화 인증코드가 만료되었습니다, 재인증 해주세요."),

    /**
     * 404 NOT_FOUND
     */

    USERID_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 아이디를 찾을 수 없습니다."),
    RECRUIT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"해당 공고를 찾을 수 없습니다."),
    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    PAYMENT_INFO_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"해당 결제 정보를 찾을 수 없습니다."),
    CANCEL_INFO_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"해당 취소 정보를 찾을 수 없습니다."),
    PREMIUM_MANAGE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"프리미엄 관리 정보가 없습니다."),

    /**
     * 500 SERVER_ERROR
     */
    FAIL_UPLOAD_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"파일 업로드 실패하였습니다."),
    SMS_SEND_FAILED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"SMS 전송에 실패하였습니다."),
    INTERNAL_FAIL_PAY_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"내부 시스템 오류로 인해 자동 결제 취소 되었습니다."),
    FAIL_PAY_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"결제 승인 요청 실패하였습니다."),
    FAIL_PAY_CANCEL_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"결제 취소 요청 실패하였습니다."),
    FAIL_PAYING_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR,"결제 요청 중 예외가 발생하였습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}