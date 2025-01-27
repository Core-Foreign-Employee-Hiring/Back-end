package com.core.foreign.common.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public enum SuccessStatus {

    /**
     * 200
     */
    SEND_REGISTER_SUCCESS(HttpStatus.OK,"회원가입 성공"),
    SEND_LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공"),
    SEND_REISSUE_TOKEN_SUCCESS(HttpStatus.OK,"토큰 재발급 성공"),
    SEND_EMAIL_VERIFICATION_CODE_SUCCESS(HttpStatus.OK,"이메일 인증코드 발송 성공"),
    SEND_EMAIL_VERIFICATION_SUCCESS(HttpStatus.OK,"이메일 코드 인증 성공"),
    SEND_ALLOW_USERID_SUCCESS(HttpStatus.OK,"사용자 ID 사용 가능"),
    SEND_PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "프로필 변경 성공"),
    SEND_SELECT_EMPLOYER_SUCCESS(HttpStatus.OK, "고용주 조회 성공"),
    SEND_EMAIL_DUPLICATION_SUCCESS(HttpStatus.OK,"사용 가능한 이메일"),
    SEND_SELECT_EMPLOYER_COMPANY_INFO_SUCCESS(HttpStatus.OK,"고용주 회사 정보 조회 성공"),
    SEND_NO_DRAFT_SAVE_SUCCESS(HttpStatus.OK,"임시 저장된 공고가 없습니다."),
    SEND_DRAFT_SAVE_SUCCESS(HttpStatus.OK,"임시 저장된 공고 조회 성공"),
    SEND_SELECT_EMPLOYEE_BASIC_RESUME_SUCCESS(HttpStatus.OK,"피고용인 기본 이력서 조회 성공"),
    SEND_EMPLOYEE_BASIC_RESUME_UPDATE_SUCCESS(HttpStatus.OK,"피고용인 기본 이력서 수정 성공"),

    /**
     * 201
     */
    CREATE_RECRUIT_ARTICLE_SUCCESS(HttpStatus.CREATED, "공고 등록 성공"),
    CREATE_DRAFT_RECRUIT_ARTICLE_SUCCESS(HttpStatus.CREATED, "공고 임시 저장 성공"),


    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}