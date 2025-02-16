package com.core.foreign.api.member.controller;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.file.service.FileService;
import com.core.foreign.api.member.dto.*;
import com.core.foreign.api.member.entity.EmployeePortfolioStatus;
import com.core.foreign.api.member.entity.EvaluationCategory;
import com.core.foreign.api.member.jwt.service.JwtService;
import com.core.foreign.api.member.service.*;
import com.core.foreign.api.recruit.entity.EvaluationStatus;
import com.core.foreign.api.recruit.service.ResumeService;
import com.core.foreign.common.SecurityMember;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.response.ApiResponse;
import com.core.foreign.common.response.ErrorStatus;
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

import java.time.LocalDate;
import java.util.List;

import static com.core.foreign.common.response.SuccessStatus.*;

@Tag(name = "Member", description = "Member 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;
    private final FileService fileService;
    private final CompanyValidationService companyValidationService;
    private final EmployeePortfolioService employeePortfolioService;
    private final EmailService emailService;
    private final EvaluationService evaluationService;
    private final ResumeService resumeService;

    @Operation(
            summary = "피고용인 회원가입 API",
            description = "피고용인 회원가입을 진행합니다.<br>" +
                    "<p>" +
                    "전화번호(phoneNumber) 전달 형식 : 01012345678<br>" +
                    "생년월일(birthDate) 전달 형식 : 2025-01-20<br>" +
                    "<p>" +
                    "termsOfServiceAgreement : 서비스 이용 약관 동의<br>" +
                    "isOver15 : 만 15세 이상 확인<br>" +
                    "personalInfoAgreement : 개인정보 수집 및 이용 동의<br>" +
                    "adInfoAgreementSnsMms : 광고성 정보 수신 동의 (SNS/MMS)<br>" +
                    "adInfoAgreementEmail : 광고성 정보 수신 동의 (이메일)<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @PostMapping("/employee-register")
    public ResponseEntity<ApiResponse<Void>> registerEmployee(@RequestBody EmployeeRegisterRequestDTO employeeRegisterRequestDTO) {
        memberService.registerEmployee(employeeRegisterRequestDTO);
        return ApiResponse.success_only(SuccessStatus.SEND_REGISTER_SUCCESS);
    }

    @Operation(
            summary = "고용주 회원가입 API",
            description = "고용인 회원가입을 진행합니다.<br>" +
                    "<p>" +
                    "전화번호(phoneNumber) 전달 형식 : 01012345678<br>" +
                    "설립일(establishedDate) 전달 형식 : 2025-01-20<br>" +
                    "가입명의자생년월일(birthDate) 전달 형식 : 2025-01-20<br>" +
                    "사업자등록번호(businessRegistrationNumber) 전달 형식 : 0000000000<br>" +
                    "<p>" +
                    "termsOfServiceAgreement : 서비스 이용 약관 동의<br>" +
                    "isOver15 : 만 15세 이상 확인<br>" +
                    "personalInfoAgreement : 개인정보 수집 및 이용 동의<br>" +
                    "adInfoAgreementSnsMms : 광고성 정보 수신 동의 (SNS/MMS)<br>" +
                    "adInfoAgreementEmail : 광고성 정보 수신 동의 (이메일)<br>" +
                    "<p>" +
                    "businessField 전달 형식 : FOOD_BEVERAGE<br>" +
                    "<p>" +
                    "FOOD_BEVERAGE : 외식/음료,<br>" +
                    "STORE_SALES : 매장/판매,<br>" +
                    "PRODUCTION_CONSTRUCTION : 생산-건설,<br>" +
                    "PRODUCTION_TECHNICAL : 생산-기술,<br>" +
                    "OFFICE_SALES : 사무/영업,<br>" +
                    "DRIVING_DELIVERY : 운전/배달,<br>" +
                    "LOGISTICS_TRANSPORT : 물류/운송,<br>" +
                    "ACCOMMODATION_CLEANING : 숙박/청소,<br>" +
                    "CULTURE_LEISURE_LIFESTYLE : 문화/여가/생활,<br>" +
                    "RURAL_FISHING : 농어촌/선원,<br>" +
                    "MODEL_SHOPPING_MALL : 모델/쇼핑몰,<br>" +
                    "EDUCATION : 교육,<br>" +
                    "OTHER_SERVICE : 기타/서비스"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @PostMapping("/employer-register")
    public ResponseEntity<ApiResponse<Void>> registerEmployer(@RequestBody EmployerRegisterRequestDTO employerRegisterRequestDTO) {
        memberService.registerEmployer(employerRegisterRequestDTO);
        return ApiResponse.success_only(SuccessStatus.SEND_REGISTER_SUCCESS);
    }

    @Operation(
            summary = "로그인 API",
            description = "ID와 Password를 통해 사용자를 인증하고 토큰을 발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "로그인 정보가 유효하지 않습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패.")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<MemberLoginResponseDTO>> login(@RequestBody MemberLoginRequestDTO memberLoginRequestDTO) {
        MemberLoginResponseDTO responseDTO = memberService.login(memberLoginRequestDTO);
        return ApiResponse.success(SuccessStatus.SEND_LOGIN_SUCCESS, responseDTO);
    }

    @Operation(
            summary = "토큰 재발급 API",
            description = "유효한 리프레시 토큰을 헤더(Authorization-Refresh)로 제공하면 새로운 액세스 토큰과 리프레시 토큰을 발급하여 헤더로 전송합니다. / [주의] 스웨거로 테스트할때 토큰 앞에 'Bearer ' 을 붙여야 함"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "리프레시 토큰이 입력되지 않았습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰입니다."),
    })
    @GetMapping("/token-reissue")
    public ResponseEntity<ApiResponse<Void>> reissueToken(@RequestHeader(value = "Authorization-Refresh", required = false) String refreshToken) {

        // 리프레시 토큰이 입력되지 않았을 경우 예외 처리
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new BadRequestException(ErrorStatus.MISSING_REFRESH_TOKEN_EXCEPTION.getMessage());
        }

        // "Bearer " 문자열 제거 후 토큰 검증
        String pureRefreshToken = refreshToken.substring(7);
        if (!jwtService.isTokenValid(pureRefreshToken)) {
            // 유효하지 않은 토큰 예외 처리
            throw new BadRequestException(ErrorStatus.UNAUTHORIZED_REFRESH_TOKEN_EXCEPTION.getMessage());
        }

        return ApiResponse.success_only(SuccessStatus.SEND_REISSUE_TOKEN_SUCCESS);
    }

    @Operation(
            summary = "사용자 ID 중복 체크 API",
            description = "해당 사용자 ID가 사용가능한지 체크합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 ID 사용 가능"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 등록된 사용자 ID 입니다."),
    })
    @GetMapping("/verify-userid")
    public ResponseEntity<ApiResponse<Void>> getUserIdVerification(@RequestParam("userId") String userId) {

        memberService.verificationUserId(userId);
        return ApiResponse.success_only(SuccessStatus.SEND_ALLOW_USERID_SUCCESS);
    }

    @Operation(
            summary = "고용주 프로필 조회 API",
            description = "고용주의 이름, 생년월일, 성별, 이메일, 휴대폰 번호, 주소, 약관 동의를 조회합니다.<br>" +
                    "<p>" +
                    "termsOfServiceAgreement : 서비스 이용 약관 동의<br>" +
                    "isOver15 : 만 15세 이상 확인<br>" +
                    "personalInfoAgreement : 개인정보 수집 및 이용 동의<br>" +
                    "adInfoAgreementSnsMms : 광고성 정보 수신 동의 (SNS/MMS)<br>" +
                    "adInfoAgreementEmail : 광고성 정보 수신 동의 (이메일)<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 프로필 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/my-profile")
    public ResponseEntity<ApiResponse<EmployerProfileResponseDTO>> getEmployerProfile(@AuthenticationPrincipal SecurityMember securityMember) {

        EmployerProfileResponseDTO responseDTO = memberService.getEmployerProfile(securityMember.getId());
        return ApiResponse.success(SuccessStatus.SEND_SELECT_EMPLOYER_SUCCESS, responseDTO);
    }

    @Operation(
            summary = "고용주 이름, 생년월일, 성별 수정 API",
            description = "고용주의 이름, 생년월일, 성별을 수정합니다." +
                    " 생년월일: yyyy-mm-dd 형식"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 이름, 생년월일, 성별 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/basic-info")
    public ResponseEntity<ApiResponse<Void>> updateEmployerBasicInfo(@RequestParam String name,
                                                                     @RequestParam LocalDate birthday,
                                                                     @RequestParam boolean isMaie,
                                                                     @AuthenticationPrincipal SecurityMember securityMember) {

        memberService.updateEmployerBasicInfo(securityMember.getId(), name, birthday, isMaie);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "고용주 이메일 수정 API",
            description = "고용주의 이메일을 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 이메일 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/email")
    public ResponseEntity<ApiResponse<Void>> updateEmployerEmail(@RequestParam String email, @AuthenticationPrincipal SecurityMember securityMember){

        memberService.updateEmployerEmail(securityMember.getId(), email);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "고용주 회사 이메일 수정 API",
            description = "고용주의 회사 이메일을 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 회사 이메일 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/company-email")
    public ResponseEntity<ApiResponse<Void>> updateEmployerCompanyEmail(@RequestParam String email, @AuthenticationPrincipal SecurityMember securityMember){

        memberService.updateEmployerCompanyEmail(securityMember.getId(), email);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "고용주 휴대폰 번호 수정 API",
            description = "고용주의 휴대폰 번호를 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 휴대폰 번호 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/phone-number")
    public ResponseEntity<ApiResponse<Void>> updateEmployerPhoneNumber(@RequestParam String phoneNumber, @AuthenticationPrincipal SecurityMember securityMember){

        memberService.updateEmployerPhoneNumber(securityMember.getId(), phoneNumber);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "고용주 회사 대표 연락처 수정 API",
            description = "고용주 회사 대표 연락처를 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 회사 대표 연락처 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/main-phone-number")
    public ResponseEntity<ApiResponse<Void>> updateEmployerCompanyMainPhoneNumber(@RequestParam String phoneNumber, @AuthenticationPrincipal SecurityMember securityMember){

        memberService.updateEmployerCompanyPhoneNumber(securityMember.getId(), phoneNumber);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "고용주 주소 수정 API",
            description = "고용주의 주소를 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 주소 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/address")
    public ResponseEntity<ApiResponse<Void>> updateEmployerAddress(@RequestParam String zipcode,
                                                                   @RequestParam String address1,
                                                                   @RequestParam String address2,
                                                                   @AuthenticationPrincipal SecurityMember securityMember) {

        memberService.updateEmployerAddress(securityMember.getId(), zipcode, address1, address2);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "고용주 약관 동의 수정 API",
            description = "고용주의 약관 동의를 수정합니다.<br>"+
                          "<p>" +
                    "termsOfServiceAgreement : 서비스 이용 약관 동의<br>" +
                    "isOver15 : 만 15세 이상 확인<br>" +
                    "personalInfoAgreement : 개인정보 수집 및 이용 동의<br>" +
                    "adInfoAgreementSnsMms : 광고성 정보 수신 동의 (SNS/MMS)<br>" +
                    "adInfoAgreementEmail : 광고성 정보 수신 동의 (이메일)<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 약관 동의 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/agreements")
    public ResponseEntity<ApiResponse<Void>> updateEmployerAgreements(@RequestParam boolean termsOfServiceAgreement,
                                                                      @RequestParam boolean isOver15,
                                                                      @RequestParam boolean personalInfoAgreement,
                                                                      @RequestParam boolean adInfoAgreementSnsMms,
                                                                      @RequestParam boolean adInfoAgreementEmail,
                                                                      @AuthenticationPrincipal SecurityMember securityMember) {

        memberService.updateEmployerAgreement(securityMember.getId(), termsOfServiceAgreement, isOver15, personalInfoAgreement, adInfoAgreementSnsMms, adInfoAgreementEmail);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "고용주 업직종 수정 API",
            description = "고용주의 업직종을 수정합니다. 최대 5개 <br>" +
                    "<p>" +
                    "FOOD_BEVERAGE : 외식/음료,<br>" +
                    "STORE_SALES : 매장/판매,<br>" +
                    "PRODUCTION_CONSTRUCTION : 생산-건설,<br>" +
                    "PRODUCTION_TECHNICAL : 생산-기술,<br>" +
                    "OFFICE_SALES : 사무/영업,<br>" +
                    "DRIVING_DELIVERY : 운전/배달,<br>" +
                    "LOGISTICS_TRANSPORT : 물류/운송,<br>" +
                    "ACCOMMODATION_CLEANING : 숙박/청소,<br>" +
                    "CULTURE_LEISURE_LIFESTYLE : 문화/여가/생활,<br>" +
                    "RURAL_FISHING : 농어촌/선원,<br>" +
                    "MODEL_SHOPPING_MALL : 모델/쇼핑몰,<br>" +
                    "EDUCATION : 교육,<br>" +
                    "OTHER_SERVICE : 기타/서비스"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주의 업직종 수정 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "최대 5개까지 가능합니다."),
    })
    @PatchMapping("/employer/my-company/business-fields")
    public ResponseEntity<ApiResponse<Void>> updateBusinessFiledOfEmployer(@AuthenticationPrincipal SecurityMember securityMember,
                                                                           @RequestParam List<BusinessField> businessFields) {
        memberService.updateBusinessFiledOfEmployer(securityMember.getId(), businessFields);

        if(businessFields.size()>=5) {
            throw new BadRequestException("최대 5개까지 가능합니다.");
        }

        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "마이페이지(고용주)-내 기업 정보 API",
            description = "고용주의 기업 정보를 조회합니다. "
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주의 기업 정보 조회 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/employer/my-company")
    public ResponseEntity<ApiResponse<EmployerCompanyInfoResponseDTO>> getEmployerCompanyInfo(@AuthenticationPrincipal SecurityMember securityMember) {
        EmployerCompanyInfoResponseDTO companyInfo = memberService.getCompanyInfo(securityMember.getId());

        ResponseEntity<ApiResponse<EmployerCompanyInfoResponseDTO>> success = ApiResponse.success(SEND_SELECT_EMPLOYER_COMPANY_INFO_SUCCESS, companyInfo);
        return success;
    }

    @Operation(
            summary = "마이페이지(피고용인)-기본 이력서 조회 API",
            description = "피고용인의 기본 이력서를 조회합니다.. "
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "피고요인의 기본 이력서 조회 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/employee/basic-resume")
    public ResponseEntity<ApiResponse<EmployeeBasicResumeResponseDTO>> getEmployeeBasicResume(@AuthenticationPrincipal SecurityMember securityMember) {
        EmployeeBasicResumeResponseDTO employeeBasicResume = memberService.getEmployeeBasicResume(securityMember.getId());

        ResponseEntity<ApiResponse<EmployeeBasicResumeResponseDTO>> success = ApiResponse.success(SEND_SELECT_EMPLOYEE_BASIC_RESUME_SUCCESS, employeeBasicResume);
        return success;
    }

    @Operation(
            summary = "마이페이지(피고용인)-기본 이력서 수정 API",
            description = "피고용인의 기본 이력서를 수정합니다. " +
                    "변경 대상: 학력, 비자, 주소" +
                    " 연략처와 이메일 변경은 각각 다른 api 로 제공 예정."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "피고요인의 기본 이력서 수정 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employee/basic-resume")
    public ResponseEntity<ApiResponse<Void>> updateEmployeeBasicResume(@AuthenticationPrincipal SecurityMember securityMember,
                                                                       @RequestBody EmployeeBasicResumeUpdateDTO updateDTO) {
        memberService.updateEmployeeBasicResume(securityMember.getId(), updateDTO);

        ResponseEntity<ApiResponse<Void>> success = ApiResponse.success_only(SEND_EMPLOYEE_BASIC_RESUME_UPDATE_SUCCESS);
        return success;
    }

    @Operation(
            summary = "사용자 ID 찾기 API",
            description = "이름과 전화번호를 이용하여 사용자의 ID를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 ID 찾기 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.")
    })
    @GetMapping("/find-user-id")
    public ResponseEntity<ApiResponse<String>> findUserId(@RequestParam String name, @RequestParam String phoneNumber) {
        String userId = memberService.findUserId(name, phoneNumber);
        return ApiResponse.success(SuccessStatus.SEND_FIND_USERID_SUCCESS, userId);
    }

    @Operation(summary = "고용주 회사 이미지 변경. API",
            description = "고용주의 회사 이미지를 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회사 이미지 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping(value="/employer/company-image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateCompanyImage(@AuthenticationPrincipal SecurityMember securityMember,
                                                                @RequestPart(value = "companyImage", required = false) MultipartFile companyImage) {

        fileService.uploadCompanyImage(securityMember.getId(), companyImage);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(summary = "사업자등록 정보 진위 확인. API",
            description = "사업자등록 정보의 진위 여부를 확인합니다.<br>" +
                    "<p>" +
                    "businessNo: 사업자등록번호<br>" +
                    "startDate: 개업일자 (YYYYMMDD 포맷)<br>" +
                    "representativeName: 대표자성명<br>" +
                    "<p>" +
                    "true: 성공<br>" +
                    "false: 사업자등록 정보가 잘못됨.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사업자등록 정보 진위 조회 완료."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/employer/company-validate")
    public ResponseEntity<ApiResponse<Boolean>> isCompanyValidate(@RequestParam String businessNo,
                                                                  @RequestParam String startDate,
                                                                  @RequestParam String representativeName) {

        boolean companyValidate = companyValidationService.isCompanyValidate(businessNo, startDate, representativeName);
        return ApiResponse.success(SuccessStatus.SEND_COMPANY_VALIDATION_COMPLETED, companyValidate);
    }

    @Operation(summary = "사업자번호 정보 변경. API",
            description = "사업자번호 정보를 변경합니다.<br>"+
                    "<p>" +
                    "businessNo: 사업자등록번호<br>" +
                    "startDate: 개업일자 (YYYYMMDD 포맷)<br>" +
                    "representativeName: 대표자성명<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사업자등록 정보 변경 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping(value="/employer/business-info")
    public ResponseEntity<ApiResponse<Void>> updateEmployerBusinessInfo(@AuthenticationPrincipal SecurityMember securityMember,
                                                                        @RequestParam String businessNo,
                                                                        @RequestParam String startDate,
                                                                        @RequestParam String representativeName) {

        memberService.updateEmployerBusinessInfo(securityMember.getId(), businessNo, startDate, representativeName);
        return ApiResponse.success_only(SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(summary = "피고용인 포트폴리오 등록 API",
            description = "피고용인의 포트폴리오를 등록합니다.<br>"+
                    "<p>" +
                    "introduction: 자기소개<br>" +
                    "enrollmentCertificateUrl: 재학증명서 URL<br>" +
                    "transcriptUrl: 성적증명서 URL<br>" +
                    "partTimeWorkPermitUrl: 시간제근로허가서 URL<br>" +
                    "topic: 주제<br>" +
                    "englishTestType: 영어능력시험 종류<br>" +
                    "englishTestScore: 영어능력시험 점수<br>"+
                    "<p>" +
                    "businessField: 업직종<br>" +
                    "experienceDescription: 본인 경력기술<br>" +
                    "startDate: 시작일자 <br>" +
                    "endDate: 종료일자 <br>" +
                    "<p>" +
                    "certificateName: 자격명<br>" +
                    "certificateDate: 취득일자 <br>" +
                    "<p>" +
                    "awardName: 상장명<br>" +
                    "awardDate: 수상날짜 <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "피고용인 포트폴리오 등록 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/employee/portfolio")
    public ResponseEntity<ApiResponse<Void>> createEmployeePortfolio(@AuthenticationPrincipal SecurityMember securityMember,
                                                                        @RequestBody EmployeePortfolioDTO dto) {

        employeePortfolioService.createEmployeePortfolio(securityMember.getId(), dto, EmployeePortfolioStatus.COMPLETED);
        return ApiResponse.success_only(CREATE_EMPLOYEE_PORTFOLIO_SUCCESS);
    }

    @Operation(summary = "피고용인 포트폴리오 임시 등록 API",
            description = "피고용인의 포트폴리오를 임시 등록합니다.<br>"+
                    "<p>" +
                    "introduction: 자기소개<br>" +
                    "enrollmentCertificateUrl: 재학증명서 URL<br>" +
                    "transcriptUrl: 성적증명서 URL<br>" +
                    "partTimeWorkPermitUrl: 시간제근로허가서 URL<br>" +
                    "topic: 주제<br>" +
                    "englishTestType: 영어능력시험 종류<br>" +
                    "englishTestScore: 영어능력시험 점수<br>"+
                    "<p>" +
                    "businessField: 업직종<br>" +
                    "experienceDescription: 본인 경력기술<br>" +
                    "startDate: 시작일자 <br>" +
                    "endDate: 종료일자 <br>" +
                    "<p>" +
                    "certificateName: 자격명<br>" +
                    "certificateDate: 취득일자 <br>" +
                    "<p>" +
                    "awardName: 상장명<br>" +
                    "awardDate: 수상날짜 <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "피고용인 포트폴리오 등록 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/employee/temp-portfolio")
    public ResponseEntity<ApiResponse<Void>> createTempEmployeePortfolio(@AuthenticationPrincipal SecurityMember securityMember,
                                                                     @RequestBody EmployeePortfolioDTO dto) {

        employeePortfolioService.createEmployeePortfolio(securityMember.getId(), dto, EmployeePortfolioStatus.TEMPORARY);
        return ApiResponse.success_only(CREATE_DRAFT_EMPLOYEE_PORTFOLIO_SUCCESS);
    }

    @Operation(summary = "피고용인 포트폴리오 조회 API",
            description = "피고용인의 포트폴리오를 조회합니다.<br>"+
                    "<p>" +
                    "introduction: 자기소개<br>" +
                    "enrollmentCertificateUrl: 재학증명서 URL<br>" +
                    "transcriptUrl: 성적증명서 URL<br>" +
                    "partTimeWorkPermitUrl: 시간제근로허가서 URL<br>" +
                    "topic: 주제<br>" +
                    "englishTestType: 영어능력시험 종류<br>" +
                    "englishTestScore: 영어능력시험 점수<br>"+
                    "<p>" +
                    "businessField: 업직종<br>" +
                    "experienceDescription: 본인 경력기술<br>" +
                    "startDate: 시작일자 <br>" +
                    "endDate: 종료일자 <br>" +
                    "<p>" +
                    "certificateName: 자격명<br>" +
                    "certificateDate: 취득일자 <br>" +
                    "<p>" +
                    "awardName: 상장명<br>" +
                    "awardDate: 수상날짜 <br>" +
                    "조회 성공 시 data 가 없을 경우, 피고용인이 포트폴리오를 아직 작성하지 않음."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "피고용인 포트폴리오 조회 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/employee/portfolio")
    public ResponseEntity<ApiResponse<EmployeePortfolioDTO>> getEmployeePortfolio(@AuthenticationPrincipal SecurityMember securityMember) {

        EmployeePortfolioDTO employeePortfolio = employeePortfolioService.getEmployeePortfolio(securityMember.getId(), EmployeePortfolioStatus.COMPLETED);
        return ApiResponse.success(SEND_EMPLOYER_PORTFOLIO_SELECT_SUCCESS, employeePortfolio);
    }

    @Operation(summary = "피고용인 포트폴리오 임시 저장 조회 API",
            description = "피고용인의 임시 저장된 포트폴리오를 조회합니다.<br>"+
                    "<p>" +
                    "introduction: 자기소개<br>" +
                    "enrollmentCertificateUrl: 재학증명서 URL<br>" +
                    "transcriptUrl: 성적증명서 URL<br>" +
                    "partTimeWorkPermitUrl: 시간제근로허가서 URL<br>" +
                    "topic: 주제<br>" +
                    "englishTestType: 영어능력시험 종류<br>" +
                    "englishTestScore: 영어능력시험 점수<br>"+
                    "<p>" +
                    "businessField: 업직종<br>" +
                    "experienceDescription: 본인 경력기술<br>" +
                    "startDate: 시작일자 <br>" +
                    "endDate: 종료일자 <br>" +
                    "<p>" +
                    "certificateName: 자격명<br>" +
                    "certificateDate: 취득일자 <br>" +
                    "<p>" +
                    "awardName: 상장명<br>" +
                    "awardDate: 수상날짜 <br>" +
                    "조회 성공 시 data 가 없을 경우, 피고용인이 포트폴리오를 아직 작성하지 않음."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "피고용인 임시 저장 포트폴리오 조회 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/employee/temp-portfolio")
    public ResponseEntity<ApiResponse<EmployeePortfolioDTO>> getTempEmployeePortfolio(@AuthenticationPrincipal SecurityMember securityMember) {

        EmployeePortfolioDTO employeePortfolio = employeePortfolioService.getEmployeePortfolio(securityMember.getId(), EmployeePortfolioStatus.TEMPORARY);
        return ApiResponse.success(SEND_EMPLOYER_DRAFT_PORTFOLIO_SELECT_SUCCESS, employeePortfolio);
    }

    @Operation(summary = "피고용인 포트폴리오 수정 API",
            description = "피고용인의 포트폴리오를 수정합니다.<br>"+
                    "<p>" +
                    "introduction: 자기소개<br>" +
                    "enrollmentCertificateUrl: 재학증명서 URL<br>" +
                    "transcriptUrl: 성적증명서 URL<br>" +
                    "partTimeWorkPermitUrl: 시간제근로허가서 URL<br>" +
                    "topic: 주제<br>" +
                    "englishTestType: 영어능력시험 종류<br>" +
                    "englishTestScore: 영어능력시험 점수<br>"+
                    "<p>" +
                    "businessField: 업직종<br>" +
                    "experienceDescription: 본인 경력기술<br>" +
                    "startDate: 시작일자 <br>" +
                    "endDate: 종료일자 <br>" +
                    "<p>" +
                    "certificateName: 자격명<br>" +
                    "certificateDate: 취득일자 <br>" +
                    "<p>" +
                    "awardName: 상장명<br>" +
                    "awardDate: 수상날짜 <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "피고용인 포트폴리오 수정 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employee/portfolio")
    public ResponseEntity<ApiResponse<Void>> updateEmployeePortfolio(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                  @RequestBody EmployeePortfolioDTO dto) {

        employeePortfolioService.updateEmployeePortfolio(securityMember.getId(), dto);
        return ApiResponse.success_only(SEND_EMPLOYER_PORTFOLIO_UPDATE_SUCCESS);
    }

    @Operation(summary = "비밀번호 확인 API",
            description = "비밀번호 확인합니다.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 확인 완료."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/verify-password")
    public ResponseEntity<ApiResponse<Boolean>> checkPassword(@AuthenticationPrincipal SecurityMember securityMember,
                                                           @RequestBody PasswordDTO passwordDTO) {

        boolean b = memberService.checkPassword(securityMember.getId(), passwordDTO.getPassword());
        return ApiResponse.success(SEND_PASSWORD_VERIFICATION_COMPLETED, b);
    }

    @Operation(summary = "아이디 변경. API",
            description = "아이디 변경합니다.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "아이디 변경 완료."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/user-id")
    public ResponseEntity<ApiResponse<Void>> updateUserId(@AuthenticationPrincipal SecurityMember securityMember,
                                                              @RequestBody UserIdDTO userIdDTO) {

         memberService.updateUserId(securityMember.getId(), userIdDTO.getUserId());
        return ApiResponse.success_only(SEND_UPDATE_USERID_SUCCESS);
    }

    @Operation(summary = "비밀번호 변경. API",
            description = "비밀번호 변경합니다.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 변경 완료."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> upadtePassword(@AuthenticationPrincipal SecurityMember securityMember,
                                                          @RequestBody PasswordDTO passwordDTO) {

        memberService.updateMemberPassword(securityMember.getId(), passwordDTO.getPassword());
        return ApiResponse.success_only(SEND_UPDATE_USERID_PASSWORD);
    }

    @Operation(
            summary = "비밀번호 초기화 요청 API",
            description = "[비밀번호 찾기 기능] 이메일로 비밀번호 초기화 링크를 보냅니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 초기화 링크 전송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/reset-password/request")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@RequestBody PasswordResetRequestDTO.PasswordResetRequest passwordResetRequest) {

        emailService.sendPasswordResetEmail(passwordResetRequest);
        return ApiResponse.success_only(SuccessStatus.SEND_PASSWORD_RESET_LINK_SUCCESS);
    }

    @Operation(
            summary = "비밀번호 초기화 API",
            description = "[비밀번호 찾기 기능] 비밀번호 초기화 링크에서 받은 코드를 이용해 비밀번호를 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 비밀번호 초기화 인증코드 입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "비밀번호 초기화 인증코드가 만료되었습니다, 재인증 해주세요."),
    })
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody PasswordResetRequestDTO.PasswordResetConfirm passwordResetConfirm) {

        memberService.resetPassword(passwordResetConfirm);
        return ApiResponse.success_only(SuccessStatus.SEND_UPDATE_USERID_PASSWORD);
    }


    @Operation(
            summary = "고용인이 피고용인 평가 API",
            description = "고용인이 피고용인을 평가합니다."+
            "<p>" +
            "WORKS_DILIGENTLY: 성실하게 일해요.<br>" +
            "NO_LATENESS_OR_ABSENCE: 지각/결근하지 않았어요.<br>" +
            "POLITE_AND_FRIENDLY: 예의 바르고 친절해요.<br>" +
            "GOOD_CUSTOMER_SERVICE: 고객 응대를 잘해요.<br>" +
            "SKILLED_AT_WORK: 업무 능력이 좋아요.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "평가 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/evaluations/employer-to-employee")
    public ResponseEntity<ApiResponse<Void>> evaluateEmployee(@AuthenticationPrincipal SecurityMember securityMember,
                                                              @RequestBody EmployerToEmployeeEvaluationRequestDTO dto) {

        if(dto.getEvaluationCategory().isEmpty()){
            throw new BadRequestException(ErrorStatus.EVALUATION_CATEGORY_IS_EMPTY_EXCEPTION.getMessage());
        }

        evaluationService.evaluateEmployee(dto.getResumeId(), dto.getEvaluationCategory());
        return ApiResponse.success_only(SuccessStatus.EVALUATE_EMPLOYEE_SUCCESS);
    }

    @Operation(
            summary = "피고용인이 고용인 평가. API",
            description = "피고용인이 고용인을 평가합니다." +
                    "<p>" +
                    "PAYS_ON_TIME: 약속된 급여를 제때 줘요.<br>" +
                    "KEEPS_CONTRACT_DATES: 계약된 날짜를 잘 지켰어요.<br>" +
                    "RESPECTS_EMPLOYEES: 알바생을 존중해줘요.<br>" +
                    "FRIENDLY_BOSS: 사장님이 친절해요.<br>" +
                    "FAIR_WORKLOAD: 업무 강도가 적당해요.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "평가 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/evaluations/employee-to-employer")
    public ResponseEntity<ApiResponse<Void>> evaluateEmployer(@AuthenticationPrincipal SecurityMember securityMember,
                                                              @RequestBody EmployeeToEmployerEvaluationRequestDTO dto) {

        if (dto.getEvaluationCategory().isEmpty()) {
            throw new BadRequestException(ErrorStatus.EVALUATION_CATEGORY_IS_EMPTY_EXCEPTION.getMessage());
        }

        evaluationService.evaluateEmployer(securityMember.getId(), dto.getRecruitId(), dto.getEvaluationCategory());
        return ApiResponse.success_only(SuccessStatus.EVALUATE_EMPLOYEE_SUCCESS);
    }

    @Operation(
            summary = "평가보기. API",
            description = "평가보기입니다."+
                    "<p>" +
                    "WORKS_DILIGENTLY: 성실하게 일해요.<br>" +
                    "NO_LATENESS_OR_ABSENCE: 지각/결근하지 않았어요.<br>" +
                    "POLITE_AND_FRIENDLY: 예의 바르고 친절해요.<br>" +
                    "GOOD_CUSTOMER_SERVICE: 고객 응대를 잘해요.<br>" +
                    "SKILLED_AT_WORK: 업무 능력이 좋아요.<br>"+
                    "<p>" +
                    "PAYS_ON_TIME: 약속된 급여를 제때 줘요.<br>" +
                    "KEEPS_CONTRACT_DATES: 계약된 날짜를 잘 지켰어요.<br>" +
                    "RESPECTS_EMPLOYEES: 알바생을 존중해줘요.<br>" +
                    "FRIENDLY_BOSS: 사장님이 친절해요.<br>" +
                    "FAIR_WORKLOAD: 업무 강도가 적당해요.<br>"

    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "평가 보기 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/evaluations")
    public ResponseEntity<ApiResponse<List<EvaluationCategory>>> getEvaluation(@AuthenticationPrincipal SecurityMember securityMember,
                                                                               @RequestParam("resumeId")Long resumeId) {

        List<EvaluationCategory> evaluation = evaluationService.getEvaluation(securityMember.getId(), resumeId);
        return ApiResponse.success(SuccessStatus.EVALUATE_VIEW_SUCCESS, evaluation);
    }


    @Operation(
            summary = "태그 조회. API",
            description = "태크 조회입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "태그 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<Page<TagResponseDTO>>> getEvaluation(@AuthenticationPrincipal SecurityMember securityMember,
                                                                           @RequestParam("evaluationStatus") EvaluationStatus evaluationStatus,
                                                                           @RequestParam("page") Integer page,
                                                                           @RequestParam("size") Integer size) {

        Page<TagResponseDTO> tags = resumeService.getTags(securityMember.getId(), evaluationStatus, page, size);
        return ApiResponse.success(SuccessStatus.TAG_VIEW_SUCCESS, tags);
    }

}