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
            summary = "알바 후기 작성 API (태근)",
            description = "새로운 알바 후기를 작성합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "title : 후기 제목 <br>"
                    + "content : 후기 내용 <br>"
                    + "region1 : 시/도 <br>"
                    + "region2 : 시/구/군 <br>"
                    + "businessField : 업직종(FOOD_BEVERAGE, STORE_SALES, 등) <br>"
                    + "<p>"
                    + "ENUM : <A href=\"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\"> 이동 하기 </A>"
    )
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
            summary = "알바 후기 상세조회 API (태근)",
            description = "알바 후기 게시글 ID를 받아 상세 정보를 조회합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "reviewId : 후기 게시글 ID <br>"
                    + "<p>"
                    + "현재 사용자가 작성한 글인지 여부는 'mine' 필드로 확인"
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
            summary = "알바 후기 전체조회 API (태근)",
            description = "알바 후기 전체 목록을 페이지네이션과 함께 조회합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "page : 페이지 번호(0부터 시작) <br>"
                    + "size : 한 페이지당 개수 <br>"
                    + "sortType : 정렬조건('popular'=조회수, 'newest'=최신순) <br>"
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
            summary = "알바 후기 수정 API (태근)",
            description = "알바 후기 게시글 ID를 받아 해당 후기를 수정합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "reviewId : 수정할 후기 게시글 ID <br>"
                    + "title : 후기 제목 <br>"
                    + "content : 후기 내용 <br>"
                    + "region1 : 시/도 <br>"
                    + "region2 : 시/구/군 <br>"
                    + "businessField : 업직종(FOOD_BEVERAGE, STORE_SALES, 등) <br>"
                    + "<p>"
                    + "작성자 본인만 수정 가능"
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
            summary = "알바 후기 삭제 API (태근)",
            description = "알바 후기 게시글 ID를 받아 해당 후기를 삭제합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "reviewId : 삭제할 후기 게시글 ID <br>"
                    + "<p>"
                    + "작성자 본인만 삭제 가능"
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
            summary = "알바 후기 검색 API (태근)",
            description = "제목이나 본문 내용에 검색어(keyword)를 포함하는 알바 후기를 조회합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "keyword : 검색어 <br>"
                    + "page, size : 페이지네이션 <br>"
                    + "<p>"
                    + "제목 가중치가 더 높게 설정되어있습니다."
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
