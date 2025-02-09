package com.core.foreign.api.albareview.dto;

import com.core.foreign.api.business_field.BusinessField;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AlbaReviewDetailDTO {
    private Long id;                // 게시글ID
    private String region1;         // (시/도)
    private String region2;         // (시/구/군)
    private String title;           // 후기 제목
    private long readCount;         // 조회 수
    private long commentCount;      // 댓글 수
    private String content;         // 후기 내용
    private String userId;          // 작성자(회원의 userId)
    private boolean isMine;         // 현재 로그인한 사용자가 작성한 후기인지 여부
    private String createdAt;       // 생성일자
    private BusinessField businessField; // 업직종
}
