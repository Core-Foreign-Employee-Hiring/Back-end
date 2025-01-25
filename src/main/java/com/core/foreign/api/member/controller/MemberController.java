package com.core.foreign.api.member.controller;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.dto.*;
import com.core.foreign.api.member.jwt.service.JwtService;
import com.core.foreign.api.member.service.EmailService;
import com.core.foreign.api.member.service.MemberService;
import com.core.foreign.common.SecurityMember;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.response.ApiResponse;
import com.core.foreign.common.response.ErrorStatus;
import com.core.foreign.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Member", description = "Member 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Operation(
            summary = "피고용인 회원가입 API",
            description = "피고용인 회원가입을 진행합니다. / 전화번호 전달 형식 : 01012345678"
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
            description = "고용주 회원가입을 진행합니다. / 설립일 전달 형식 : 2025-01-01, 전화번호 전달 형식 : 01012345678"
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
            description = "고용주의 이름, 생년월일, 성별, 이메일, 휴대폰 번호, 주소, 약관 동의를 조회합니다."
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
            description = "고용주의 이름, 생년월일, 성별을 수정합니다."
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

    /**
     * @apiNote
     * 이메일 변경 시 토큰 조심
     */
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
            description = "고용주의 약관 동의를 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "고용주 약관 동의 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping("/employer/profile/agreements")
    public ResponseEntity<ApiResponse<Void>> updateEmployerAgreements(@RequestParam boolean termsOfServiceAgreement,
                                                                      @RequestParam boolean personalInfoAgreement,
                                                                      @RequestParam boolean adInfoAgreementSnsMms,
                                                                      @RequestParam boolean adInfoAgreementEmail,
                                                                      @AuthenticationPrincipal SecurityMember securityMember) {

        memberService.updateEmployerAgreement(securityMember.getId(), termsOfServiceAgreement, personalInfoAgreement, adInfoAgreementSnsMms, adInfoAgreementEmail);
        return ApiResponse.success_only(SuccessStatus.SEND_PROFILE_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "이메일 중복 확인 API",
            description = "이메일 중복 확인하여 사용가능한 이메일인지 판단합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용가능한 이메일입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "사용할 수 없는 이메일입니다."),
    })
    @PostMapping("/duplication/email")
    public ResponseEntity<ApiResponse<Boolean>> isEmailDuplication(@RequestParam String email) {
        boolean duplicateEmail = memberService.isDuplicateEmail(email);

        return ApiResponse.success(SuccessStatus.SEND_EMAIL_DUPLICATION_SUCCESS, duplicateEmail);
    }


    @Operation(
            summary = "휴대폰 번호 중복 확인 API",
            description = "휴대폰 번호 중복 확인하여 사용가능한지 판단합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용가능한 휴대폰 번호입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "사용할 수 없는 휴대폰 번호입니다."),
    })
    @PostMapping("/duplication/phone-number")
    public ResponseEntity<ApiResponse<Boolean>> isPhoneNumberDuplication(@RequestParam String phoneNumber) {
        boolean duplicatePhoneNumber = memberService.isDuplicatePhoneNumber(phoneNumber);

        return ApiResponse.success(SuccessStatus.SEND_EMAIL_DUPLICATION_SUCCESS, duplicatePhoneNumber);
    }

    @Operation(
            summary = "고용주의 업직종을 수정 API",
            description = "고용주의 업직종을 수정합니다. 최대 5개"
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

        return ApiResponse.success_only(SuccessStatus.SEND_EMAIL_DUPLICATION_SUCCESS);
    }
}