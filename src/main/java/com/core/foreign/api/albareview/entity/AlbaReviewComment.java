package com.core.foreign.api.albareview.entity;

import com.core.foreign.api.member.entity.Member;
import com.core.foreign.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "albareview_comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class AlbaReviewComment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 댓글을 작성한 멤버

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alba_review_id", nullable = false)
    private AlbaReview albaReview; // 알바 후기

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", unique = true)
    private AlbaReviewComment parentComment; // 부모댓글

}
