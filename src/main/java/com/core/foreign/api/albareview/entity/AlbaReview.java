package com.core.foreign.api.albareview.entity;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "albareview")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class AlbaReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;       // 후기 제목
    @Column(columnDefinition = "TEXT")
    private String content;     // 후기 내용

    private BusinessField businessField; // 업직종

    private String region1;     // (시/도)
    private String region2;     // (시/구/군)

    private long commentCount;  // 댓글 수
    private long readCount;     // 조회 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 후기를 작성한 멤버

    // 조회 수 증가
    public AlbaReview incrementReadCount() {
        return this.toBuilder()
                .readCount(this.readCount + 1)
                .build();
    }
}
