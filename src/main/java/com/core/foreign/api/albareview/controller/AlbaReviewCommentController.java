package com.core.foreign.api.albareview.controller;

import com.core.foreign.api.albareview.dto.AlbaReviewCommentCreateDTO;
import com.core.foreign.api.albareview.dto.AlbaReviewCommentResponseDTO;
import com.core.foreign.api.albareview.service.AlbaReviewCommentService;
import com.core.foreign.common.SecurityMember;
import com.core.foreign.common.response.ApiResponse;
import com.core.foreign.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AlbaReview", description = "알바후기 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/albareview")
@RequiredArgsConstructor
public class AlbaReviewCommentController {

    private final AlbaReviewCommentService albaReviewCommentService;

    @Operation(
            summary = "알바 후기 댓글 작성 API",
            description = "새로운 알바 후기 댓글을 작성합니다. <br>" +
                    "만약 등록 하는 댓글이 부모댓글일 경우 parentId : null' 로 하시면 됩니다!")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "알바 후기 댓글 작성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @PostMapping("/comment")
    public ResponseEntity<ApiResponse<Void>> createAlbaReviewComment(
            @AuthenticationPrincipal SecurityMember securityMember,
            @RequestBody AlbaReviewCommentCreateDTO albaReviewCreateDTO) {

        albaReviewCommentService.createAlbaReviewComment(albaReviewCreateDTO, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.ALBA_REVIEW_COMMENT_CREATE_SUCCESS);
    }

    @Operation(
            summary = "알바 후기 댓글 조회 API",
            description = "리뷰 ID에 해당하는 댓글들을 최신순(생성일 내림차순)으로 반환합니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알바 후기 댓글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/comment")
    public ResponseEntity<ApiResponse<List<AlbaReviewCommentResponseDTO>>> getCommentsByReviewId(
            @RequestParam Long reviewId) {

        List<AlbaReviewCommentResponseDTO> comments = albaReviewCommentService.getCommentsByReviewId(reviewId);
        return ApiResponse.success(SuccessStatus.SEND_ALBA_REVIEW_COMMENT_SUCCESS, comments);
    }
}
