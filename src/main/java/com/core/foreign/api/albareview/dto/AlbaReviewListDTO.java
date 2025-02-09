package com.core.foreign.api.albareview.dto;

import com.core.foreign.api.business_field.BusinessField;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlbaReviewListDTO {
    private Long id;                    // 게시글ID
    private String region1;             // (시/도)
    private String region2;             // (시/구/군)
    private BusinessField businessField; // 업직종
    private String title;               // 후기 제목
    private String content;             // 후기 내용
    private String createdAt;           // 생성일자
    private long readCount;             // 조회 수
    private long commentCount;          // 댓글 수
}
