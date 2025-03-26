package com.core.foreign.api.portfolio.controller;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.portfolio.dto.response.ApplicationPortfolioPreviewResponseDTO;
import com.core.foreign.api.portfolio.dto.response.ApplicationPortfolioResponseDTO;
import com.core.foreign.api.portfolio.dto.response.BasicPortfolioPreviewResponseDTO;
import com.core.foreign.api.portfolio.dto.response.BasicPortfolioResponseDTO;
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

import java.util.List;

@Tag(name = "Portfolio", description = "Portfolio 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;


    @Operation(
            summary = "포트폴리오 기본 조회 API (용범)",
            description = "포트폴리오 기본 조회. <br>"+
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
    @GetMapping(value = "/basics")
    public ResponseEntity<ApiResponse<PageResponseDTO<BasicPortfolioPreviewResponseDTO>>> getBasicPortfolios(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                             @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                             @RequestParam("size") Integer size) {
        PageResponseDTO<BasicPortfolioPreviewResponseDTO> basicPortfolios = portfolioService.getBasicPortfolios(page, size);

        return ApiResponse.success(SuccessStatus.BASIC_PORTFOLIO_VIEW_SUCCESS, basicPortfolios);
    }

    @Operation(
            summary = "포트폴리오 기본 상세 조회 API (용범)",
            description = "포트폴리오 기본 상세 조회. <br>" +
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "employeeId: 피고용인<br>" +
                    "name: 피고용인 이름<br>" +
                    "nationality: 국적<br>" +
                    "education: 학력<br>" +
                    "visa: 비자<br>" +
                    "birthday: 생년월일<br>" +
                    "email: 이메일<br>" +
                    
                    "introduction: 자기소개<br>" +
                    "enrollmentCertificateUrl: 재학증명서<br>" +
                    "transcriptUrl: 성적증명서 <br>" +
                    "partTimeWorkPermitUrl: 시간제근로허가서 <br>" +
                    "topik: 한국어능력시험 <br>" +
                    "businessField: 업직종 <br>" +
                    "experienceDescription: 본인 경력기술 <br>" +
                    "startDate: 시작일자 <br>" +
                    "endDate: 종료일자 <br>" +
                    "businessField: 업직종 <br>" +
                    "certificateName: 자격명 <br>" +
                    "certificateDate: 취득일자 <br>" +
                    "businessField: 업직종 <br>" +
                    "awardName: 상장명 <br>" +
                    "awardDate: 수상날짜 <br>" +
                    "portfolioPublic: 공개/비공개 <br>" +

                    "worksDiligently: 성실하게 일해요.<br>" +
                    "noLatenessOrAbsence: 지각/결근하지 않았어요.<br>" +
                    "politeAndFriendly: 예의 바르고 친절해요.<br>" +
                    "goodCustomerService: 고객 응대를 잘해요.<br>" +
                    "skilledAtWork: 업무 능력이 좋아요.<br>" +
                    "joinCount: 평가 참여 수<br>"+
                    
                    "viewCount: 조회수 <br>" +
                    "isLiked: 찜하기 유무 <br>"+
                    "<p>"+
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/basics/{employee-id}")
    public ResponseEntity<ApiResponse<BasicPortfolioResponseDTO>> getBasicPortfolio(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                    @PathVariable("employee-id") Long employeeId) {

        Long memberId=securityMember!=null?securityMember.getId():null;

        BasicPortfolioResponseDTO basicPortfolio = portfolioService.getBasicPortfolio(memberId, employeeId);

        return ApiResponse.success(SuccessStatus.BASIC_PORTFOLIO_VIEW_SUCCESS, basicPortfolio);
    }


    @Operation(
            summary = "포트폴리오 실제 지원 조회 API (용범)",
            description = "포트폴리오 실제 지원 조회. <br>"+
                    "<p>" +
                    "호출 필드 정보) <br>" +
                    "응답)<br>"+
                    "resumeId: 이력서 ID <br>" +
                    "name: 피고용인 이름 <br>" +
                    "businessFields: 업직종들 <br>" +
                    "worksDiligently: 성실하게 일해요.<br>" +
                    "noLatenessOrAbsence: 지각/결근하지 않았어요.<br>" +
                    "politeAndFriendly: 예의 바르고 친절해요.<br>" +
                    "goodCustomerService: 고객 응대를 잘해요.<br>" +
                    "skilledAtWork: 업무 능력이 좋아요.<br>" +
                    "joinCount: 평가 참여 수<br>" +
                    "<p>" +
                    "ENUM 정보 : <A href = \"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/applications")
    public ResponseEntity<ApiResponse<PageResponseDTO<ApplicationPortfolioPreviewResponseDTO>>> getApplicationPortfolios(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                                         @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                                         @RequestParam(value = "filter", required = false) List<BusinessField> businessField,
                                                                                                                         @RequestParam("size") Integer size) {
        PageResponseDTO<ApplicationPortfolioPreviewResponseDTO> applicationPortfolios = portfolioService.getApplicationPortfolios(page, size, businessField);

        return ApiResponse.success(SuccessStatus.APPLICATION_PORTFOLIO_VIEW_SUCCESS, applicationPortfolios);
    }

    @Operation(
            summary = "포트폴리오 실제 지원 상세 조회 API (용범)",
            description = "포트폴리오 실제 지원 상세 조회. <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/applications/{resume-id}")
    public ResponseEntity<ApiResponse<ApplicationPortfolioResponseDTO>> getApplicationPortfolio(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                @PathVariable("resume-id") Long resumeId) {

        Long memberId=securityMember!=null?securityMember.getId():null;

        ApplicationPortfolioResponseDTO applicationPortfolio = portfolioService.getApplicationPortfolio(memberId, resumeId);

        return ApiResponse.success(SuccessStatus.APPLICATION_PORTFOLIO_VIEW_SUCCESS, applicationPortfolio);
    }

    @Operation(
            summary = "포트폴리오 기본 찜하기 API (용범)",
            description = "포트폴리오 기본 찜하기. <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "찜하기"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/basics/{employee-id}")
    public ResponseEntity<ApiResponse<Boolean>> flipEmployerEmployee(@AuthenticationPrincipal SecurityMember securityMember,
                                                                     @PathVariable("employee-id") Long employeeId) {
        boolean flip = portfolioService.flipEmployerEmployee(securityMember.getId(), employeeId);

        return ApiResponse.success(SuccessStatus.UPDATE_RECRUIT_BOOKMARK_STATUS_SUCCESS, flip);
    }

    @Operation(
            summary = "포트폴리오 실제 지원 찜하기 API (용범)",
            description = "포트폴리오 실제 지원 찜하기. <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "찜하기"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/applications/{resume-id}")
    public ResponseEntity<ApiResponse<Boolean>> flipEmployerResume(@AuthenticationPrincipal SecurityMember securityMember,
                                                                   @PathVariable("resume-id") Long resumeId) {
        boolean flip = portfolioService.flipEmployerResume(securityMember.getId(), resumeId);

        return ApiResponse.success(SuccessStatus.UPDATE_RECRUIT_BOOKMARK_STATUS_SUCCESS, flip);
    }
}
