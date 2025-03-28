package com.core.foreign.api.recruit.controller;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.recruit.dto.*;
import com.core.foreign.api.recruit.entity.RecruitType;
import com.core.foreign.api.recruit.entity.RecruitmentStatus;
import com.core.foreign.api.recruit.service.RecruitService;
import com.core.foreign.api.recruit.service.ResumeService;
import com.core.foreign.common.SecurityMember;
import com.core.foreign.common.response.ApiResponse;
import com.core.foreign.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Recruit", description = "공고 관련 API 입니다.<br>" +
        "<p>" +
        "[임시 저장된 공고 존재 여부 조회 API + 작성 가능 공고 조회 API + 회사 정보 조회 API(마이페이지(고용주)-내 기업 정보) -> 만약 임시저장 데이터 있다면 -> 해당 공고 퍼블리싱 API / 임시 저장 데이터가 없다면 -> 공고 등록 API")
@RestController
@RequestMapping("/api/v1/recruit")
@RequiredArgsConstructor
public class RecruitController {

    private final RecruitService recruitService;
    private final ResumeService resumeService;

    @Operation(
            summary = "일반 공고 등록 API (태근)",
            description = "일반 공고를 등록합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "title : 공고 제목 (String) <br>"
                    + "recruitStartDate : 모집 시작일 (ex: '2025-01-01') <br>"
                    + "recruitEndDate : 모집 종료일 (상시 모집 시 '2099-12-31')<br>"
                    + "recruitCount : 모집 인원 (ex 5) <br>"
                    + "gender : 성별 (남자: 'male', 여자: 'female', 무관: null) <br>"
                    + "education : 학력 조건 (ex '학력무관') <br>"
                    + "otherConditions : 기타 조건 <br>"
                    + "preferredConditions : 우대 조건 (여러 항목 가능) <br>"
                    + "workDuration : 근무 기간 (ex '1주일이하' 혹은 '기간 협의') <br>"
                    + "workDurationOther : 근무 기간 기타 사항 <br>"
                    + "workTime : 근무 시간 (ex '오전~오후' 혹은 '시간 협의') <br>"
                    + "workTimeOther : 근무 시간 기타 사항 <br>"
                    + "workDays : 근무 요일 (ex '주말 (토, 일)' 혹은 '요일 협의') <br>"
                    + "workDaysOther : 근무 요일 기타 사항 <br>"
                    + "salary : 급여 정보 (ex 14000) <br>"
                    + "salaryType : 급여 형태 (ex '월급') <br>"
                    + "salaryOther : 급여 기타 사항 <br>"
                    + "businessFields : 업직종 리스트 (ex 'FOOD_BEVERAGE','STORE_SALES') <br>"
                    + "applicationMethods : 지원 방법 (ex 'ONLINE','INQUIRY') <br>"
                    + "latitude : 위도 (ex 36.215556) <br>"
                    + "longitude : 경도 (ex 127.251855) <br>"
                    + "zipcode : 우편 번호 (ex '28464') <br>"
                    + "address1 : 주소 (ex '충대로1') <br>"
                    + "address2 : 상세주소 (ex '충북대학교') <br>"
                    + "<p>"
                    + "ENUM : <A href=\"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "공고 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/general", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> createGeneralRecruit(
            @AuthenticationPrincipal SecurityMember securityMember,
            @RequestPart("request") RecruitRequestDTO.GeneralRecruitRequest recruitRequest,
            @RequestPart(value = "posterImage", required = false) MultipartFile posterImage
    ) {
        recruitService.createGeneralRecruit(securityMember.getId(), recruitRequest, posterImage);
        return ApiResponse.success_only(SuccessStatus.CREATE_RECRUIT_ARTICLE_SUCCESS);
    }

    @Operation(summary = "프리미엄 공고 등록 API (태근)",
            description = "프리미엄 공고를 등록합니다.<br>" +
                    "<p>" +
                    "title : 공고 제목 - String <br>" +
                    "recruitStartDate : 모집 시작일 - String (ex '2025-01-01')<br>" +
                    "recruitEndDate : 모집 종료일 - String (ex '2025-01-20') / 상시 모집일 경우 2099-12-31 로 등록<br>" +
                    "recruitCount : 모집 인원 - int (ex 5)<br>" +
                    "gender : 성별 - String (ex 남자 : 'male', 여자 : 'female', 무관일시 : null)<br>" +
                    "education : 학력 조건 - String (ex '학력무관' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다!)<br>" +
                    "otherConditions : 기타 조건 - String <br>" +
                    "preferredConditions : 우대 조건  - String (ex '유사업무 경험' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다!)<br>" +
                    "workDuration : 근무 기간 - String (ex '1주일이하' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다! 또한 기간 협의를 누를경우 '기간 협의'로 등록하면 됩니다!)<br>" +
                    "workDurationOther : 근무 기간 기타 사항 - String (없다면 null)<br>" +
                    "workTime : 근무 시간 - String (ex '오전~오후' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다! 또한 시간 협의를 누를경우 '시간 협의'로 등록하면 됩니다! / 만약 직접 선택시 '시작시간~종료시간' <- 이런 형식으로 등록하면 됩니다!<br>" +
                    "workTimeOther : 근무 시간 기타 사항 - String (없다면 null)<br>" +
                    "workDays : 근무 요일 - String (ex '주말 (토, 일)' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다! 또한 요일 협의를 누른 경우 '요일 협의'로 등록하면 됩니다! / 만약 직접 선택시 (월요일 선택할 경우) '월요일' <- 이런 형식으로 등록하면 됩니다! '<br>" +
                    "workDaysOther : 근무 요일 기타 사항 - String (없다면 null)<br>" +
                    "salary : 급여 정보 - String (ex 14000)<br>" +
                    "salaryType : 급여 형태 - String (ex '월급' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다!)<br>" +
                    "salaryOther : 급여 기타 사항 - String (없다면 null)<br>" +
                    "businessFields : 업직종 리스트 - String (ex 'FOOD_BEVERAGE','STORE_SALES' <- 여러개 일경우 이렇게 하시면 됩니다!) <br>" +
                    "applicationMethods : 지원 방법  - String (ex 'ONLINE','INQUIRY' <- 여러개 일경우 이렇게 하시면 됩니다!<br>" +
                    "latitude : 위도 - Double (ex 36.215556)<br>" +
                    "longitude : 경도- Double (ex 127.251855)<br>" +
                    "zipcode : 우편 번호 - String (ex '28464')<br>" +
                    "address1 : 주소 - String (ex '충대로1')<br>" +
                    "address2 : 상세 주소 - String (ex '충북대학교')<br>" +
                    "<p>" +
                    "Portfolios<br>" +
                    "title : 질문 제목 - String<br>" +
                    "type : 질문 유형 - String (LONG_TEXT : 장문형 / SHORT_TEXT : 단답형 / FILE_UPLOAD : 파일 업로드)<br>" +
                    "required : 필수 질문 - String<br>" +
                    "maxFileCount : 최대 업로드 가능 갯수 - Int (FILE_UPLOAD 일때만, 다른 유형일 경우 null)<br>" +
                    "<p>" +
                    "요청 예시 : <A href=\"https://www.notion.so/1bc244b92af281f9a82dce6cafca896f?v=1bc244b92af281559243000c1a4fef2f&p=1bc244b92af2810b99c7f90aeec98def&pm=s\" target=\"_blank\"> 이동 하기 </A>" +
                    "<p>" +
                    "ENUM : <A href=\"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "공고 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/premium", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> createPremiumRecruit(
            @AuthenticationPrincipal SecurityMember securityMember,
            @RequestPart("request") RecruitRequestDTO.PremiumRecruitRequest recruitRequest,
            @RequestPart(value = "posterImage", required = false) MultipartFile posterImage
    ) {
        recruitService.createPremiumRecruit(securityMember.getId(), recruitRequest, posterImage);
        return ApiResponse.success_only(SuccessStatus.CREATE_RECRUIT_ARTICLE_SUCCESS);
    }

    @Operation(summary = "일반 공고 임시저장 API (태근)",
            description = "일반 공고를 임시저장 합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "title : 공고 제목 (String) <br>"
                    + "recruitStartDate : 모집 시작일 (ex: '2025-01-01') <br>"
                    + "recruitEndDate : 모집 종료일 (상시 모집 시 '2099-12-31')<br>"
                    + "recruitCount : 모집 인원 (ex 5) <br>"
                    + "gender : 성별 (남자: 'male', 여자: 'female', 무관: null) <br>"
                    + "education : 학력 조건 (ex '학력무관') <br>"
                    + "otherConditions : 기타 조건 <br>"
                    + "preferredConditions : 우대 조건 (여러 항목 가능) <br>"
                    + "workDuration : 근무 기간 (ex '1주일이하' 혹은 '기간 협의') <br>"
                    + "workDurationOther : 근무 기간 기타 사항 <br>"
                    + "workTime : 근무 시간 (ex '오전~오후' 혹은 '시간 협의') <br>"
                    + "workTimeOther : 근무 시간 기타 사항 <br>"
                    + "workDays : 근무 요일 (ex '주말 (토, 일)' 혹은 '요일 협의') <br>"
                    + "workDaysOther : 근무 요일 기타 사항 <br>"
                    + "salary : 급여 정보 (ex 14000) <br>"
                    + "salaryType : 급여 형태 (ex '월급') <br>"
                    + "salaryOther : 급여 기타 사항 <br>"
                    + "businessFields : 업직종 리스트 (ex 'FOOD_BEVERAGE','STORE_SALES') <br>"
                    + "applicationMethods : 지원 방법 (ex 'ONLINE','INQUIRY') <br>"
                    + "latitude : 위도 (ex 36.215556) <br>"
                    + "longitude : 경도 (ex 127.251855) <br>"
                    + "zipcode : 우편 번호 (ex '28464') <br>"
                    + "address1 : 주소 (ex '충대로1') <br>"
                    + "address2 : 상세주소 (ex '충북대학교') <br>"
                    + "<p>"
                    + "ENUM : <A href=\"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "공고 임시 저장 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/general/draft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> saveGeneralRecruitDraft(
            @AuthenticationPrincipal SecurityMember securityMember,
            @RequestPart("request") RecruitRequestDTO.GeneralRecruitRequest recruitRequest,
            @RequestPart(value = "posterImage", required = false) MultipartFile posterImage
    ) {
        recruitService.createGeneralDraft(securityMember.getId(), recruitRequest, posterImage);
        return ApiResponse.success_only(SuccessStatus.CREATE_DRAFT_RECRUIT_ARTICLE_SUCCESS);
    }

    @Operation(summary = "임시 저장된 일반 공고 수정 API (태근)",
            description = "임시 저장된 일반 공고 데이터를 수정 합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "title : 공고 제목 (String) <br>"
                    + "recruitStartDate : 모집 시작일 (ex: '2025-01-01') <br>"
                    + "recruitEndDate : 모집 종료일 (상시 모집 시 '2099-12-31')<br>"
                    + "recruitCount : 모집 인원 (ex 5) <br>"
                    + "gender : 성별 (남자: 'male', 여자: 'female', 무관: null) <br>"
                    + "education : 학력 조건 (ex '학력무관') <br>"
                    + "otherConditions : 기타 조건 <br>"
                    + "preferredConditions : 우대 조건 (여러 항목 가능) <br>"
                    + "workDuration : 근무 기간 (ex '1주일이하' 혹은 '기간 협의') <br>"
                    + "workDurationOther : 근무 기간 기타 사항 <br>"
                    + "workTime : 근무 시간 (ex '오전~오후' 혹은 '시간 협의') <br>"
                    + "workTimeOther : 근무 시간 기타 사항 <br>"
                    + "workDays : 근무 요일 (ex '주말 (토, 일)' 혹은 '요일 협의') <br>"
                    + "workDaysOther : 근무 요일 기타 사항 <br>"
                    + "salary : 급여 정보 (ex 14000) <br>"
                    + "salaryType : 급여 형태 (ex '월급') <br>"
                    + "salaryOther : 급여 기타 사항 <br>"
                    + "businessFields : 업직종 리스트 (ex 'FOOD_BEVERAGE','STORE_SALES') <br>"
                    + "applicationMethods : 지원 방법 (ex 'ONLINE','INQUIRY') <br>"
                    + "latitude : 위도 (ex 36.215556) <br>"
                    + "longitude : 경도 (ex 127.251855) <br>"
                    + "zipcode : 우편 번호 (ex '28464') <br>"
                    + "address1 : 주소 (ex '충대로1') <br>"
                    + "address2 : 상세주소 (ex '충북대학교') <br>"
                    + "<p>"
                    + "ENUM : <A href=\"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "공고 임시 저장 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PutMapping(value = "/general/draft/{recruitId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateGeneralDraft(
            @AuthenticationPrincipal SecurityMember securityMember,
            @PathVariable Long recruitId,
            @RequestPart("request") RecruitRequestDTO.GeneralRecruitRequest recruitRequest,
            @RequestPart(value = "posterImage", required = false) MultipartFile posterImage) {

        recruitService.updateGeneralDraft(securityMember.getId(), recruitId, recruitRequest, posterImage);
        return ApiResponse.success_only(SuccessStatus.CREATE_DRAFT_RECRUIT_ARTICLE_SUCCESS);
    }

        @Operation(summary = "일반 공고 퍼블리싱 API (태근)",
            description = "임시 저장한 일반 공고를 최종 퍼블리싱 합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "title : 공고 제목 (String) <br>"
                    + "recruitStartDate : 모집 시작일 (ex: '2025-01-01') <br>"
                    + "recruitEndDate : 모집 종료일 (상시 모집 시 '2099-12-31')<br>"
                    + "recruitCount : 모집 인원 (ex 5) <br>"
                    + "gender : 성별 (남자: 'male', 여자: 'female', 무관: null) <br>"
                    + "education : 학력 조건 (ex '학력무관') <br>"
                    + "otherConditions : 기타 조건 <br>"
                    + "preferredConditions : 우대 조건 (여러 항목 가능) <br>"
                    + "workDuration : 근무 기간 (ex '1주일이하' 혹은 '기간 협의') <br>"
                    + "workDurationOther : 근무 기간 기타 사항 <br>"
                    + "workTime : 근무 시간 (ex '오전~오후' 혹은 '시간 협의') <br>"
                    + "workTimeOther : 근무 시간 기타 사항 <br>"
                    + "workDays : 근무 요일 (ex '주말 (토, 일)' 혹은 '요일 협의') <br>"
                    + "workDaysOther : 근무 요일 기타 사항 <br>"
                    + "salary : 급여 정보 (ex 14000) <br>"
                    + "salaryType : 급여 형태 (ex '월급') <br>"
                    + "salaryOther : 급여 기타 사항 <br>"
                    + "businessFields : 업직종 리스트 (ex 'FOOD_BEVERAGE','STORE_SALES') <br>"
                    + "applicationMethods : 지원 방법 (ex 'ONLINE','INQUIRY') <br>"
                    + "latitude : 위도 (ex 36.215556) <br>"
                    + "longitude : 경도 (ex 127.251855) <br>"
                    + "zipcode : 우편 번호 (ex '28464') <br>"
                    + "address1 : 주소 (ex '충대로1') <br>"
                    + "address2 : 상세주소 (ex '충북대학교') <br>"
                    + "<p>"
                    + "ENUM : <A href=\"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
        )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "공고 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PutMapping(value = "/general/draft/{recruitId}/publish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> publishGeneralDraft(
            @AuthenticationPrincipal SecurityMember securityMember,
            @PathVariable Long recruitId,
            @RequestPart("request") RecruitRequestDTO.GeneralRecruitRequest recruitRequest,
            @RequestPart(value = "posterImage", required = false) MultipartFile posterImage) {

        recruitService.publishGeneralDraft(securityMember.getId(), recruitId, recruitRequest, posterImage);
        return ApiResponse.success_only(SuccessStatus.CREATE_RECRUIT_ARTICLE_SUCCESS);
    }

    @Operation(summary = "프리미엄 공고 임시저장 API (태근)",
            description = "프리미엄 공고를 임시저장 합니다.<br>" +
                    "<p>" +
                    "title : 공고 제목 - String <br>" +
                    "recruitStartDate : 모집 시작일 - String (ex '2025-01-01')<br>" +
                    "recruitEndDate : 모집 종료일 - String (ex '2025-01-20') / 상시 모집일 경우 2099-12-31 로 등록<br>" +
                    "recruitCount : 모집 인원 - int (ex 5)<br>" +
                    "gender : 성별 - String (ex 남자 : 'male', 여자 : 'female', 무관일시 : null)<br>" +
                    "education : 학력 조건 - String (ex '학력무관' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다!)<br>" +
                    "otherConditions : 기타 조건 - String <br>" +
                    "preferredConditions : 우대 조건  - String (ex '유사업무 경험' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다!)<br>" +
                    "workDuration : 근무 기간 - String (ex '1주일이하' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다! 또한 기간 협의를 누를경우 '기간 협의'로 등록하면 됩니다!)<br>" +
                    "workDurationOther : 근무 기간 기타 사항 - String (없다면 null)<br>" +
                    "workTime : 근무 시간 - String (ex '오전~오후' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다! 또한 시간 협의를 누를경우 '시간 협의'로 등록하면 됩니다! / 만약 직접 선택시 '시작시간~종료시간' <- 이런 형식으로 등록하면 됩니다!<br>" +
                    "workTimeOther : 근무 시간 기타 사항 - String (없다면 null)<br>" +
                    "workDays : 근무 요일 - String (ex '주말 (토, 일)' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다! 또한 요일 협의를 누른 경우 '요일 협의'로 등록하면 됩니다! / 만약 직접 선택시 (월요일 선택할 경우) '월요일' <- 이런 형식으로 등록하면 됩니다! '<br>" +
                    "workDaysOther : 근무 요일 기타 사항 - String (없다면 null)<br>" +
                    "salary : 급여 정보 - String (ex 14000)<br>" +
                    "salaryType : 급여 형태 - String (ex '월급' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다!)<br>" +
                    "salaryOther : 급여 기타 사항 - String (없다면 null)<br>" +
                    "businessFields : 업직종 리스트 - String (ex 'FOOD_BEVERAGE','STORE_SALES' <- 여러개 일경우 이렇게 하시면 됩니다!) <br>" +
                    "applicationMethods : 지원 방법  - String (ex 'ONLINE','INQUIRY' <- 여러개 일경우 이렇게 하시면 됩니다!<br>" +
                    "latitude : 위도 - Double (ex 36.215556)<br>" +
                    "longitude : 경도- Double (ex 127.251855)<br>" +
                    "zipcode : 우편 번호 - String (ex '28464')<br>" +
                    "address1 : 주소 - String (ex '충대로1')<br>" +
                    "address2 : 상세 주소 - String (ex '충북대학교')<br>" +
                    "<p>" +
                    "Portfolios<br>" +
                    "title : 질문 제목 - String<br>" +
                    "type : 질문 유형 - String (LONG_TEXT : 장문형 / SHORT_TEXT : 단답형 / FILE_UPLOAD : 파일 업로드)<br>" +
                    "required : 필수 질문 - String<br>" +
                    "maxFileCount : 최대 업로드 가능 갯수 - Int (FILE_UPLOAD 일때만, 다른 유형일 경우 null)<br>" +
                    "<p>" +
                    "요청 예시 : <A href=\"https://www.notion.so/1bc244b92af281f9a82dce6cafca896f?v=1bc244b92af281559243000c1a4fef2f&p=1bc244b92af2810b99c7f90aeec98def&pm=s\" target=\"_blank\"> 이동 하기 </A>" +
                    "<p>" +
                    "ENUM : <A href=\"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "공고 임시 저장 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/premium/draft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> savePremiumRecruitDraft(
            @AuthenticationPrincipal SecurityMember securityMember,
            @RequestPart("request") RecruitRequestDTO.PremiumRecruitRequest recruitRequest,
            @RequestPart(value = "posterImage", required = false) MultipartFile posterImage
    ) {
        recruitService.savePremiumRecruitDraft(securityMember.getId(), recruitRequest, posterImage);
        return ApiResponse.success_only(SuccessStatus.CREATE_DRAFT_RECRUIT_ARTICLE_SUCCESS);
    }

    @Operation(summary = "임시 저장된 프리미엄 공고 수정 API (태근)",
            description = "임시 저장된 프리미엄 공고 데이터를 수정 합니다.<br>" +
                    "<p>" +
                    "title : 공고 제목 - String <br>" +
                    "recruitStartDate : 모집 시작일 - String (ex '2025-01-01')<br>" +
                    "recruitEndDate : 모집 종료일 - String (ex '2025-01-20') / 상시 모집일 경우 2099-12-31 로 등록<br>" +
                    "recruitCount : 모집 인원 - int (ex 5)<br>" +
                    "gender : 성별 - String (ex 남자 : 'male', 여자 : 'female', 무관일시 : null)<br>" +
                    "education : 학력 조건 - String (ex '학력무관' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다!)<br>" +
                    "otherConditions : 기타 조건 - String <br>" +
                    "preferredConditions : 우대 조건  - String (ex '유사업무 경험' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다!)<br>" +
                    "workDuration : 근무 기간 - String (ex '1주일이하' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다! 또한 기간 협의를 누를경우 '기간 협의'로 등록하면 됩니다!)<br>" +
                    "workDurationOther : 근무 기간 기타 사항 - String (없다면 null)<br>" +
                    "workTime : 근무 시간 - String (ex '오전~오후' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다! 또한 시간 협의를 누를경우 '시간 협의'로 등록하면 됩니다! / 만약 직접 선택시 '시작시간~종료시간' <- 이런 형식으로 등록하면 됩니다!<br>" +
                    "workTimeOther : 근무 시간 기타 사항 - String (없다면 null)<br>" +
                    "workDays : 근무 요일 - String (ex '주말 (토, 일)' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다! 또한 요일 협의를 누른 경우 '요일 협의'로 등록하면 됩니다! / 만약 직접 선택시 (월요일 선택할 경우) '월요일' <- 이런 형식으로 등록하면 됩니다! '<br>" +
                    "workDaysOther : 근무 요일 기타 사항 - String (없다면 null)<br>" +
                    "salary : 급여 정보 - String (ex 14000)<br>" +
                    "salaryType : 급여 형태 - String (ex '월급' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다!)<br>" +
                    "salaryOther : 급여 기타 사항 - String (없다면 null)<br>" +
                    "businessFields : 업직종 리스트 - String (ex 'FOOD_BEVERAGE','STORE_SALES' <- 여러개 일경우 이렇게 하시면 됩니다!) <br>" +
                    "applicationMethods : 지원 방법  - String (ex 'ONLINE','INQUIRY' <- 여러개 일경우 이렇게 하시면 됩니다!<br>" +
                    "latitude : 위도 - Double (ex 36.215556)<br>" +
                    "longitude : 경도- Double (ex 127.251855)<br>" +
                    "zipcode : 우편 번호 - String (ex '28464')<br>" +
                    "address1 : 주소 - String (ex '충대로1')<br>" +
                    "address2 : 상세 주소 - String (ex '충북대학교')<br>" +
                    "<p>" +
                    "Portfolios<br>" +
                    "title : 질문 제목 - String<br>" +
                    "type : 질문 유형 - String (LONG_TEXT : 장문형 / SHORT_TEXT : 단답형 / FILE_UPLOAD : 파일 업로드)<br>" +
                    "required : 필수 질문 - String<br>" +
                    "maxFileCount : 최대 업로드 가능 갯수 - Int (FILE_UPLOAD 일때만, 다른 유형일 경우 null)<br>" +
                    "<p>" +
                    "요청 예시 : <A href=\"https://www.notion.so/1bc244b92af281f9a82dce6cafca896f?v=1bc244b92af281559243000c1a4fef2f&p=1bc244b92af2810b99c7f90aeec98def&pm=s\" target=\"_blank\"> 이동 하기 </A>" +
                    "<p>" +
                    "ENUM : <A href=\"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "공고 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PutMapping(value = "/premium/draft/{recruitId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updatePremiumDraft(
            @AuthenticationPrincipal SecurityMember securityMember,
            @PathVariable Long recruitId,
            @RequestPart("request") RecruitRequestDTO.PremiumRecruitRequest recruitRequest,
            @RequestPart(value = "posterImage", required = false) MultipartFile posterImage) {

        recruitService.updatePremiumDraft(securityMember.getId(), recruitId, recruitRequest, posterImage);
        return ApiResponse.success_only(SuccessStatus.CREATE_DRAFT_RECRUIT_ARTICLE_SUCCESS);
    }

        @Operation(summary = "프리미엄 공고 퍼블리싱 API (태근)",
            description = "임시 저장한 프리미엄 공고를 최종 퍼블리싱 합니다.<br>" +
                    "<p>" +
                    "title : 공고 제목 - String <br>" +
                    "recruitStartDate : 모집 시작일 - String (ex '2025-01-01')<br>" +
                    "recruitEndDate : 모집 종료일 - String (ex '2025-01-20') / 상시 모집일 경우 2099-12-31 로 등록<br>" +
                    "recruitCount : 모집 인원 - int (ex 5)<br>" +
                    "gender : 성별 - String (ex 남자 : 'male', 여자 : 'female', 무관일시 : null)<br>" +
                    "education : 학력 조건 - String (ex '학력무관' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다!)<br>" +
                    "otherConditions : 기타 조건 - String <br>" +
                    "preferredConditions : 우대 조건  - String (ex '유사업무 경험' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다!)<br>" +
                    "workDuration : 근무 기간 - String (ex '1주일이하' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다! 또한 기간 협의를 누를경우 '기간 협의'로 등록하면 됩니다!)<br>" +
                    "workDurationOther : 근무 기간 기타 사항 - String (없다면 null)<br>" +
                    "workTime : 근무 시간 - String (ex '오전~오후' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다! 또한 시간 협의를 누를경우 '시간 협의'로 등록하면 됩니다! / 만약 직접 선택시 '시작시간~종료시간' <- 이런 형식으로 등록하면 됩니다!<br>" +
                    "workTimeOther : 근무 시간 기타 사항 - String (없다면 null)<br>" +
                    "workDays : 근무 요일 - String (ex '주말 (토, 일)' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다! 또한 요일 협의를 누른 경우 '요일 협의'로 등록하면 됩니다! / 만약 직접 선택시 (월요일 선택할 경우) '월요일' <- 이런 형식으로 등록하면 됩니다! '<br>" +
                    "workDaysOther : 근무 요일 기타 사항 - String (없다면 null)<br>" +
                    "salary : 급여 정보 - String (ex 14000)<br>" +
                    "salaryType : 급여 형태 - String (ex '월급' <- 피그마, 화면정의서에 있는거랑 네이밍 같게 하면 됩니다!)<br>" +
                    "salaryOther : 급여 기타 사항 - String (없다면 null)<br>" +
                    "businessFields : 업직종 리스트 - String (ex 'FOOD_BEVERAGE','STORE_SALES' <- 여러개 일경우 이렇게 하시면 됩니다!) <br>" +
                    "applicationMethods : 지원 방법  - String (ex 'ONLINE','INQUIRY' <- 여러개 일경우 이렇게 하시면 됩니다!<br>" +
                    "latitude : 위도 - Double (ex 36.215556)<br>" +
                    "longitude : 경도- Double (ex 127.251855)<br>" +
                    "zipcode : 우편 번호 - String (ex '28464')<br>" +
                    "address1 : 주소 - String (ex '충대로1')<br>" +
                    "address2 : 상세 주소 - String (ex '충북대학교')<br>" +
                    "<p>" +
                    "Portfolios<br>" +
                    "title : 질문 제목 - String<br>" +
                    "type : 질문 유형 - String (LONG_TEXT : 장문형 / SHORT_TEXT : 단답형 / FILE_UPLOAD : 파일 업로드)<br>" +
                    "required : 필수 질문 - String<br>" +
                    "maxFileCount : 최대 업로드 가능 갯수 - Int (FILE_UPLOAD 일때만, 다른 유형일 경우 null)<br>" +
                    "<p>" +
                    "요청 예시 : <A href=\"https://www.notion.so/1bc244b92af281f9a82dce6cafca896f?v=1bc244b92af281559243000c1a4fef2f&p=1bc244b92af2810b99c7f90aeec98def&pm=s\" target=\"_blank\"> 이동 하기 </A>" +
                    "<p>" +
                    "ENUM : <A href=\"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
        )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "공고 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PutMapping(value = "/premium/draft/{recruitId}/publish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> publishPremiumDraft(
            @AuthenticationPrincipal SecurityMember securityMember,
            @PathVariable Long recruitId,
            @RequestPart("request") RecruitRequestDTO.PremiumRecruitRequest recruitRequest,
            @RequestPart(value = "posterImage", required = false) MultipartFile posterImage) {

        recruitService.publishPremiumDraft(securityMember.getId(), recruitId, recruitRequest, posterImage);
        return ApiResponse.success_only(SuccessStatus.CREATE_RECRUIT_ARTICLE_SUCCESS);
    }

    @Operation(summary = "임시 저장된 공고 존재 여부 조회 API (태근)",
            description = "현재 사용자가 임시 저장한 공고가 존재하는지 여부를 확인합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "임시 저장된 공고 여부 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/drafts/exist")
    public ResponseEntity<ApiResponse<Boolean>> checkDraftExistence(
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        boolean exists = recruitService.hasDrafts(securityMember.getId());
        return ApiResponse.success(exists ? SuccessStatus.SEND_DRAFT_SAVE_SUCCESS : SuccessStatus.SEND_NO_DRAFT_SAVE_SUCCESS, exists);
    }

    @Operation(summary = "임시 저장된 공고 내용 조회 API (태근)",
            description = "해당 임시 공고의 ID를 받아 임시 저장된 공고의 상세 데이터를 반환합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "recruitId : 임시 저장된 공고 ID"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "임시 저장된 공고 내용 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/drafts/{recruitId}")
    public ResponseEntity<ApiResponse<RecruitResponseDTO>> getDraftById(
            @AuthenticationPrincipal SecurityMember securityMember,
            @PathVariable("recruitId") Long recruitId
    ) {
        RecruitResponseDTO draft = recruitService.getDraftById(recruitId, securityMember.getId());
        return ApiResponse.success(SuccessStatus.SEND_DRAFT_DETAIL_SUCCESS, draft);
    }

    @Operation(summary = "작성 가능 공고 조회 API (태근)",
            description = "현재 회원이 작성할 수 있는 공고 유형을 조회합니다.<br>" +
                    "프리미엄 공고가 가능한 경우 '일반 공고', '프리미엄 공고'를 반환합니다.<br>" +
                    "프리미엄 공고가 불가능한 경우 '일반 공고'만 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "작성 가능 공고 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/publish-available")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableRecruits(
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        List<String> availableRecruits = recruitService.getAvailableRecruits(securityMember.getId());
        return ApiResponse.success(SuccessStatus.SEND_AVAILABLE_RECRUIT_SUCCESS, availableRecruits);
    }

    @Operation(
            summary = "일반 채용 지원하기. API (용범)",
            description = "피고용인이 일반 채용을 지원합니다.<br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "messageToEmployer: 사장님께 한마디<br>" +
                    "thirdPartyConsent: 개인정보 제3자 제공 동의<br>" +
                    "applyMethod: 지원방법<br>" +
                    "<p>"+
                    "요청 예시: <a href=\"https://www.notion.so/api-v1-recruit-general-recruit-id-apply-7dcabb97b3c846cf9639dd22dd024446?pvs=4\" target= \"_blank\">이동 하기</a><br>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "일반 채용 지원 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/general/{recruit-id}/apply")
    public ResponseEntity<ApiResponse<Void>> applyGeneralRecruit(@AuthenticationPrincipal SecurityMember securityMember,
                                                                 @PathVariable("recruit-id") Long recruitId,
                                                                 @RequestBody GeneralResumeRequestDTO dto) {

        resumeService.applyResume(securityMember.getId(), recruitId, dto);
        return ApiResponse.success_only(SuccessStatus.APPLY_RECRUIT_ARTICLE_SUCCESS);
    }

    @Operation(
            summary = "프리미엄 채용 지원하기. API (용범)",
            description = "피고용인이 프리미엄 채용을 지원합니다.<br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "messageToEmployer: 사장님께 한마디<br>" +
                    "thirdPartyConsent: 개인정보 제3자 제공 동의<br>" +
                    "applyMethod: 지원방법(상관 x DB에 insert 할 때 온라인 지원으로 강제함.)<br>" +
                    "portfolioId: 사장님이 등록한 포트폴리오 ID<br>" +
                    "content: 내용/파일 URL<br>" +
                    "portfolioPublic: 공개/비공개<br>"+
                    "<p>"+
                    "요청 예시: <a href=\"https://www.notion.so/api-v1-recruit-premium-recruit-id-apply-e9e429281e8747d98e2727cb36ce3654?pvs=4\" target= \"_blank\">이동 하기</a><br>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프리미엄 채용 지원 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/premium/{recruit-id}/apply")
    public ResponseEntity<ApiResponse<Void>> applyPremiumResume(@AuthenticationPrincipal SecurityMember securityMember,
                                                                @PathVariable("recruit-id") Long recruitId,
                                                                @RequestBody PremiumResumeRequestDTO dto) {

        resumeService.applyPremiumResume(securityMember.getId(), recruitId, dto);
        return ApiResponse.success_only(SuccessStatus.APPLY_RECRUIT_ARTICLE_SUCCESS);
    }

    @Operation(summary = "공고 전체 조회 API (태근)",
            description = "등록되어있는 공고를 전체 조회합니다.<br>"
                    + "상단 점프한 공고는 jump = true 로 표시됩니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "page, size : 페이징 처리 <br>"
                    + "businessFields : 업직종 필터 <br>"
                    + "workDurations : 근무기간 필터 <br>"
                    + "workDays : 근무요일 필터 <br>"
                    + "workTimes : 근무시간 필터 <br>"
                    + "gender : 성별 필터 <br>"
                    + "salaryType : 급여 타입 필터 <br>"
                    + "<p>"
                    + "요청 예시: <a href=\"https://www.notion.so/1bc244b92af281f9a82dce6cafca896f?v=1bc244b92af281559243000c1a4fef2f&p=1bc244b92af281f8b7b3c84b2b9e234f&pm=s\" target= \"_blank\">이동 하기</a><br>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @GetMapping("/search")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공고 전체 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    public ResponseEntity<ApiResponse<PageResponseDTO<RecruitListResponseDTO>>> getAllRecruits(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) List<BusinessField> businessFields,
            @RequestParam(required = false) List<String> workDurations,
            @RequestParam(required = false) List<String> workDays,
            @RequestParam(required = false) List<String> workTimes,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) List<String> salaryType
    ) {
        // 검색조건 생성
        RecruitSearchConditionDTO condition = RecruitSearchConditionDTO.builder()
                .page(page)
                .size(size)
                .businessFields(businessFields)
                .workDurations(workDurations)
                .workDays(workDays)
                .workTimes(workTimes)
                .gender(gender)
                .salaryType(salaryType)
                .build();

        Page<RecruitListResponseDTO> recruitPage = recruitService.getRecruitsWithFilters(condition);
        PageResponseDTO<RecruitListResponseDTO> pageResponse = PageResponseDTO.of(recruitPage);
        return ApiResponse.success(SuccessStatus.SEND_RECRUIT_ALL_LIST_SUCCESS, pageResponse);
    }

    @Operation(
            summary = "프리미엄 공고 전체 조회 API (태근)",
            description = "프리미엄 공고만 조회합니다.<br>"
                    + "상단 점프한 공고는 jump = true 로 표시됩니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "page, size : 페이징 처리 <br>"
                    + "businessFields : 업직종 필터 <br>"
                    + "workDurations : 근무기간 필터 <br>"
                    + "workDays : 근무요일 필터 <br>"
                    + "workTimes : 근무시간 필터 <br>"
                    + "gender : 성별 필터 <br>"
                    + "salaryType : 급여 타입 필터 <br>"
                    + "<p>"
                    + "요청 예시: <a href=\"https://www.notion.so/1bc244b92af281f9a82dce6cafca896f?v=1bc244b92af281559243000c1a4fef2f&p=1bc244b92af2811fbabccbe33003a34c&pm=s\" target= \"_blank\">이동 하기</a><br>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공고 전체 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/premium/search")
    public ResponseEntity<ApiResponse<PageResponseDTO<RecruitListResponseDTO>>> getPremiumRecruits(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) List<BusinessField> businessFields,
            @RequestParam(required = false) List<String> workDurations,
            @RequestParam(required = false) List<String> workDays,
            @RequestParam(required = false) List<String> workTimes,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) List<String> salaryType
    ) {
        // 검색조건 생성
        RecruitSearchConditionDTO condition = RecruitSearchConditionDTO.builder()
                .page(page)
                .size(size)
                .businessFields(businessFields)
                .workDurations(workDurations)
                .workDays(workDays)
                .workTimes(workTimes)
                .gender(gender)
                .salaryType(salaryType)
                .build();

        Page<RecruitListResponseDTO> recruitPage = recruitService.getPremiumRecruitsWithFilters(condition);
        PageResponseDTO<RecruitListResponseDTO> pageResponse = PageResponseDTO.of(recruitPage);
        return ApiResponse.success(SuccessStatus.SEND_RECRUIT_ALL_LIST_SUCCESS, pageResponse);
    }

    @Operation(summary = "공고 상세 조회 API (태근)",
            description = "공고 ID를 받아 해당 공고의 상세 정보를 반환합니다.<br>" +
                    "반환 정보: 회사(점포) 명, 회사 아이콘 이미지, 공고 제목, 급여(시급) 정보 등 공고 등록된 모든 정보를 포함합니다.<br>" +
                    "<p>"
                    + "응답 필드 정보) <br>" +
                    "employerAddress : 회사 주소<br>" +
                    "employerContact : 연락처<br>" +
                    "representative : 담당자명<br>" +
                    "employerEmail : 이메일<br>" +
                    "businessRegistrationNumber : 사업자등록번호<br>" +
                    "paysOnTime: 약속된 급여를 제때 줘요 (개수)<br>" +
                    "keepsContractDates: 계약된 날짜를 잘 지켰어요 (개수)<br>" +
                    "respectsEmployees: 알바생을 존중해줘요 (개수)<br>" +
                    "friendlyBoss: 사장님이 친절해요 (개수)<br>" +
                    "fairWorkload: 업무 강도가 적당해요 (개수)<br>" +
                    "joinCount: 평가에 참여수"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공고 상세 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 공고를 찾을 수 없습니다.")
    })
    @GetMapping(value = "/view", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RecruitDetailResponseDTO>> getRecruitDetail(@AuthenticationPrincipal SecurityMember securityMember,@RequestParam Long recruitId) {

        Long memberId=securityMember==null ?null:securityMember.getId();

        RecruitDetailResponseDTO detailDTO = recruitService.getRecruitDetail(memberId, recruitId);
        return ApiResponse.success(SuccessStatus.SEND_RECRUIT_DETAIL_SUCCESS, detailDTO);
    }

    @Operation(summary = "고용인 지원현황 조회 API (용범)",
            description = "고용인이 등록한 공고의 지원현황을 조회합니다.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "지원 현황 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/apply-status")
    public ResponseEntity<ApiResponse<PageResponseDTO<RecruitmentApplyStatusDTO>>> getRecruitmentApplicationStatus(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                                   @RequestParam("page") Integer page,
                                                                                                                   @RequestParam("size") Integer size) {
        PageResponseDTO<RecruitmentApplyStatusDTO> recruitmentApplyStatus = recruitService.getRecruitmentApplyStatus(securityMember.getId(), page, size);
        return ApiResponse.success(SuccessStatus.SEND_RECRUITMENT_APPLICATION_STATUS_SUCCESS, recruitmentApplyStatus);
    }

    @Operation(
            summary = "지원자 이력서 보기 API (용범)",
            description = "지원자의 이력서를 조회합니다.<br>" +
                    "<p>" +
                    "응답)" +
                    "recruitmentStatus : 모집 상태<br>" +
                    "contractStatus : 계약서 상태(NOT_COMPLETED or COMPLETED)<br>" +
                    "isEmployeeEvaluatedByEmployer : 고용인이 피고용인을 평가했는지<br>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "지원자 이력서 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/resumes/{resume-id}")
    public ResponseEntity<ApiResponse<ApplicationResumeResponseDTO>> getResume(@AuthenticationPrincipal SecurityMember securityMember,
                                                                               @PathVariable("resume-id") Long resumeId) {
        ApplicationResumeResponseDTO resume = resumeService.getResumeForEmployer(resumeId);
        return ApiResponse.success(SuccessStatus.SEND_APPLICANT_RESUME_SUCCESS, resume);
    }

    @Operation(
            summary = "공고에 지원한 피고용인들 조회 API (용범)",
            description = "공고에 지원한 피고용인들 조회.<br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "keyword: 검색<br>"+
                    "recruitmentStatus: 모집 상태<br>"+
                    "contractStatus: 계약서 상태(NOT_COMPLETED 와 COMPLETED 만 이용)<br>"+
                    "<p>"+
                    "요청 예시: <a href=\"https://www.notion.so/api-v1-recruit-recruit-id-resumes-3aad11f7c19a45c7aec63ffc50da152d?pvs=4\" target= \"_blank\">이동 하기</a><br>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "공고에 지원한 피고용인들 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/{recruit-id}/resumes")
    public ResponseEntity<ApiResponse<PageResponseDTO<ApplicationResumePreviewResponseDTO>>> searchApplicationResume(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                                     @PathVariable("recruit-id") Long recruitId,
                                                                                                                     @RequestParam(value = "keyword", required = false) String keyword,
                                                                                                                     @RequestParam(value = "recruitmentStatus", required = false) RecruitmentStatus recruitmentStatus,
                                                                                                                     @RequestParam(value = "contractStatus", required = false) ContractStatus contractStatus,
                                                                                                                     @RequestParam("page") Integer page,
                                                                                                                     @RequestParam("size") Integer size) {

        PageResponseDTO<ApplicationResumePreviewResponseDTO> responseDTOS = resumeService.searchApplicationResume(recruitId, keyword, recruitmentStatus, contractStatus, page, size);
        return ApiResponse.success(SuccessStatus.SEND_APPLICANTS_FOR_RECRUIT_SUCCESS, responseDTOS);
    }

    @Operation(
            summary = "지원서 거절 API (용범)",
            description = "지원서 거절.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "지원서 거절 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/resumes/{resume-id}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectResume(@AuthenticationPrincipal SecurityMember securityMember,
                                                          @PathVariable("resume-id") Long resumeId
    ) {
        resumeService.approveOrRejectResume(securityMember.getId(), resumeId, RecruitmentStatus.REJECTED);
        return ApiResponse.success_only(SuccessStatus.UPDATE_RECRUITMENT_STATUS_SUCCESS);
    }

    @Operation(
            summary = "지원서 승인 API (용범)",
            description = "지원서 승인.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "지원서 승인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/resumes/{resume-id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveApprove(@AuthenticationPrincipal SecurityMember securityMember,
                                                            @PathVariable("resume-id") Long resumeId
    ) {
        resumeService.approveOrRejectResume(securityMember.getId(), resumeId, RecruitmentStatus.APPROVED);
        return ApiResponse.success_only(SuccessStatus.UPDATE_RECRUITMENT_STATUS_SUCCESS);
    }

    @Operation(summary = "피고용인 작성한 이력서 리스트 조회 API (용범)",
            description = "피고용인 작성한 이력서 리스트 조회.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "이력서 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/my-resumes")
    public ResponseEntity<ApiResponse<PageResponseDTO<EmployeeApplicationStatusResponseDTO>>> getMyResumes(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                           @RequestParam("page") Integer page,
                                                                                                           @RequestParam("size") Integer size) {

        PageResponseDTO<EmployeeApplicationStatusResponseDTO> response = resumeService.getMyResumes(securityMember.getId(), page, size);
        return ApiResponse.success(SuccessStatus.SEND_MY_RESUME_SUCCESS, response);
    }

    @Operation(summary = "피고용인 자신이 작성한 이력서 삭제. API (용범)",
            description = "피고용인 자신이 작성한 이력서 삭제.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "이력서 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @DeleteMapping(value = "/my-resumes/{resume-id}/remove")
    public ResponseEntity<ApiResponse<Void>> removeMyResume(@AuthenticationPrincipal SecurityMember securityMember,
                                                            @PathVariable("resume-id") Long resumeId) {

        resumeService.removeMyResume(securityMember.getId(), resumeId);
        return ApiResponse.success_only(SuccessStatus.DELETE_MY_RESUME_SUCCESS);
    }

    @Operation(summary = "공고 찜하기 상태 변경. API (용범)",
            description = "공고 찜하기 상태 변경.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "찜하기 상태 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping(value = "/{recruit-id}/bookmark")
    public ResponseEntity<ApiResponse<Boolean>> flipRecruitBookmark(@AuthenticationPrincipal SecurityMember securityMember,
                                                                    @PathVariable("recruit-id") Long recruitId) {

        boolean b = recruitService.flipRecruitBookmark(securityMember.getId(), recruitId);
        return ApiResponse.success(SuccessStatus.UPDATE_RECRUIT_BOOKMARK_STATUS_SUCCESS, b);
    }

    @Operation(summary = "찜한 공고 조회. API (용범)",
            description = "찜한 공고 조회하기.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "찜한 공고 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/bookmarks")
    public ResponseEntity<ApiResponse<PageResponseDTO<RecruitBookmarkResponseDTO>>> getRecruitBookmarks(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                        @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                        @RequestParam("size") Integer size) {

        PageResponseDTO<RecruitBookmarkResponseDTO> response = recruitService.getMyRecruitBookmark(securityMember.getId(), page, size);
        return ApiResponse.success(SuccessStatus.SEND_BOOKMARKED_RECRUITS_SUCCESS, response);
    }

    @Operation(summary = "고용인의 내 공고 조회 API (용범)",
            description = "고용인의 내가 등록했던 공고 글을 확인할 수 있는 화면입니다.<br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "요청)" +
                    "recruitType: 프리미엄/일반<br>"+
                    "excludeExpired: 만료 제외합니까?<br>"+
                    "응답)" +
                    "recruitId : 공고 ID<br>" +
                    "title : 공고 제목<br>" +
                    "recruitStartDate : 모집 시작일<br>" +
                    "recruitEndDate : 모집 종료일 / 상시 모집일 경우 2099-12-31<br>" +
                    "workDuration : 근무 기간<br>" +
                    "workTime : 근무 시간(직접 선택시 '시작시간~종료시간'<br>" +
                    "workDays : 근무 요일<br>" +
                    "recruitType: 공고 유형<br> "+
                    "zipcode : 우편 번호<br>" +
                    "address1 : 주소1<br>" +
                    "address2 : 주소2<br>" +
                    "<p>"+
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "공고 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/my")
    public ResponseEntity<ApiResponse<PageResponseDTO<MyRecruitResponseDTO>>> getMyRecruits(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                            @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                            @RequestParam("size") Integer size,
                                                                                            @RequestParam(value = "recruitType", required = false) RecruitType recruitType,
                                                                                            @RequestParam(value = "excludeExpired", defaultValue = "false") boolean excludeExpired) {

        PageResponseDTO<MyRecruitResponseDTO> recruits = recruitService.getMyRecruits(securityMember.getId(), page, size, recruitType, excludeExpired);
        return ApiResponse.success(SuccessStatus.SEND_EMPLOYER_RECRUIT_LIST_SUCCESS, recruits);
    }

    @Operation(summary = "고용인 내 임시 공고 공고 조회 API (용범)",
            description = "고용인 내가 등록했던 임시 공고 글을 확인할 수 있는 화면입니다.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "공고 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/draft/my")
    public ResponseEntity<ApiResponse<PageResponseDTO<MyDraftRecruitResponseDTO>>> getMyDraftRecruits(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                      @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                      @RequestParam("size") Integer size) {

        PageResponseDTO<MyDraftRecruitResponseDTO> recruits = recruitService.getMyDraftRecruits(securityMember.getId(), page, size);
        return ApiResponse.success(SuccessStatus.SEND_EMPLOYER_RECRUIT_LIST_SUCCESS, recruits);
    }

    @Operation(summary = "프리미엄 공고의 포트폴리오 조회. API (용범)",
            description = "프리미엄 공고의 포트폴리오 조회.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "포트폴리오 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/{recruit-id}/portfolios")
    public ResponseEntity<ApiResponse<List<PortfolioResponseDTO>>> getPortfolios(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                 @PathVariable("recruit-id") Long recruitId) {

        List<PortfolioResponseDTO> portfolios = recruitService.getPortfolios(recruitId);
        return ApiResponse.success(SuccessStatus.PORTFOLIO_VIEW_SUCCESS, portfolios);
    }

    @Operation(summary = "프리미엄 공고 상단 점프 API (태근)",
            description = "프리미엄 공고를 상단으로 올립니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "recruitId : 프리미엄 공고 ID"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공고 상단 점프 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/premium/{recruitId}/top-jump")
    public ResponseEntity<ApiResponse<Void>> topJumpPremiumRecruit(
            @AuthenticationPrincipal SecurityMember securityMember,
            @PathVariable Long recruitId
    ) {
        recruitService.topJumpPremiumRecruit(recruitId, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.UPDATE_TOP_JUMP_SUCCESS);
    }

    @Operation(summary = "일반 공고 상단 점프 API (태근)",
            description = "일반 공고를 상단으로 올립니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "recruitId : 일반 공고 ID"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공고 상단 점프 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/general/{recruitId}/top-jump")
    public ResponseEntity<ApiResponse<Void>> topJumpGeneralRecruit(
            @AuthenticationPrincipal SecurityMember securityMember,
            @PathVariable Long recruitId
    ) {
        recruitService.topJumpGeneralRecruit(recruitId, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.UPDATE_TOP_JUMP_SUCCESS);
    }

    @Operation(summary = "상단 점프 잔여 횟수 조회 API (태근)",
            description = "현재 사용자가 보유한 프리미엄/일반 상단 점프 횟수를 반환합니다.<br>"
                    + "<p>"
                    + "응답 필드 정보) <br>"
                    + "premiumJumpCount : 프리미엄 공고 점프 가능 횟수, normalJumpCount : 일반 공고 점프 가능 횟수"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "상단 점프 잔여 횟수 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/top-jump-count")
    public ResponseEntity<ApiResponse<TopJumpCountResponseDTO>> getTopJumpCounts(
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        TopJumpCountResponseDTO topJumpCountResponseDTO = recruitService.getTopJumpCounts(securityMember.getId());
        return ApiResponse.success(SuccessStatus.SEND_TOP_JUMP_COUNT_SUCCESS, topJumpCountResponseDTO);
    }

    @Operation(summary = "프리미엄 공고 상단 점프 목록 조회 API (태근)",
            description = "프리미엄 공고 중 상단 점프를 한 공고들을 최신순으로 반환합니다.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프리미엄 공고 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/premium/jump")
    public ResponseEntity<ApiResponse<PageResponseDTO<RecruitListResponseDTO>>> getPremiumJumpedRecruits(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        Page<RecruitListResponseDTO> recruitPage = recruitService.getRecruitsOrderedByJumpDate(RecruitType.PREMIUM, page, size);
        PageResponseDTO<RecruitListResponseDTO> response = PageResponseDTO.of(recruitPage);
        return ApiResponse.success(SuccessStatus.SEND_RECRUIT_ALL_LIST_SUCCESS, response);
    }

    @Operation(summary = "일반 공고 상단 점프 목록 조회 API (태근)",
            description = "일반 공고 중 상단 점프를 한 공고들을 최신순으로 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "일반 공고 목록 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/general/jump")
    public ResponseEntity<ApiResponse<PageResponseDTO<RecruitListResponseDTO>>> getGeneralJumpedRecruits(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ) {
        Page<RecruitListResponseDTO> recruitPage = recruitService.getRecruitsOrderedByJumpDate(RecruitType.GENERAL, page, size);
        PageResponseDTO<RecruitListResponseDTO> response = PageResponseDTO.of(recruitPage);
        return ApiResponse.success(SuccessStatus.SEND_RECRUIT_ALL_LIST_SUCCESS, response);
    }

    @Operation(summary = "메인 공고 조회 API (태근)",
            description = "공고 명, 회사(점포)명을 입력받아 관련 공고를 연관도 높은순으로 반환합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "query : 검색 키워드 <br>"
                    + "page, size : 페이징 처리"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공고 검색 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/keyword/search")
    public ResponseEntity<ApiResponse<PageResponseDTO<RecruitListResponseDTO>>> searchRecruit(
            @RequestParam("query") String query,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        PageResponseDTO<RecruitListResponseDTO> response = recruitService.searchRecruits(query, page, size);
        return ApiResponse.success(SuccessStatus.SEARCH_RECRUIT_SUCESS, response);
    }

    @Operation(summary = "피고용인 공고 지원 가능한지 판단. API (용범)",
            description = "피고용인 공고 지원 가능한지 판단. API" +
                    "<p>" +
                    "응답 코드)<br>" +
                    "200: 공고 지원이 가능한 피고용인입니다.<br>" +
                    "그 외: 지원 불가<br>" +
                    "400: 고용인은 공고에 지원할 수 없습니다, 필수 스펙 및 경력이 없습니다.<br>" +
                    "404: 해당 공고를 찾을 수 없습니다, 포트폴리오(스펙 및 경력)를 찾을 수 없습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공고 지원이 가능한 피고용인입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "고용인은 공고에 지원할 수 없습니다, 필수 스펙 및 경력이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 공고를 찾을 수 없습니다, 포트폴리오(스펙 및 경력)를 찾을 수 없습니다.")
    })
    @PostMapping("/{recruit-id}/validate-application")
    public ResponseEntity<ApiResponse<Void>> isEmployeeEligibleForRecruitment(@AuthenticationPrincipal SecurityMember securityMember,
                                                                              @PathVariable("recruit-id") Long recruitId) {

        recruitService.isEmployeeEligibleForRecruitment(securityMember.getId(), recruitId);
        return ApiResponse.success_only(SuccessStatus.EMPLOYEE_ELIGIBLE_FOR_APPLICATION);
    }
}