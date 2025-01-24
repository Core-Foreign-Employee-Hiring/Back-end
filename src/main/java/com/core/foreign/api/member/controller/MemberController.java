package com.core.foreign.api.member.controller;

import com.core.foreign.api.member.dto.*;
import com.core.foreign.api.member.jwt.service.JwtService;
import com.core.foreign.api.member.service.EmailService;
import com.core.foreign.api.member.service.MemberService;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.response.ApiResponse;
import com.core.foreign.common.response.ErrorStatus;
import com.core.foreign.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDateTime;
import java.util.Map;

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

}