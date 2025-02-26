package com.core.foreign.api.albareview.controller;

import com.core.foreign.api.albareview.dto.AlbaReviewCreateDTO;
import com.core.foreign.api.albareview.dto.AlbaReviewDetailDTO;
import com.core.foreign.api.albareview.dto.AlbaReviewPageResponseDTO;
import com.core.foreign.api.albareview.service.AlbaReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.core.foreign.common.SecurityMember;
import com.core.foreign.common.response.ApiResponse;
import com.core.foreign.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AlbaReview", description = "알바후기 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/albareview")
@RequiredArgsConstructor
public class AlbaReviewController {

    private final AlbaReviewService albaReviewService;

    @Operation(
            summary = "알바 후기 작성 API",
            description = "새로운 알바 후기를 작성합니다.<br>"+
                    "region1 : (시/도) <br>" +
                    "region2 : (시/구/군) <br>" +
                    "<p>" +
                    "businessField 종류<br>" +
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
                    "OTHER_SERVICE : 기타/서비스")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알바 후기 작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createAlbaReview(
            @AuthenticationPrincipal SecurityMember securityMember,
            @RequestBody AlbaReviewCreateDTO albaReviewCreateDTO) {

        albaReviewService.createAlbaReview(albaReviewCreateDTO, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.ALBA_REVIEW_CREATE_SUCCESS);
    }

    @Operation(
            summary = "알바 후기 상세조회 API",
            description = "알바 후기 게시글 ID를 받아 상세 정보를 조회합니다.<br>" +
                    "현재 사용자가 작성한 글인지 여부(mine)"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알바 후기 상세조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "알바 후기를 찾을 수 없습니다.")
    })
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<AlbaReviewDetailDTO>> getAlbaReviewDetail(
            @AuthenticationPrincipal SecurityMember securityMember,
            @PathVariable("reviewId") Long reviewId) {

        Long memberId = (securityMember != null) ? securityMember.getId() : null;
        AlbaReviewDetailDTO albaReviewDetail = albaReviewService.getAlbaReviewDetail(reviewId, memberId);
        return ApiResponse.success(SuccessStatus.ALBA_REVIEW_DETAIL_SUCCESS, albaReviewDetail);
    }

    @Operation(
            summary = "알바 후기 전체조회 API",
            description = "알바 후기 전체목록을 페이지네이션과 함께 조회합니다.<br>" +
                    "정렬조건(sortType) popular : 조회수 / newest : 최신순"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알바 후기 전체조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<AlbaReviewPageResponseDTO>> getAlbaReviewList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "newest") String sortType) {

        AlbaReviewPageResponseDTO albaReviewPageResponseDTO = albaReviewService.getAlbaReviewList(sortType, page, size);
        return ApiResponse.success(SuccessStatus.ALBA_REVIEW_LIST_SUCCESS, albaReviewPageResponseDTO);
    }

    @Operation(
            summary = "알바 후기 수정 API",
            description = "알바 후기 게시글 ID를 받아 해당 후기를 수정합니다.<br>" +
                    "수정은 작성자만 가능하며, 미로그인 시 수정할 수 없습니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알바 후기 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "수정 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "알바 후기를 찾을 수 없습니다.")
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> updateAlbaReview(
            @AuthenticationPrincipal SecurityMember securityMember,
            @PathVariable("reviewId") Long reviewId,
            @RequestBody AlbaReviewCreateDTO albaReviewCreateDTO) {

        albaReviewService.updateAlbaReview(reviewId, albaReviewCreateDTO, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.ALBA_REVIEW_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "알바 후기 삭제 API",
            description = "알바 후기 게시글 ID를 받아 해당 후기를 삭제합니다.<br>" +
                    "삭제는 작성자만 가능하며, 미로그인 시 삭제할 수 없습니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알바 후기 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "삭제 권한이 없습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "알바 후기를 찾을 수 없습니다.")
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteAlbaReview(
            @AuthenticationPrincipal SecurityMember securityMember,
            @PathVariable("reviewId") Long reviewId) {

        albaReviewService.deleteAlbaReview(reviewId, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.ALBA_REVIEW_DELETE_SUCCESS);
    }

    @Operation(
            summary = "알바 후기 검색 API",
            description = "제목이나 본문 내용에 검색어(keyword)를 포함하는 알바 후기를 연관도(제목 가중치 우선)와 최신순으로 조회합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알바 후기 검색 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<AlbaReviewPageResponseDTO>> searchAlbaReview(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        AlbaReviewPageResponseDTO result = albaReviewService.searchAlbaReview(keyword, page, size);
        return ApiResponse.success(SuccessStatus.ALBA_REVIEW_SEARCH_SUCCESS, result);
    }
}
