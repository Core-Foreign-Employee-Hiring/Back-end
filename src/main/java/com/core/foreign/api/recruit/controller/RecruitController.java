package com.core.foreign.api.recruit.controller;

import com.core.foreign.api.recruit.dto.*;
import org.springframework.data.domain.Page;
import com.core.foreign.api.recruit.dto.GeneralResumeRequestDTO;
import com.core.foreign.api.recruit.dto.PremiumResumeRequestDTO;
import com.core.foreign.api.recruit.dto.RecruitRequestDTO;
import com.core.foreign.api.recruit.dto.RecruitResponseDTO;
import com.core.foreign.api.recruit.service.RecruitService;
import com.core.foreign.api.recruit.service.ResumeService;
import com.core.foreign.common.SecurityMember;
import com.core.foreign.common.response.ApiResponse;
import com.core.foreign.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Recruit", description = "공고 관련 API 입니다.<br>" +
        "<p>" +
        "[사용자 임시저장 데이터조회 API + 작성 가능 공고 조회 API + 회사 정보 조회 API(마이페이지(고용주)-내 기업 정보) -> 만약 임시저장 데이터 있다면 -> 해당 공고 퍼블리싱 API / 임시 저장 데이터가 없다면 -> 공고 등록 API")
@RestController
@RequestMapping("/api/v1/recruit")
@RequiredArgsConstructor
public class RecruitController {

    private final RecruitService recruitService;
    private final ResumeService resumeService;

    @Operation(summary = "일반 공고 등록 API",
            description = "일반 공고를 등록합니다.<br>" +
                    "<p>" +
                    "title : 공고 제목<br>" +
                    "recruitStartDate : 모집 시작일<br>" +
                    "recruitEndDate : 모집 종료일 / 상시 모집일 경우 2099-12-31<br>" +
                    "recruitCount : 모집 인원<br>" +
                    "gender : 성별 (무관일시 null)<br>" +
                    "education : 학력 조건<br>" +
                    "otherConditions : 기타 조건<br>" +
                    "preferredConditions : 우대 조건 리스트<br>" +
                    "workDuration : 근무 기간<br>" +
                    "workTime : 근무 시간(직접 선택시 '시작시간~종료시간'<br>" +
                    "workDays : 근무 요일<br>" +
                    "workDaysOther : 근무 요일 기타 사항(없다면 null)<br>" +
                    "salary : 급여 정보<br>" +
                    "salaryType : 급여 형태 (월급, 시급 등)<br>" +
                    "businessFields : 업직종 리스트<br>" +
                    "applicationMethods : 지원 방법<br>" +
                    "latitude : 위도<br>" +
                    "longitude : 경도<br>" +
                    "zipcode : 우편 번호<br>" +
                    "address1 : 주소<br>" +
                    "address2 : 상세 주소<br>")
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

    @Operation(summary = "프리미엄 공고 등록 API",
            description = "프리미엄 공고를 등록합니다.<br>" +
                    "<p>" +
                    "title : 공고 제목<br>" +
                    "recruitStartDate : 모집 시작일<br>" +
                    "recruitEndDate : 모집 종료일 / 상시 모집일 경우 2099-12-31<br>" +
                    "recruitCount : 모집 인원<br>" +
                    "gender : 성별 (무관일시 null)<br>" +
                    "education : 학력 조건<br>" +
                    "otherConditions : 기타 조건<br>" +
                    "preferredConditions : 우대 조건 리스트<br>" +
                    "workDuration : 근무 기간<br>" +
                    "workTime : 근무 시간(직접 선택시 '시작시간~종료시간'<br>" +
                    "workDays : 근무 요일<br>" +
                    "workDaysOther : 근무 요일 기타 사항(없다면 null)<br>" +
                    "salary : 급여 정보<br>" +
                    "salaryType : 급여 형태 (월급, 시급 등)<br>" +
                    "businessFields : 업직종 리스트<br>" +
                    "applicationMethods : 지원 방법<br>" +
                    "latitude : 위도<br>" +
                    "longitude : 경도<br>" +
                    "zipcode : 우편 번호<br>" +
                    "address1 : 주소<br>" +
                    "address2 : 상세 주소<br>" +
                    "<p>" +
                    "Portfolios<br>" +
                    "title : 질문 제목<br>" +
                    "type : 질문 유형(LONG_TEXT : 장문형 / SHORT_TEXT : 단답형 / FILE_UPLOAD : 파일 업로드)<br>" +
                    "required : 필수 질문<br>" +
                    "maxFileCount : 최대 업로드 가능 갯수(FILE_UPLOAD 일때만, 다른 유형일 경우 null)<br>" +
                    "maxFileSize : 최대 업로드 가능 파일 사이즈(FILE_UPLOAD 일때만, 다른 유형일 경우 null)<br>")
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

    @Operation(summary = "일반 공고 임시저장 API",
            description = "일반 공고를 임시저장 합니다.<br>" +
                    "<p>" +
                    "title : 공고 제목<br>" +
                    "recruitStartDate : 모집 시작일<br>" +
                    "recruitEndDate : 모집 종료일 / 상시 모집일 경우 2099-12-31<br>" +
                    "recruitCount : 모집 인원<br>" +
                    "gender : 성별 (무관일시 null)<br>" +
                    "education : 학력 조건<br>" +
                    "otherConditions : 기타 조건<br>" +
                    "preferredConditions : 우대 조건 리스트<br>" +
                    "workDuration : 근무 기간<br>" +
                    "workTime : 근무 시간(직접 선택시 '시작시간~종료시간'<br>" +
                    "workDays : 근무 요일<br>" +
                    "workDaysOther : 근무 요일 기타 사항(없다면 null)<br>" +
                    "salary : 급여 정보<br>" +
                    "salaryType : 급여 형태 (월급, 시급 등)<br>" +
                    "businessFields : 업직종 리스트<br>" +
                    "applicationMethods : 지원 방법<br>" +
                    "latitude : 위도<br>" +
                    "longitude : 경도<br>" +
                    "zipcode : 우편 번호<br>" +
                    "address1 : 주소<br>" +
                    "address2 : 상세 주소<br>")
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
        recruitService.saveGeneralRecruitDraft(securityMember.getId(), recruitRequest, posterImage);
        return ApiResponse.success_only(SuccessStatus.CREATE_DRAFT_RECRUIT_ARTICLE_SUCCESS);
    }

    @Operation(summary = "프리미엄 공고 임시저장 API",
            description = "프리미엄 공고를 임시저장 합니다.<br>" +
                    "<p>" +
                    "title : 공고 제목<br>" +
                    "recruitStartDate : 모집 시작일<br>" +
                    "recruitEndDate : 모집 종료일 / 상시 모집일 경우 2099-12-31<br>" +
                    "recruitCount : 모집 인원<br>" +
                    "gender : 성별 (무관일시 null)<br>" +
                    "education : 학력 조건<br>" +
                    "otherConditions : 기타 조건<br>" +
                    "preferredConditions : 우대 조건 리스트<br>" +
                    "workDuration : 근무 기간<br>" +
                    "workTime : 근무 시간(직접 선택시 '시작시간~종료시간'<br>" +
                    "workDays : 근무 요일<br>" +
                    "workDaysOther : 근무 요일 기타 사항(없다면 null)<br>" +
                    "salary : 급여 정보<br>" +
                    "salaryType : 급여 형태 (월급, 시급 등)<br>" +
                    "businessFields : 업직종 리스트<br>" +
                    "applicationMethods : 지원 방법<br>" +
                    "latitude : 위도<br>" +
                    "longitude : 경도<br>" +
                    "zipcode : 우편 번호<br>" +
                    "address1 : 주소<br>" +
                    "address2 : 상세 주소<br>" +
                    "<p>" +
                    "Portfolios<br>" +
                    "title : 질문 제목<br>" +
                    "type : 질문 유형(LONG_TEXT : 장문형 / SHORT_TEXT : 단답형 / FILE_UPLOAD : 파일 업로드)<br>" +
                    "required : 필수 질문<br>" +
                    "maxFileCount : 최대 업로드 가능 갯수(FILE_UPLOAD 일때만, 다른 유형일 경우 null)<br>" +
                    "maxFileSize : 최대 업로드 가능 파일 사이즈(FILE_UPLOAD 일때만, 다른 유형일 경우 null)<br>")
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

    @Operation(summary = "일반 공고 퍼블리싱 API",
            description = "임시 저장한 일반 공고를 최종 퍼블리싱 합니다.<br>" +
                    "<p>" +
                    "title : 공고 제목<br>" +
                    "recruitStartDate : 모집 시작일<br>" +
                    "recruitEndDate : 모집 종료일 / 상시 모집일 경우 2099-12-31<br>" +
                    "recruitCount : 모집 인원<br>" +
                    "gender : 성별 (무관일시 null)<br>" +
                    "education : 학력 조건<br>" +
                    "otherConditions : 기타 조건<br>" +
                    "preferredConditions : 우대 조건 리스트<br>" +
                    "workDuration : 근무 기간<br>" +
                    "workTime : 근무 시간(직접 선택시 '시작시간~종료시간'<br>" +
                    "workDays : 근무 요일<br>" +
                    "workDaysOther : 근무 요일 기타 사항(없다면 null)<br>" +
                    "salary : 급여 정보<br>" +
                    "salaryType : 급여 형태 (월급, 시급 등)<br>" +
                    "businessFields : 업직종 리스트<br>" +
                    "applicationMethods : 지원 방법<br>" +
                    "latitude : 위도<br>" +
                    "longitude : 경도<br>" +
                    "zipcode : 우편 번호<br>" +
                    "address1 : 주소<br>" +
                    "address2 : 상세 주소<br>")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "공고 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PutMapping(value = "/general/{recruitId}/publish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> publishGeneralRecruit(
            @PathVariable Long recruitId,
            @RequestPart("request") RecruitRequestDTO.GeneralRecruitRequest recruitRequest,
            @RequestPart(value = "posterImage", required = false) MultipartFile posterImage
    ) {
        recruitService.publishGeneralRecruit(recruitId, recruitRequest, posterImage);
        return ApiResponse.success_only(SuccessStatus.CREATE_RECRUIT_ARTICLE_SUCCESS);
    }

    @Operation(summary = "프리미엄 공고 퍼블리싱 API",
            description = "임시 저장한 프리미엄 공고를 최종 퍼블리싱 합니다.<br>" +
                    "<p>" +
                    "title : 공고 제목<br>" +
                    "recruitStartDate : 모집 시작일<br>" +
                    "recruitEndDate : 모집 종료일 / 상시 모집일 경우 2099-12-31<br>" +
                    "recruitCount : 모집 인원<br>" +
                    "gender : 성별 (무관일시 null)<br>" +
                    "education : 학력 조건<br>" +
                    "otherConditions : 기타 조건<br>" +
                    "preferredConditions : 우대 조건 리스트<br>" +
                    "workDuration : 근무 기간<br>" +
                    "workTime : 근무 시간(직접 선택시 '시작시간~종료시간'<br>" +
                    "workDays : 근무 요일<br>" +
                    "workDaysOther : 근무 요일 기타 사항(없다면 null)<br>" +
                    "salary : 급여 정보<br>" +
                    "salaryType : 급여 형태 (월급, 시급 등)<br>" +
                    "businessFields : 업직종 리스트<br>" +
                    "applicationMethods : 지원 방법<br>" +
                    "latitude : 위도<br>" +
                    "longitude : 경도<br>" +
                    "zipcode : 우편 번호<br>" +
                    "address1 : 주소<br>" +
                    "address2 : 상세 주소<br>" +
                    "<p>" +
                    "Portfolios<br>" +
                    "title : 질문 제목<br>" +
                    "type : 질문 유형(LONG_TEXT : 장문형 / SHORT_TEXT : 단답형 / FILE_UPLOAD : 파일 업로드)<br>" +
                    "required : 필수 질문<br>" +
                    "maxFileCount : 최대 업로드 가능 갯수(FILE_UPLOAD 일때만, 다른 유형일 경우 null)<br>" +
                    "maxFileSize : 최대 업로드 가능 파일 사이즈(FILE_UPLOAD 일때만, 다른 유형일 경우 null)<br>")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "공고 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PutMapping(value = "/premium/{recruitId}/publish", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> publishPremiumRecruit(
            @PathVariable Long recruitId,
            @RequestPart("request") RecruitRequestDTO.PremiumRecruitRequest recruitRequest,
            @RequestPart(value = "posterImage", required = false) MultipartFile posterImage
    ) {
        recruitService.publishPremiumRecruit(recruitId, recruitRequest, posterImage);
        return ApiResponse.success_only(SuccessStatus.CREATE_RECRUIT_ARTICLE_SUCCESS);
    }

    @Operation(summary = "사용자 임시저장 데이터 조회 API",
            description = "임시 저장한 데이터가 있는지 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "임시 저장된 공고 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/drafts")
    public ResponseEntity<ApiResponse<List<RecruitResponseDTO>>> getDrafts(
            @AuthenticationPrincipal SecurityMember securityMember
    ) {
        List<RecruitResponseDTO> drafts = recruitService.getDrafts(securityMember.getId());
        if (drafts.isEmpty()) {
            return ApiResponse.success(SuccessStatus.SEND_NO_DRAFT_SAVE_SUCCESS, List.of());
        }
        return ApiResponse.success(SuccessStatus.SEND_DRAFT_SAVE_SUCCESS, drafts);
    }

    @Operation(summary = "작성 가능 공고 조회 API",
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

    @Operation(summary = "일반 채용 지원하기. API",
            description = "피고용인이 일반 채용을 지원합니다..")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "일반 채용 지원 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/general/{recruit-id}/apply")
    public ResponseEntity<ApiResponse<Void>> applyGeneralRecruit(@AuthenticationPrincipal SecurityMember securityMember,
                                                                       @PathVariable("recruit-id") Long recruitId,
                                                                       @RequestBody GeneralResumeRequestDTO dto) {
        resumeService.applyResume(securityMember.getId(), recruitId,dto);

        return ApiResponse.success_only(SuccessStatus.APPLY_RECRUIT_ARTICLE_SUCCESS);
    }

    @Operation(summary = "프리미엄 채용 지원하기. API",
            description = "피고용인이 프리미엄 채용을 지원합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프리미엄 채용 지원 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/premium/{recruit-id}/apply")
    public ResponseEntity<ApiResponse<Void>> applyGeneralRecruit(@AuthenticationPrincipal SecurityMember securityMember,
                                                                 @PathVariable("recruit-id") Long recruitId,
                                                                 @RequestBody PremiumResumeRequestDTO dto) {
        resumeService.applyPremiumResume(securityMember.getId(), recruitId,dto);

        return ApiResponse.success_only(SuccessStatus.APPLY_RECRUIT_ARTICLE_SUCCESS);
    }

    @Operation(summary = "공고 전체 조회 API",
            description = "등록되어있는 공고들을 전체 조회 합니다.")
    @GetMapping("/search")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공고 전체 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    public ResponseEntity<ApiResponse<PageResponseDTO<RecruitListResponseDTO>>> getAllRecruits(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) List<String> businessFields,
            @RequestParam(required = false) List<String> workDurations,
            @RequestParam(required = false) List<String> workDays,
            @RequestParam(required = false) List<String> workTimes,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String salaryType
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

    @Operation(summary = "공고 상세 조회 API",
            description = "공고 ID를 받아 해당 공고의 상세 정보를 반환합니다.<br>" +
                    "반환 정보: 회사(점포) 명, 회사 아이콘 이미지, 공고 제목, 급여(시급) 정보 등 공고 등록된 모든 정보를 포함합니다.<br>"+
                    "<p>" +
                    "employerAddress : 회사 주소<br>"+
                    "employerContact : 연락처<br>"+
                    "representative : 담당자명<br>"+
                    "employerEmail : 이메일<br>" +
                    "businessRegistrationNumber : 사업자등록번호<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "공고 상세 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 공고를 찾을 수 없습니다.")
    })
    @GetMapping(value = "/view", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<RecruitDetailResponseDTO>> getRecruitDetail(@RequestParam Long recruitId) {

        RecruitDetailResponseDTO detailDTO = recruitService.getRecruitDetail(recruitId);
        return ApiResponse.success(SuccessStatus.SEND_RECRUIT_DETAIL_SUCCESS, detailDTO);
    }
}