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
    THIRD_PARTY_CONSENT_REQUIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "개인정보 제3자 제공 동의가 필요합니다."),
    REQUIRED_PORTFOLIO_MISSING_EXCEPTION(HttpStatus.BAD_REQUEST, "필수 포트폴리오가 등록되지 않았습니다."),
    INVALID_RECRUIT_TYPE_EXCEPTION(HttpStatus.BAD_REQUEST, "공고 유형이 올바르지 않습니다."),
    UNAUTHORIZED_RESUME_DELETE_EXCEPTION(HttpStatus.BAD_REQUEST, "다른 사용자의 이력서를 삭제할 수 없습니다."),
    EXCEEDED_RECRUIT_CAPACITY_EXCEPTION(HttpStatus.BAD_REQUEST, "모집 인원을 초과할 수 없습니다."),
    ALREADY_APPROVED_RESUME_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 승인된 이력서입니다."),
    ALREADY_REJECTED_RESUME_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 거절된 이력서입니다."),
    NOT_DRAFT_RECRUIT_EXCEPTION(HttpStatus.BAD_REQUEST,"해당 공고는 임시 저장 상태가 아닙니다."),
    DONT_HAVE_PERMISSION_EXCEPTION(HttpStatus.BAD_REQUEST,"해당 요청을 수행할 권한이 없습니다."),
    ALREADY_ADD_CHILD_COMMENT_EXCEPTION(HttpStatus.BAD_REQUEST,"해당 댓글에는 이미 대댓글이 등록되어 있습니다."),
    EVALUATION_NOT_ALLOWED_BEFORE_APPROVAL_DATE(HttpStatus.BAD_REQUEST, "모집 승인 30일 이후부터 평가하기가 가능합니다."),
    EVALUATION_NOT_ALLOWED_FOR_USER_EXCEPTION(HttpStatus.BAD_REQUEST, "평가할 수 없는 유저입니다."),
    EVALUATION_CATEGORY_IS_EMPTY_EXCEPTION(HttpStatus.BAD_REQUEST, "평가 항목이 비어 있습니다."),
    EVALUATION_ALREADY_COMPLETED_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 평가를 완료했습니다."),
    DUPLICATE_APPLICATION_NOT_ALLOWED_EXCEPTION(HttpStatus.BAD_REQUEST, "중복 지원은 불가합니다."),
    EVALUATION_TYPE_MISMATCH_EXCEPTION(HttpStatus.BAD_REQUEST, "평가 타입이 일치하지 않습니다."),
    TAG_EVALUATION_STATUS_CANNOT_BE_NONE(HttpStatus.BAD_REQUEST, "태그의 평가 상태는 NONE 일 수 없습니다."),
    FILE_COUNT_MISMATCH_EXCEPTION(HttpStatus.BAD_REQUEST, "파일 개수가 맞지 않습니다."),
    EMPLOYER_CANNOT_APPLY_EXCEPTION(HttpStatus.BAD_REQUEST, "고용인은 공고에 지원할 수 없습니다."),
    PASSWORD_VERIFICATION_REQUIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "비밀번호 확인을 진행하세요."),
    NOT_READY_YET_EXCEPTION(HttpStatus.BAD_REQUEST, "아직 준비되지 않았습니다."),
    CONTRACT_TYPE_ALREADY_SELECTED_EXCEPTION(HttpStatus.BAD_REQUEST, "계약서 형태를 이미 선택하였습니다."),
    CONTRACT_TYPE_SELECTION_REQUIRED_EXCEPTION(HttpStatus.BAD_REQUEST, "계약서 형태를 선택하세요."),
    EMPLOYEE_CANNOT_SELECT_CONTRACT_TYPE_EXCEPTION(HttpStatus.BAD_REQUEST, "피고용인은 계약서 형태를 선택할 수 없습니다."),
    FILE_UPLOAD_NOT_ALLOWED_FOR_NON_FILE_CONTRACT_EXCEPTION(HttpStatus.BAD_REQUEST, "파일 업로드 계약서가 아닌 경우 업로드 할 수 없습니다."),





    /**
     * 401 UNAUTHORIZED
     */
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"인증되지 않은 사용자입니다."),
    UNAUTHORIZED_REFRESH_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED,"유효하지 않은 리프레시 토큰입니다."),
    UNAUTHORIZED_EMAIL_VERIFICATION_CODE_EXCEPTION(HttpStatus.UNAUTHORIZED,"이메일 인증코드가 만료되었습니다, 재인증 해주세요."),
    UNAUTHORIZED_SMS_VERIFICATION_CODE_EXCEPTION(HttpStatus.UNAUTHORIZED,"SMS 인증코드가 만료되었습니다, 재인증 해주세요."),
    EXPIRED_PASSWORD_RESET_CODE_EXCEPTION(HttpStatus.UNAUTHORIZED,"비밀번호 초기화 인증코드가 만료되었습니다, 재인증 해주세요."),
    INVALID_USER_EXCEPTION(HttpStatus.UNAUTHORIZED,"해당 시스템에 접근할 권한이 없습니다."),

    /**
     * 404 NOT_FOUND
     */

    NOT_LOGIN_EXCEPTION(HttpStatus.NOT_FOUND,"로그인이 필요합니다."),
    USERID_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 아이디를 찾을 수 없습니다."),
    RECRUIT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"해당 공고를 찾을 수 없습니다."),
    USER_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    PAYMENT_INFO_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"해당 결제 정보를 찾을 수 없습니다."),
    CANCEL_INFO_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"해당 취소 정보를 찾을 수 없습니다."),
    PREMIUM_MANAGE_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"프리미엄 관리 정보가 없습니다."),
    RESUME_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 이력서를 찾을 수 없습니다."),
    ALBAREVIEW_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"알바 후기를 찾을 수 없습니다."),
    PARENT_COMMENT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND,"부모 댓글을 찾을 수 없습니다."),
    PORTFOLIO_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "해당 포트폴리오를 찾을 수 없습니다."),
    CONTRACT_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "계약서를 찾을 수 없습니다."),


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