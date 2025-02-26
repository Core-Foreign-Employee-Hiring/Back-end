package com.core.foreign.api.portfolio.controller;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.portfolio.dto.ApplicationPortfolioPreviewResponseDTO;
import com.core.foreign.api.portfolio.dto.ApplicationPortfolioResponseDTO;
import com.core.foreign.api.portfolio.dto.BasicPortfolioPreviewResponseDTO;
import com.core.foreign.api.portfolio.dto.BasicPortfolioResponseDTO;
import com.core.foreign.api.portfolio.service.PortfolioService;
import com.core.foreign.api.recruit.dto.PageResponseDTO;
import com.core.foreign.common.SecurityMember;
import com.core.foreign.common.response.ApiResponse;
import com.core.foreign.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Portfolio", description = "Portfolio 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;


    @Operation(summary = "포트폴리오 기본 조회. API",
            description = "포트폴리오 기본 조회. <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/basics")
    public ResponseEntity<ApiResponse<PageResponseDTO<BasicPortfolioPreviewResponseDTO>>> getBasicPortfolios(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                  @RequestParam(value = "page", defaultValue = "0") Integer page) {
        PageResponseDTO<BasicPortfolioPreviewResponseDTO> basicPortfolios = portfolioService.getBasicPortfolios(page);

        return ApiResponse.success(SuccessStatus.BASIC_PORTFOLIO_VIEW_SUCCESS, basicPortfolios);
    }

    @Operation(summary = "포트폴리오 기본 상세 조회. API",
            description = "포트폴리오 기본 상세 조회. <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/basics/{employee-id}")
    public ResponseEntity<ApiResponse<BasicPortfolioResponseDTO>> getBasicPortfolio(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                  @PathVariable("employee-id") Long employeeId) {
        BasicPortfolioResponseDTO basicPortfolio = portfolioService.getBasicPortfolio(employeeId);

        return ApiResponse.success(SuccessStatus.BASIC_PORTFOLIO_VIEW_SUCCESS, basicPortfolio);
    }


    @Operation(summary = "포트폴리오 실제 지원 조회. API",
            description = "포트폴리오 실제 지원 조회. <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/applications")
    public ResponseEntity<ApiResponse<PageResponseDTO<ApplicationPortfolioPreviewResponseDTO>>> getApplicationPortfolios(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                              @RequestParam("filter") BusinessField businessField) {
        PageResponseDTO<ApplicationPortfolioPreviewResponseDTO> applicationPortfolios = portfolioService.getApplicationPortfolios(page, businessField);

        return ApiResponse.success(SuccessStatus.APPLICATION_PORTFOLIO_VIEW_SUCCESS, applicationPortfolios);
    }

    @Operation(summary = "포트폴리오 실제 지원 상세 조회. API",
            description = "포트폴리오 실제 지원 상세 조회. <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/applications/{resume-id}")
    public ResponseEntity<ApiResponse<ApplicationPortfolioResponseDTO>> getApplicationPortfolio(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                              @PathVariable("resume-id") Long resumeId) {
        ApplicationPortfolioResponseDTO applicationPortfolio = portfolioService.getApplicationPortfolio(resumeId);

        return ApiResponse.success(SuccessStatus.APPLICATION_PORTFOLIO_VIEW_SUCCESS, applicationPortfolio);
    }
}
