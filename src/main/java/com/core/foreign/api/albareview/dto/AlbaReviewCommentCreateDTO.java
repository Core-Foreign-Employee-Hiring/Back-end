package com.core.foreign.api.albareview.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class AlbaReviewCommentCreateDTO {

    private String content; // 댓글 내용
    private Long reviewId;  // 알바 리뷰
    private Long parentId;  // 부모 댓글
}
