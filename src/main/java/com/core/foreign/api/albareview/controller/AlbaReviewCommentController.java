package com.core.foreign.api.albareview.controller;

import com.core.foreign.api.albareview.dto.AlbaReviewCommentCreateDTO;
import com.core.foreign.api.albareview.dto.AlbaReviewCommentResponseDTO;
import com.core.foreign.api.albareview.dto.AlbaReviewCommentUpdateDTO;
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
            summary = "알바 후기 댓글 작성 API (태근)",
            description = "새로운 알바 후기 댓글을 작성합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "content : 댓글 내용 <br>"
                    + "reviewId : 알바 후기 ID <br>"
                    + "parentId : 부모 댓글 ID (부모 댓글 작성 시 null) <br>"
    )
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
            summary = "알바 후기 댓글 조회 API (태근)",
            description = "리뷰 ID에 해당하는 댓글들을 최신순(생성일 내림차순)으로 반환합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "reviewId : 알바 후기 ID <br>"
                    + "<p>"
                    + "내 댓글 여부 : 'mine : true' 일 때, 현재 로그인 사용자가 작성한 댓글입니다."
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알바 후기 댓글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/comment")
    public ResponseEntity<ApiResponse<List<AlbaReviewCommentResponseDTO>>> getCommentsByReviewId(
            @AuthenticationPrincipal SecurityMember securityMember,
            @RequestParam Long reviewId) {

        Long memberId = (securityMember != null) ? securityMember.getId() : null;
        List<AlbaReviewCommentResponseDTO> comments = albaReviewCommentService.getCommentsByReviewId(reviewId, memberId);
        return ApiResponse.success(SuccessStatus.SEND_ALBA_REVIEW_COMMENT_SUCCESS, comments);
    }

    @Operation(
            summary = "알바 후기 댓글 수정 API (태근)",
            description = "댓글 작성자만 댓글을 수정할 수 있습니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "commentId : 수정할 댓글의 ID <br>"
                    + "content : 수정할 댓글 내용 <br>"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알바 후기 댓글 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateAlbaReviewComment(
            @AuthenticationPrincipal SecurityMember securityMember,
            @PathVariable Long commentId,
            @RequestBody AlbaReviewCommentUpdateDTO albaReviewCommentUpdateDTO) {

        albaReviewCommentService.updateAlbaReviewComment(commentId, albaReviewCommentUpdateDTO, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.ALBA_REVIEW_COMMENT_UPDATE_SUCCESS);
    }

    @Operation(
            summary = "알바 후기 댓글 삭제 API (태근)",
            description = "댓글 작성자만 댓글을 삭제할 수 있습니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "commentId : 삭제할 댓글의 ID <br>"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알바 후기 댓글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteAlbaReviewComment(
            @AuthenticationPrincipal SecurityMember securityMember,
            @PathVariable Long commentId) {

        albaReviewCommentService.deleteAlbaReviewComment(commentId, securityMember.getId());
        return ApiResponse.success_only(SuccessStatus.ALBA_REVIEW_COMMENT_DELETE_SUCCESS);
    }
}
