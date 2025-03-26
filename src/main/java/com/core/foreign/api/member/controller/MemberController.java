package com.core.foreign.api.member.controller;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.contract.dto.EmployeeCompletedContractResponseDTO;
import com.core.foreign.api.contract.dto.EmployerCompletedContractResponseDTO;
import com.core.foreign.api.contract.service.ContractService;
import com.core.foreign.api.file.dto.FileUrlAndOriginalFileNameDTO;
import com.core.foreign.api.file.service.FileService;
import com.core.foreign.api.member.dto.*;
import com.core.foreign.api.member.jwt.service.JwtService;
import com.core.foreign.api.member.service.*;
import com.core.foreign.api.portfolio.dto.response.ApplicationPortfolioPreviewResponseDTO;
import com.core.foreign.api.portfolio.dto.response.BasicPortfolioPreviewResponseDTO;
import com.core.foreign.api.recruit.dto.MyResumeResponseDTO;
import com.core.foreign.api.recruit.dto.PageResponseDTO;
import com.core.foreign.api.recruit.dto.RecruitPreviewResponseDTO;
import com.core.foreign.api.recruit.entity.EvaluationStatus;
import com.core.foreign.api.recruit.service.RecruitService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final ContractService contractService;
    private final RecruitService recruitService;

    @Operation(
            summary = "피고용인 회원가입 API (태근)",
            description = "피고용인 회원가입을 진행합니다. <br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "phoneNumber : 전화번호 (예시: 01012345678) <br>"
                    + "birthDate : 생년월일 (예시: 2025-01-20) <br>"
                    + "termsOfServiceAgreement : 서비스 이용 약관 동의 <br>"
                    + "isOver15 : 만 15세 이상 확인 <br>"
                    + "personalInfoAgreement : 개인정보 수집 및 이용 동의 <br>"
                    + "adInfoAgreementSnsMms : 광고성 정보 수신 동의 (SNS/MMS) <br>"
                    + "adInfoAgreementEmail : 광고성 정보 수신 동의 (이메일) <br>"
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
            summary = "고용주 회원가입 API (태근)",
            description = "고용주 회원가입을 진행합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "phoneNumber : 전화번호 (예시: 01012345678) <br>"
                    + "establishedDate : 설립일 (예시: 2025-01-20) <br>"
                    + "birthDate : 가입명의자생년월일 (예시: 2025-01-20) <br>"
                    + "businessRegistrationNumber : 사업자등록번호 (예시: 0000000000) <br>"
                    + "<p>"
                    + "termsOfServiceAgreement : 서비스 이용 약관 동의 <br>"
                    + "isOver15 : 만 15세 이상 확인 <br>"
                    + "personalInfoAgreement : 개인정보 수집 및 이용 동의 <br>"
                    + "adInfoAgreementSnsMms : 광고성 정보 수신 동의 (SNS/MMS) <br>"
                    + "adInfoAgreementEmail : 광고성 정보 수신 동의 (이메일) <br>"
                    + "<p>"
                    + "ENUM : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
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
            summary = "로그인 API (태근)",
            description = "ID와 Password를 통해 사용자를 인증하고 토큰을 발급합니다. <br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "userId : 사용자 아이디 (예시: user) <br>"
                    + "password : 사용자 비밀번호 (예시: password)"
                    + "<p>"
                    + "요청 예시 : <A href = \"https://www.notion.so/1bc244b92af281f9a82dce6cafca896f?v=1bc244b92af281559243000c1a4fef2f&p=1bc244b92af28125b330c47629fd5152&pm=s\" target=\"_blank\"> 이동 하기 </A>"
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
            summary = "토큰 재발급 API (태근)",
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
            summary = "사용자 ID 중복 체크 API (태근)",
            description = "해당 사용자 ID가 사용가능한지 체크합니다. <br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "userId : 사용자 ID"
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
            summary = "고용주 프로필 조회 API (용범)",
            description = "고용주의 이름, 생년월일, 성별, 이메일, 휴대폰 번호, 주소, 약관 동의를 조회합니다.<br>" +
                    "<p>" +
                    "호출 필드 정보)<br>" +
                    "번호: ('-' 없음)"
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
            summary = "고용주 이름, 생년월일, 성별 수정 API (용범)",
            description = "고용주의 이름, 생년월일, 성별을 수정합니다." +
                    "<p>" +
                    "호출 필드 정보)<br>" +
                    "name: 고용주 이름<br>" +
                    "birthday: 생년월일: yyyy-mm-dd 형식<br>" +
                    "male: 성별(true(남자) / false(여자))" +
                    "<p>" +
                    "요청 예시: <a href=\"https://www.notion.so/api-v1-member-employer-profile-basic-info-55dbced041d64d42906d4db86ec3293e?pvs=4\" target=\"_blank\" >이동하기</a>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 이름, 생년월일, 성별 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/basic-info")
    public ResponseEntity<ApiResponse<Void>> updateEmployerBasicInfo(@AuthenticationPrincipal SecurityMember securityMember,
                                                                     @RequestBody EmployerBasicInfoUpdateRequestDTO dto) {

        memberService.updateEmployerBasicInfo(securityMember.getId(), dto.getName(), dto.getBirthday(), dto.isMale());
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "회원 이메일 수정 API (용범)",
            description = "회원 이메일을 수정합니다.<br>" +
                    "<p>" +
                    "호출 필드 정보)<br>" +
                    "email: 이메일"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 이메일 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/profile/email")
    public ResponseEntity<ApiResponse<Void>> updateMemberEmail(@AuthenticationPrincipal SecurityMember securityMember,
                                                               @RequestBody EmailRequestDTO.EmailVerificationRequest request) {

        String email = request.getEmail();

        memberService.updateMemberEmail(securityMember.getId(), email);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "고용주 회사 이메일 수정 API (용범)",
            description = "고용주 회사 이메일 수정합니다.<br>" +
                    "<p>" +
                    "호출 필드 정보)<br>" +
                    "email: 이메일"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 회사 이메일 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/company-email")
    public ResponseEntity<ApiResponse<Void>> updateEmployerCompanyEmail(@AuthenticationPrincipal SecurityMember securityMember,
                                                                        @RequestBody EmailRequestDTO.EmailVerificationRequest request) {

        String email = request.getEmail();

        memberService.updateEmployerCompanyEmail(securityMember.getId(), email);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "고용주 회사 주소 수정 API (용범)",
            description = "고용주의 회사 주소를 수정합니다." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "zipcode, address1, address2"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 회사 주소 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/company-address")
    public ResponseEntity<ApiResponse<Void>> updateEmployerCompanyAddress(@AuthenticationPrincipal SecurityMember securityMember,
                                                                          @RequestBody AddressDTO dto) {

        memberService.updateCompanyAddress(securityMember.getId(), dto.getZipcode(), dto.getAddress1(), dto.getAddress2());
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "회원 휴대폰 번호 수정 API (용범)",
            description = "회원 휴대폰 번호를 수정합니다." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "phoneNumber: 전화번호('-' 없음)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 휴대폰 번호 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/profile/phone-number")
    public ResponseEntity<ApiResponse<Void>> updateMemberPhoneNumber(@AuthenticationPrincipal SecurityMember securityMember,
                                                                     @RequestBody SmsRequestDTO.SmsVerificationRequest request) {
        String phoneNumber = request.getPhoneNumber();

        memberService.updateMemberPhoneNumber(securityMember.getId(), phoneNumber);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "고용주 회사 대표 연락처 수정 API (용범)",
            description = "고용주 회사 대표 연락처를 수정합니다." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "phoneNumber: 전화번호('-' 없음)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 회사 대표 연락처 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/main-phone-number")
    public ResponseEntity<ApiResponse<Void>> updateEmployerCompanyMainPhoneNumber(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                  @RequestBody SmsRequestDTO.SmsVerificationRequest request) {

        String phoneNumber = request.getPhoneNumber();

        memberService.updateEmployerCompanyPhoneNumber(securityMember.getId(), phoneNumber);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "고용주 주소 수정 API (용범)",
            description = "고용주의 주소를 수정합니다." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "zipcode, address1, address2"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 주소 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/address")
    public ResponseEntity<ApiResponse<Void>> updateEmployerAddress(@AuthenticationPrincipal SecurityMember securityMember,
                                                                   @RequestBody AddressDTO dto) {

        memberService.updateEmployerAddress(securityMember.getId(), dto.getZipcode(), dto.getAddress1(), dto.getAddress2());
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "고용주 약관 동의 수정 API (용범)",
            description = "고용주의 약관 동의를 수정합니다." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "termsOfServiceAgreement : 서비스 이용 약관 동의<br>" +
                    "over15 : 만 15세 이상 확인<br>" +
                    "personalInfoAgreement : 개인정보 수집 및 이용 동의<br>" +
                    "adInfoAgreementSnsMms : 광고성 정보 수신 동의 (SNS/MMS)<br>" +
                    "adInfoAgreementEmail : 광고성 정보 수신 동의 (이메일)<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 약관 동의 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/agreements")
    public ResponseEntity<ApiResponse<Void>> updateEmployerAgreements(@AuthenticationPrincipal SecurityMember securityMember,
                                                                      @RequestBody AgreementRequestDTO agreementRequestDTO) {

        memberService.updateEmployerAgreement(securityMember.getId(),
                agreementRequestDTO.isTermsOfServiceAgreement(), agreementRequestDTO.isOver15(), agreementRequestDTO.isPersonalInfoAgreement(),
                agreementRequestDTO.isAdInfoAgreementSnsMms(), agreementRequestDTO.isAdInfoAgreementEmail());
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "고용주 업직종 수정 API (용범)",
            description = "고용주의 업직종을 수정합니다. 최대 5개 " +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "businessFields: 업직종 리스트" +
                    "<p>" +
                    "요청 예시 : <A href =\"https://www.notion.so/api-v1-member-employer-my-company-business-fields-28475ab2915c435f97f92b1c8770ad78?pvs=4\"  target=\"_blank\"> 이동 하기 </A><br>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주의 업직종 수정 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "최대 5개까지 가능합니다."),
    })
    @PatchMapping("/employer/my-company/business-fields")
    public ResponseEntity<ApiResponse<Void>> updateBusinessFiledOfEmployer(@AuthenticationPrincipal SecurityMember securityMember,
                                                                           @RequestParam List<BusinessField> businessFields) {
        memberService.updateBusinessFiledOfEmployer(securityMember.getId(), businessFields);

        if (businessFields.size() >= 5) {
            throw new BadRequestException("최대 5개까지 가능합니다.");
        }

        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "마이페이지(고용주)-내 기업 정보 API (용범)",
            description = "고용주의 기업 정보를 조회합니다." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "companyImage: 회사 로고 사진<br>" +
                    "companyName: 회사명 <br>" +
                    "name: 대표자명<br>" +
                    "businessRegistrationNumber: 사업자 번호('-' 없음)<br>" +
                    "establishedDate: 설립일(YYYY-MM-DD)<br>" +
                    "businessFields: 업직종<br>" +
                    "zipcode: 우편번호<br>" +
                    "address1: 주소<br>" +
                    "address2: 상세주소<br>" +
                    "companyEmail: 회사 이메일<br>" +
                    "mainPhoneNumber: 대표 연락처 ('-' 없음)<br>" +
                    "<p>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
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
            summary = "마이페이지(피고용인)-기본 이력서 조회 API (용범)",
            description = "피고용인의 기본 이력서를 조회합니다.<br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "employeeId: 피고용인 id<br>" +
                    "name: 이름<br>" +
                    "nationality: 국적<br>" +
                    "education: 학력<br>" +
                    "visa: 비자<br>" +
                    "birthDate: 생년월일(YYYY-MM-DD)<br>" +
                    "email: 이메일<br>" +
                    "phoneNumber: 연락처 ('-' 없음)<br>" +
                    "zipcode: 우편번호<br>" +
                    "address1: 주소<br>" +
                    "address2: 상세주소<br>"
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
            summary = "마이페이지(피고용인)-기본 이력서 수정 API (용범)",
            description = "피고용인의 기본 이력서를 수정합니다." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "education: 학력<br>" +
                    "visa: 비자<br>" +
                    "zipcode: 우편번호<br>" +
                    "address1: 주소<br>" +
                    "address2: 상세주소<br>"
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
            summary = "사용자 ID 찾기 API (태근)",
            description = "이름과 전화번호를 이용하여 사용자의 ID를 반환합니다. <br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "name : 사용자 이름 <br>"
                    + "phoneNumber : 사용자 전화번호 (예시 : 01012345678)"
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

    @Operation(
            summary = "고용주 회사 이미지 변경. API (용범)",
            description = "고용주의 회사 이미지를 변경합니다." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "companyImage: 회사 이미지<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회사 이미지 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping(value = "/employer/company-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> updateCompanyImage(@AuthenticationPrincipal SecurityMember securityMember,
                                                                @RequestPart(value = "companyImage", required = false) MultipartFile companyImage) {

        fileService.uploadCompanyImage(securityMember.getId(), companyImage);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "사업자등록 정보 진위 확인. API (용범)",
            description = "사업자등록 정보의 진위 여부를 확인합니다." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "businessNo: 사업자등록번호('-' 없음)<br>" +
                    "startDate: 개업일자 (YYYYMMDD 포맷)<br>" +
                    "representativeName: 대표자 성명<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사업자등록 정보 진위 조회 완료."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/employer/company-validate")
    public ResponseEntity<ApiResponse<Boolean>> isCompanyValidate(@RequestBody BusinessVerificationRequestDTO businessVerificationRequestDTO) {

        boolean companyValidate = companyValidationService.isCompanyValidate(businessVerificationRequestDTO.getBusinessNo(), businessVerificationRequestDTO.getStartDate(), businessVerificationRequestDTO.getRepresentativeName());
        return ApiResponse.success(SuccessStatus.SEND_COMPANY_VALIDATION_COMPLETED, companyValidate);
    }

    @Operation(
            summary = "사업자번호 정보 변경. API (용범)",
            description = "사업자번호 정보를 변경합니다.<br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "businessNo: 사업자등록번호('-' 없음)<br>" +
                    "startDate: 개업일자 (YYYYMMDD 포맷)<br>" +
                    "representativeName: 대표자 성명<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사업자등록 정보 변경 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping(value = "/employer/business-info")
    public ResponseEntity<ApiResponse<Void>> updateEmployerBusinessInfo(@AuthenticationPrincipal SecurityMember securityMember,
                                                                        @RequestBody BusinessVerificationRequestDTO businessVerificationRequestDTO) {

        String businessNo = businessVerificationRequestDTO.getBusinessNo();
        String startDate = businessVerificationRequestDTO.getStartDate();
        String representativeName = businessVerificationRequestDTO.getRepresentativeName();

        memberService.updateEmployerBusinessInfo(securityMember.getId(), businessNo, startDate, representativeName);
        return ApiResponse.success_only(SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "피고용인 포트폴리오 등록 API (용범)",
            description = "피고용인의 포트폴리오를 등록합니다.<br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "introduction: 자기소개<br>" +
                    "enrollmentCertificateUrl: 재학증명서 URL<br>" +
                    "transcriptUrl: 성적증명서 URL<br>" +
                    "partTimeWorkPermitUrl: 시간제근로허가서 URL<br>" +
                    "topik: 한국어능력시험<br>" +
                    "businessField: 업직종 <br>" +
                    "experienceDescription: 본인 경력기술<br>" +
                    "startDate: 시작일자(YYYY-MM-DD) <br>" +
                    "endDate: 종료일자(YYYY-MM-DD) <br>" +
                    "businessField: 업직종 <br>" +
                    "certificateName: 자격명<br>" +
                    "certificateDate: 취득일자(YYYY-MM-DD) <br>" +
                    "businessField: 업직종 <br>" +
                    "awardName: 상장명<br>" +
                    "awardDate: 수상날짜(YYYY-MM-DD) <br>" +
                    "<p>" +
                    "요청 예시: <a href=\"https://www.notion.so/api-v1-member-employee-portfolio-8d150eeb2b3944a5bbd15c7d6f58e139?pvs=4\" target= \"_blank\">이동 하기</a><br>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "피고용인 포트폴리오 등록 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/employee/portfolio")
    public ResponseEntity<ApiResponse<Void>> createEmployeePortfolio(@AuthenticationPrincipal SecurityMember securityMember,
                                                                     @RequestBody EmployeePortfolioDTO dto) {

        employeePortfolioService.createEmployeePortfolio(securityMember.getId(), dto);
        return ApiResponse.success_only(CREATE_EMPLOYEE_PORTFOLIO_SUCCESS);
    }

    @Operation(summary = "피고용인 포트폴리오 조회 API (용범)",
            description = "피고용인의 포트폴리오를 조회합니다.<br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "introduction: 자기소개<br>" +
                    "enrollmentCertificateUrl: 재학증명서 URL<br>" +
                    "transcriptUrl: 성적증명서 URL<br>" +
                    "partTimeWorkPermitUrl: 시간제근로허가서 URL<br>" +
                    "topik: 한국어능력시험<br>" +
                    "businessField: 업직종 <br>" +
                    "experienceDescription: 본인 경력기술<br>" +
                    "startDate: 시작일자(YYYY-MM-DD) <br>" +
                    "endDate: 종료일자(YYYY-MM-DD) <br>" +
                    "businessField: 업직종 <br>" +
                    "certificateName: 자격명<br>" +
                    "certificateDate: 취득일자(YYYY-MM-DD) <br>" +
                    "businessField: 업직종 <br>" +
                    "awardName: 상장명<br>" +
                    "awardDate: 수상날짜(YYYY-MM-DD) <br>" +
                    "portfolioPublic: 포트폴리오 목록 공개 여부 선택" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "피고용인 포트폴리오 조회 성공."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/employee/portfolio")
    public ResponseEntity<ApiResponse<EmployeePortfolioDTO>> getEmployeePortfolio(@AuthenticationPrincipal SecurityMember securityMember) {

        EmployeePortfolioDTO employeePortfolio = employeePortfolioService.getEmployeePortfolio(securityMember.getId());
        return ApiResponse.success(SEND_EMPLOYER_PORTFOLIO_SELECT_SUCCESS, employeePortfolio);
    }

    @Operation(
            summary = "피고용인 포트폴리오 수정 API  (용범)",
            description = "피고용인의 포트폴리오를 수정합니다.<br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "introduction: 자기소개<br>" +
                    "enrollmentCertificateUrl: 재학증명서 URL<br>" +
                    "transcriptUrl: 성적증명서 URL<br>" +
                    "partTimeWorkPermitUrl: 시간제근로허가서 URL<br>" +
                    "topik: 한국어능력시험<br>" +
                    "businessField: 업직종 <br>" +
                    "experienceDescription: 본인 경력기술<br>" +
                    "startDate: 시작일자(YYYY-MM-DD) <br>" +
                    "endDate: 종료일자(YYYY-MM-DD) <br>" +
                    "businessField: 업직종 <br>" +
                    "certificateName: 자격명<br>" +
                    "certificateDate: 취득일자(YYYY-MM-DD) <br>" +
                    "businessField: 업직종 <br>" +
                    "awardName: 상장명<br>" +
                    "awardDate: 수상날짜(YYYY-MM-DD) <br>" +
                    "<p>" +
                    "요청 예시: <a href=\"https://www.notion.so/api-v1-member-employee-portfolio-4561ce52be5042f7890b5a66770f2564?pvs=4\" target= \"_blank\">이동 하기</a><br>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
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

    @Operation(summary = "비밀번호 확인 API (용범)",
            description = "비밀번호를 확인합니다. <br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "password : 사용자 비밀번호"
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

    @Operation(summary = "아이디 변경. API (용범)",
            description = "아이디 변경합니다.<br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "userId: 새로운 아이디<br>"
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

    @Operation(
            summary = "비밀번호 변경. API (용범)",
            description = "비밀번호 변경합니다.<br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "password: 새로운 비밀번호<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 변경 완료."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@AuthenticationPrincipal SecurityMember securityMember,
                                                            @RequestBody PasswordDTO passwordDTO) {

        memberService.updateMemberPassword(securityMember.getId(), passwordDTO.getPassword());
        return ApiResponse.success_only(SEND_UPDATE_USERID_PASSWORD);
    }

    @Operation(
            summary = "비밀번호 초기화 요청 API (태근)",
            description = "[비밀번호 찾기 기능] 이메일로 비밀번호 초기화 링크를 보냅니다. <br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "userId : 사용자 아이디 <br>"
                    + "name : 사용자 이름 <br>"
                    + "email : 사용자 이메일"
    )
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
            summary = "비밀번호 초기화 API (태근)",
            description = "[비밀번호 찾기 기능] 비밀번호 초기화 링크에서 받은 코드를 이용해 비밀번호를 변경합니다. <br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "code : 비밀번호 초기화 인증코드 <br>"
                    + "newPassword : 새 비밀번호"
    )
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
            summary = "고용인이 피고용인 평가 API (용범)",
            description = "고용인이 피고용인을 평가합니다." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "resumeId: 이력서 ID<br>" +
                    "evaluationCategory: 평가 항목들<br>" +
                    "가능한 평가 항목) <br>" +
                    "WORKS_DILIGENTLY: 성실하게 일해요.<br>" +
                    "NO_LATENESS_OR_ABSENCE: 지각/결근하지 않았어요.<br>" +
                    "POLITE_AND_FRIENDLY: 예의 바르고 친절해요.<br>" +
                    "GOOD_CUSTOMER_SERVICE: 고객 응대를 잘해요.<br>" +
                    "SKILLED_AT_WORK: 업무 능력이 좋아요.<br>" +
                    "<p>" +
                    "요청 예시: <a href=\"https://www.notion.so/api-v1-member-evaluations-employer-to-employee-19a98c9adc8a80b4961be9b21fbaa938?pvs=4\" target= \"_blank\">이동 하기</a><br>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "평가 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/evaluations/employer-to-employee")
    public ResponseEntity<ApiResponse<Void>> evaluateEmployee(@AuthenticationPrincipal SecurityMember securityMember,
                                                              @RequestBody EmployerToEmployeeEvaluationRequestDTO dto) {

        if (dto.getEvaluationCategory().isEmpty()) {
            throw new BadRequestException(ErrorStatus.EVALUATION_CATEGORY_IS_EMPTY_EXCEPTION.getMessage());
        }

        evaluationService.evaluateEmployee(dto.getResumeId(), dto.getEvaluationCategory());
        return ApiResponse.success_only(SuccessStatus.EVALUATE_EMPLOYEE_SUCCESS);
    }

    @Operation(
            summary = "피고용인이 고용인 평가. API (용범)",
            description = "피고용인이 고용인을 평가합니다." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "recruitId: 공고 ID<br>" +
                    "evaluationCategory: 평가 항목들<br>" +
                    "가능한 평가 항목) <br>" +
                    "PAYS_ON_TIME: 약속된 급여를 제때 줘요.<br>" +
                    "KEEPS_CONTRACT_DATES: 계약된 날짜를 잘 지켰어요.<br>" +
                    "RESPECTS_EMPLOYEES: 알바생을 존중해줘요.<br>" +
                    "FRIENDLY_BOSS: 사장님이 친절해요.<br>" +
                    "FAIR_WORKLOAD: 업무 강도가 적당해요.<br>" +
                    "<p>" +
                    "요청 예시: <a href=\"https://www.notion.so/api-v1-member-evaluations-employee-to-employer-19a98c9adc8a80bdbdede39e6bb78545?pvs=4\" target= \"_blank\">이동 하기</a><br>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"

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
            summary = "고용인이 피고용인을 평가한 항목 보기 API (용범)",
            description = "고용인이 피고용인을 평가한 항목 보기. 마이페이지(고용인)-지원자 이력서 보기에서 넘어온다." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "resumeId: 이력서 ID<br>" +
                    "WORKS_DILIGENTLY: 성실하게 일해요.<br>" +
                    "NO_LATENESS_OR_ABSENCE: 지각/결근하지 않았어요.<br>" +
                    "POLITE_AND_FRIENDLY: 예의 바르고 친절해요.<br>" +
                    "GOOD_CUSTOMER_SERVICE: 고객 응대를 잘해요.<br>" +
                    "SKILLED_AT_WORK: 업무 능력이 좋아요.<br>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "평가 보기 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/employer-to-employee-evaluations")
    public ResponseEntity<ApiResponse<EvaluationCategoryResponseDTO>> getEmployerToEmployeeEvaluation(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                 @RequestParam("resumeId") Long resumeId) {

        EvaluationCategoryResponseDTO evaluation = evaluationService.getEmployerToEmployeeEvaluation(resumeId);
        return ApiResponse.success(SuccessStatus.EVALUATE_VIEW_SUCCESS, evaluation);
    }

    @Operation(
            summary = "피고용인이 고용인을 평가한 항목 보기 API (용범)",
            description = "피고용인이 고용인을 평가한 항목 보기. 공고 상세 보기에서 넘어온다." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "resumeId: 이력서 ID<br>" +
                    "PAYS_ON_TIME: 약속된 급여를 제때 줘요.<br>" +
                    "KEEPS_CONTRACT_DATES: 계약된 날짜를 잘 지켰어요.<br>" +
                    "RESPECTS_EMPLOYEES: 알바생을 존중해줘요.<br>" +
                    "FRIENDLY_BOSS: 사장님이 친절해요.<br>" +
                    "FAIR_WORKLOAD: 업무 강도가 적당해요.<br>"+
                    "<p>"+
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "평가 보기 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/employee-To-employer-evaluations")
    public ResponseEntity<ApiResponse<EvaluationCategoryResponseDTO>> getEmployeeToEmployerEvaluation(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                 @RequestParam("recruitId") Long recruitId) {

        EvaluationCategoryResponseDTO evaluation = evaluationService.getEmployeeToEmployerEvaluation(securityMember.getId(), recruitId);
        return ApiResponse.success(SuccessStatus.EVALUATE_VIEW_SUCCESS, evaluation);
    }


    @Operation(
            summary = "마이페이지(피고용인)- 리뷰 태그. API (용범)",
            description = "태그 관련 사항들을 모아서 볼 수 있는 페이지.<br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "요청)<br>" +
                    "evaluationStatus: 평가 상태.<br>" +
                    "page: 페이지 번호.<br>" +
                    "size: 한 페이지 당 몇 개?.<br>"+
                    "응답)<br>" +
                    "recruitId: 공고 ID.<br>" +
                    "title: 공고 제목.<br>" +
                    "evaluationStatus: 평가 상태.<br>"+
                    "tagRegistrationDate: 작성일.<br>" +
                    "<p>"+
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "태그 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping("/tags")
    public ResponseEntity<ApiResponse<PageResponseDTO<TagResponseDTO>>> getEvaluation(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                      @RequestParam("evaluationStatus") EvaluationStatus evaluationStatus,
                                                                                      @RequestParam("page") Integer page,
                                                                                      @RequestParam("size") Integer size) {

        PageResponseDTO<TagResponseDTO> tags = resumeService.getTags(securityMember.getId(), evaluationStatus, page, size);
        return ApiResponse.success(SuccessStatus.TAG_VIEW_SUCCESS, tags);
    }

    @Operation(
            summary = "피고용인 완료된 계약서 조회. API (용범)",
            description = "피고용인 완료된 계약서 조회." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "contractId: 계약서 ID.<br>" +
                    "contractType: 계약서 타입.<br>" +
                    "companyName: 점포명.<br>" +
                    "completedDate: 체결일.<br>" +
                    "<p>"+
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/employee/complete-contract")
    public ResponseEntity<ApiResponse<PageResponseDTO<EmployeeCompletedContractResponseDTO>>> getCompletedContractOfEmployee(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                                             @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                                             @RequestParam("size") Integer size) {
        PageResponseDTO<EmployeeCompletedContractResponseDTO> response = contractService.getCompletedContractMetadataOfEmployee(securityMember.getId(), page, size);

        return ApiResponse.success(SuccessStatus.COMPLETE_CONTRACT_VIEW_SUCCESS, response);
    }

    @Operation(
            summary = "고용인 완료된 계약서 조회. API (용범)",
            description = "고용인 완료된 계약서 조회. <br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "recruitId: 공고 ID<br>" +
                    "contractId: 계약서 ID<br>" +
                    "contractType: 계약서 타입<br>" +
                    "title: 공고 제목<br>" +
                    "workDuration: 근무 기간<br>" +
                    "workDays: 근무 요일<br>" +
                    "zipcode: 우편번호<br>" +
                    "address1: 주소<br>" +
                    "address2: 상세주소<br>"+
                    "workTime:  근무 시간<br>" +
                    "recruitStartDate: 모집 시작일<br>" +
                    "recruitEndDate: 모집 종료일<br>" +
                    "businessFields: 업직종<br>" +
                    "salary: 급여<br>" +
                    "salaryType: 급여 타입<br>" +
                    "applyMethods: 접수 방법<br>" +
                    "employeeName: 피고용인 이름<br>" +
                    "<p>"+
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/employer/complete-contract")
    public ResponseEntity<ApiResponse<PageResponseDTO<EmployerCompletedContractResponseDTO>>> getCompletedContractOfEmployer(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                                             @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                                             @RequestParam("size") Integer size) {
        PageResponseDTO<EmployerCompletedContractResponseDTO> response = contractService.getCompletedContractMetadataOfEmployer(securityMember.getId(), page, size);

        return ApiResponse.success(SuccessStatus.COMPLETE_CONTRACT_VIEW_SUCCESS, response);
    }

    @Operation(
            summary = "완료된 계약서(파일 업로드) 상세 조회. API (용범)",
            description = "완료된 계약서(파일 업로드) 상세 조회." +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "요청)<br>" +
                    "contract-id: 계약서 ID<br>" +
                    "응답)<br>" +
                    "fileUrl: fileUrl<br>" +
                    "originalFileName: 원본 파일 이름<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/complete-file-upload-contract/{contract-id}")
    public ResponseEntity<ApiResponse<List<FileUrlAndOriginalFileNameDTO>>> getCompletedFileUploadContract(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                           @PathVariable("contract-id") Long contractMetadataId) {
        List<FileUrlAndOriginalFileNameDTO> response = contractService.getCompletedFileUploadContract(securityMember.getId(), contractMetadataId);

        return ApiResponse.success(SuccessStatus.COMPLETE_CONTRACT_VIEW_SUCCESS, response);
    }

    @Operation(
            summary = "공고 미리보기 조회 API (용범)",
            description = "공고 미리보기 조회 <br>"+
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "요청)<br>" +
                    "recruitId: 공고 ID<br>" +
                    "응답)<br>" +
                    "recruitId: 공고 ID<br>" +
                    "title: 공고 제목<br>" +
                    "recruitStartDate: 모집 시작일<br>" +
                    "recruitEndDate: 모집 종료일<br>" +
                    "workDuration: 근무 기간<br>" +
                    "workDays: 근무 요일<br>" +
                    "workTime: 근무 시간<br>" +
                    "employerReliability: 신뢰도<br>" +
                    "companyName: 회사(점포) 명<br>" +
                    "companyIconImage: 회사 아이콘 이미지 URL<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/employer/recruit-preview")
    public ResponseEntity<ApiResponse<RecruitPreviewResponseDTO>> getRecruitPreview(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                    @RequestParam("recruitId") Long recruitId) {
        RecruitPreviewResponseDTO response = recruitService.getRecruitPreview(recruitId);


        return ApiResponse.success(SuccessStatus.PREVIEW_RECRUIT_SUCCESS, response);
    }

    @Operation(
            summary = "고용인 관심 피고용인 (기본) 조회. API (용범)",
            description = "고용인 관심 피고용인 (기본) 조회 <br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "employeeId: 피고용인 ID<br>" +
                    "name: 피고용인 이름<br>" +
                    "worksDiligently: 성실하게 일해요.<br>" +
                    "noLatenessOrAbsence: 지각/결근하지 않았어요.<br>" +
                    "politeAndFriendly: 예의 바르고 친절해요.<br>" +
                    "goodCustomerService: 고객 응대를 잘해요.<br>" +
                    "skilledAtWork: 업무 능력이 좋아요.<br>" +
                    "joinCount: 평가 참여 수<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/employer/favorite/basic")
    public ResponseEntity<ApiResponse<PageResponseDTO<BasicPortfolioPreviewResponseDTO>>> getMyBasicPortfolios(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                               @RequestParam(value = "size", defaultValue = "6") Integer size) {
        PageResponseDTO<BasicPortfolioPreviewResponseDTO> response = memberService.getMyBasicPortfolios(securityMember.getId(), page, size);


        return ApiResponse.success(SuccessStatus.BASIC_PORTFOLIO_VIEW_SUCCESS, response);
    }

    @Operation(
            summary = "고용인 관심 피고용인 (실제 지원) 조회. API (용범)",
            description = "고용인 관심 피고용인 (실제 지원) 조회 <br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "응답)<br>" +
                    "resumeId: 피고용인 ID<br>" +
                    "name: 피고용인 이름<br>" +
                    "businessFields: 업직종<br>" +
                    "worksDiligently: 성실하게 일해요.<br>" +
                    "noLatenessOrAbsence: 지각/결근하지 않았어요.<br>" +
                    "politeAndFriendly: 예의 바르고 친절해요.<br>" +
                    "goodCustomerService: 고객 응대를 잘해요.<br>" +
                    "skilledAtWork: 업무 능력이 좋아요.<br>" +
                    "joinCount: 평가 참여 수<br>" +
                    "<p>"+
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/employer/favorite/application")
    public ResponseEntity<ApiResponse<PageResponseDTO<ApplicationPortfolioPreviewResponseDTO>>> getMyApplicationPortfolios(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                                           @RequestParam(value = "size", defaultValue = "6") Integer size) {
        PageResponseDTO<ApplicationPortfolioPreviewResponseDTO> response = memberService.getMyApplicationPortfolios(securityMember.getId(), page, size);


        return ApiResponse.success(SuccessStatus.APPLICATION_PORTFOLIO_VIEW_SUCCESS, response);
    }

    @Operation(
            summary = "피고용인 이력서 공개/비공개 변경 API (용범)",
            description = "피고용인 이력서 공개/비공개 변경. API"+
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "resume-id: 피고용인 ID<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping(value = "/employee/resumes/{resume-id}/visibility")
    public ResponseEntity<ApiResponse<Void>> flipResumePublic(@AuthenticationPrincipal SecurityMember securityMember,
                                                              @PathVariable("resume-id") Long resumeId) {

        resumeService.flipResumePublic(securityMember.getId(), resumeId);

        return ApiResponse.success_only(SuccessStatus.RESUME_VISIBILITY_UPDATE_SUCCESS);
    }


    @Operation(
            summary = "회원 탈퇴하기 API (용범)",
            description = "회원 탈퇴하기. API <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "탈퇴 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @DeleteMapping(value = "/withdraw")
    public ResponseEntity<ApiResponse<Void>> withdrawMember(@AuthenticationPrincipal SecurityMember securityMember) {

        memberService.withdrawMember(securityMember.getId());

        return ApiResponse.success_only(SuccessStatus.MEMBER_WITHDRAW_SUCCESS);
    }

    @Operation(
            summary = "마이페이지(피고용인) 이력서 조회. API (용범)",
            description = "마이페이지(피고용인) 이력서 조회." +
                    "<p>"+
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/employee/my-resumes/{resume-id}")
    public ResponseEntity<ApiResponse<MyResumeResponseDTO>> getMyResume(@AuthenticationPrincipal SecurityMember securityMember,
                                                                        @PathVariable("resume-id") Long resumeId) {

        MyResumeResponseDTO myResume = memberService.getMyResume(securityMember.getId(), resumeId);

        return ApiResponse.success(SuccessStatus.SEND_MY_RESUME_SUCCESS, myResume);
    }


    @Operation(
            summary = "고용인이 피고용인 평가 삭제 (용범)",
            description = "고용인이 피고용인 평가 삭제 <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "평가 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @DeleteMapping(value = "/evaluations/employer-to-employee")
    public ResponseEntity<ApiResponse<Void>> deleteEmployerToEmployeeEvaluation(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                @RequestParam("resumeId") Long resumeId) {
        evaluationService.deleteEmployerToEmployeeEvaluation(securityMember.getId(), resumeId);

        return ApiResponse.success_only(SuccessStatus.EVALUATION_DELETE_SUCCESS);
    }

    @Operation(
            summary = "피고용인이 고용인 평가 삭제 (용범)",
            description = "피고용인이 고용인 평가 삭제 <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "평가 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @DeleteMapping(value = "/evaluations/employee-to-employer")
    public ResponseEntity<ApiResponse<Void>> deleteEmployeeToEmployerEvaluation(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                @RequestParam("resumeId") Long resumeId) {
        evaluationService.deleteEmployeeToEmployerEvaluation(securityMember.getId(), resumeId);

        return ApiResponse.success_only(SuccessStatus.EVALUATION_DELETE_SUCCESS);
    }

}