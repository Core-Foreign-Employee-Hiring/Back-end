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
    SEND_DRAFT_DETAIL_SUCCESS(HttpStatus.OK,"임시 저장된 공고 내용 조회 성공"),
    SEND_SELECT_EMPLOYEE_BASIC_RESUME_SUCCESS(HttpStatus.OK,"피고용인 기본 이력서 조회 성공"),
    SEND_EMPLOYEE_BASIC_RESUME_UPDATE_SUCCESS(HttpStatus.OK,"피고용인 기본 이력서 수정 성공"),
    SEND_SMS_VERIFICATION_CODE_SUCCESS(HttpStatus.OK,"SMS 인증코드 발송 성공"),
    SEND_VERIFY_SMS_CODE_SUCCESS(HttpStatus.OK,"SMS 코드 인증 성공"),
    SEND_FIND_USERID_SUCCESS(HttpStatus.OK,"사용자 ID 찾기 성공"),
    SEND_COMPANY_VALIDATION_COMPLETED(HttpStatus.OK, "사업자등록 정보 진위 조회 완료"),
    SEND_EMPLOYER_PORTFOLIO_SELECT_SUCCESS(HttpStatus.OK, "피고용인 포트폴리오 조회 성공"),
    SEND_EMPLOYER_DRAFT_PORTFOLIO_SELECT_SUCCESS(HttpStatus.OK, "피고용인 임시 저장 포트폴리오 조회 성공"),
    SEND_EMPLOYER_PORTFOLIO_UPDATE_SUCCESS(HttpStatus.OK, "피고용인 포트폴리오 수정 성공"),
    SEND_PAY_SUCCESS(HttpStatus.OK,"결제 승인 성공"),
    SEND_CANCELED_PAY_SUCCESS(HttpStatus.OK,"결제 취소 성공"),
    SEND_AVAILABLE_RECRUIT_SUCCESS(HttpStatus.OK,"작성 가능 공고 조회 성공"),
    SEND_PASSWORD_VERIFICATION_COMPLETED(HttpStatus.OK, "비밀번호 확인 완료"),
    SEND_UPDATE_USERID_SUCCESS(HttpStatus.OK, "아이디 변경 성공"),
    SEND_UPDATE_USERID_PASSWORD(HttpStatus.OK, "비밀번호 변경 성공"),
    SEND_RECRUIT_ALL_LIST_SUCCESS(HttpStatus.OK,"공고 전체 조회 성공"),
    SEND_PASSWORD_RESET_LINK_SUCCESS(HttpStatus.OK,"비밀번호 초기화 링크 전송 성공"),
    SEND_RECRUIT_DETAIL_SUCCESS(HttpStatus.OK,"공고 상세 조회 성공"),
    SEND_EMPLOYER_RECRUIT_LIST_SUCCESS(HttpStatus.OK, "고용인의 공고 목록 조회 성공"),
    SEND_RECRUITMENT_APPLICATION_STATUS_SUCCESS(HttpStatus.OK, "지원 현황 조회 성공"),
    SEND_APPLICANT_RESUME_SUCCESS(HttpStatus.OK, "지원자의 이력서 조회 성공"),
    SEND_APPLICANTS_FOR_RECRUIT_SUCCESS(HttpStatus.OK, "공고에 지원한 피고용인 목록 조회 성공"),
    UPDATE_RECRUITMENT_STATUS_SUCCESS(HttpStatus.OK, "모집 상태 변경 성공"),
    SEND_PAYMENT_HISTORY_SUCCESS(HttpStatus.OK, "결제 내역 조회 성공"),
    SEND_MY_RESUME_SUCCESS(HttpStatus.OK, "내 이력서 조회 성공"),
    DELETE_MY_RESUME_SUCCESS(HttpStatus.OK, "내 이력서 삭제 성공"),
    UPDATE_RECRUIT_BOOKMARK_STATUS_SUCCESS(HttpStatus.OK, "찜하기 상태 변경 성공"),
    SEND_BOOKMARKED_RECRUITS_SUCCESS(HttpStatus.OK, "찜한 공고 조회 성공"),
    ALBA_REVIEW_DETAIL_SUCCESS(HttpStatus.OK,"알바 후기 상세조회 성공"),
    ALBA_REVIEW_LIST_SUCCESS(HttpStatus.OK,"알바 후기 전체조회 성공"),
    ALBA_REVIEW_UPDATE_SUCCESS(HttpStatus.OK,"알바 후기 수정 성공"),
    ALBA_REVIEW_DELETE_SUCCESS(HttpStatus.OK,"알바 후기 삭제 성공"),
    EVALUATE_EMPLOYEE_SUCCESS(HttpStatus.OK, "평가하기 성공"),
    EVALUATE_VIEW_SUCCESS(HttpStatus.OK, "평가 보기 성공"),
    BASIC_PORTFOLIO_VIEW_SUCCESS(HttpStatus.OK, "기본 포트폴리오 조회 성공"),
    APPLICATION_PORTFOLIO_VIEW_SUCCESS(HttpStatus.OK, "실제 지원 포트폴리오 조회 성공"),
    TAG_VIEW_SUCCESS(HttpStatus.OK, "태그 조회 성공"),




    /**
     * 201
     */
    CREATE_RECRUIT_ARTICLE_SUCCESS(HttpStatus.CREATED, "공고 등록 성공"),
    CREATE_DRAFT_RECRUIT_ARTICLE_SUCCESS(HttpStatus.CREATED, "공고 임시 저장 성공"),
    CREATE_EMPLOYEE_PORTFOLIO_SUCCESS(HttpStatus.CREATED, "피고용인 포트폴리오 등록 성공"),
    CREATE_DRAFT_EMPLOYEE_PORTFOLIO_SUCCESS(HttpStatus.CREATED, "피고용인 포트폴리오 임시 저장 성공"),
    SEND_PAY_INFO_SAVE_SUCCESS(HttpStatus.CREATED,"결제 정보 등록 성공"),
    UPLOAD_IMAGE_SUCCESS(HttpStatus.CREATED, "이미지 업로드 성공"),
    APPLY_RECRUIT_ARTICLE_SUCCESS(HttpStatus.CREATED, "공고 지원 성공"),
    ALBA_REVIEW_CREATE_SUCCESS(HttpStatus.CREATED,"알바 후기 작성 성공"),

    ;

    private final HttpStatus httpStatus;
    private final String message;

    public int getStatusCode() {
        return this.httpStatus.value();
    }
}