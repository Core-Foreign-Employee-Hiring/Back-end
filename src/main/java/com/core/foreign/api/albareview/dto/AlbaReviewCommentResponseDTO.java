package com.core.foreign.api.albareview.dto;

import lombok.Data;

@Data
public class AlbaReviewCommentResponseDTO {
    private String userId;     // 댓글 작성자 ID
    private String comment;  // 댓글 내용
    private String createdAt; // 작성일 (yyyy.MM.dd)
    private Long parentId; // 부모 댓글 ID
}
