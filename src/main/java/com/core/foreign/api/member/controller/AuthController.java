package com.core.foreign.api.member.controller;

import com.core.foreign.api.member.dto.EmailRequestDTO;
import com.core.foreign.api.member.dto.SmsRequestDTO;
import com.core.foreign.api.member.service.EmailService;
import com.core.foreign.api.member.service.SmsService;
import com.core.foreign.common.SecurityMember;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.response.ApiResponse;
import com.core.foreign.common.response.ErrorStatus;
import com.core.foreign.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "Member", description = "Member 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class AuthController {

    private final EmailService emailService;
    private final SmsService smsService;

    @Operation(
            summary = "이메일 인증코드 발송 API (태근)",
            description = "이메일 인증코드를 발송합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "email : 사용자 이메일"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 인증코드 발송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "올바른 이메일 형식이 아닙니다."),
    })
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> getEmailVerification(@RequestBody EmailRequestDTO.EmailVerificationRequest request) {
        LocalDateTime requestedAt = LocalDateTime.now();
        String email = request.getEmail();

        // Apache Commons EmailValidator 검증
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new BadRequestException(ErrorStatus.VALIDATION_EMAIL_FORMAT_EXCEPTION.getMessage());
        }

        emailService.sendVerificationEmail(email, requestedAt);
        return ApiResponse.success_only(SuccessStatus.SEND_EMAIL_VERIFICATION_CODE_SUCCESS);
    }

    @Operation(
            summary = "이메일 코드 인증 API (태근)",
            description = "발송된 이메일 인증 코드를 검증합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "code : 이메일로 발송된 인증코드"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 코드 인증 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "이메일 인증코드가 올바르지 않습니다."),
    })
    @PostMapping("/verification-email-code")
    public ResponseEntity<ApiResponse<Void>> verificationByCode(@RequestBody EmailRequestDTO.VerificationCodeRequest request) {
        LocalDateTime requestedAt = LocalDateTime.now();
        emailService.verifyEmail(request.getCode(), requestedAt);
        return ApiResponse.success_only(SuccessStatus.SEND_EMAIL_VERIFICATION_SUCCESS);
    }

    @Operation(
            summary = "SMS 인증코드 발송 API (태근)",
            description = "휴대폰으로 인증코드를 발송합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "phoneNumber : 전화번호 (예시 : 01012345678)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SMS 인증코드 발송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "휴대폰 번호 형식이 올바르지 않습니다."),
    })
    @PostMapping("/verify-phone")
    public ResponseEntity<ApiResponse<Void>> sendVerificationSms(@RequestBody SmsRequestDTO.SmsVerificationRequest request) {
        String phoneNumber = request.getPhoneNumber();
        LocalDateTime requestedAt = LocalDateTime.now();

        if (StringUtils.isBlank(phoneNumber) || !phoneNumber.matches("\\d{10,11}")) {
            throw new BadRequestException(ErrorStatus.VALIDATION_PHONE_FORMAT_EXCEPTION.getMessage());
        }

        smsService.sendVerificationSms(phoneNumber, requestedAt);
        return ApiResponse.success_only(SuccessStatus.SEND_SMS_VERIFICATION_CODE_SUCCESS);
    }

    @Operation(
            summary = "SMS 코드 인증 API (태근)",
            description = "발송된 SMS 인증 코드를 검증합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "code : 문자로 발송된 인증코드"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "SMS 코드 인증 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "SMS 인증코드가 올바르지 않습니다."),
    })
    @PostMapping("/verification-phone-code")
    public ResponseEntity<ApiResponse<Void>> verifyPhoneCode(@RequestBody SmsRequestDTO.VerificationCodeRequest request) {
        LocalDateTime requestedAt = LocalDateTime.now();
        smsService.verifyCode(request.getCode(), requestedAt);
        return ApiResponse.success_only(SuccessStatus.SEND_VERIFY_SMS_CODE_SUCCESS);
    }

    @Operation(
            summary = "내 이메일 인증코드 발송 API (용범)",
            description = "내 이메일 인증코드를 발송합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 인증코드 발송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "올바른 이메일 형식이 아닙니다."),
    })
    @PostMapping("/verify-my-email")
    public ResponseEntity<ApiResponse<Void>> getMyEmailVerification(@AuthenticationPrincipal SecurityMember securityMember) {

        emailService.sendVerificationMyEmail(securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.SEND_EMAIL_VERIFICATION_CODE_SUCCESS);
    }
}
