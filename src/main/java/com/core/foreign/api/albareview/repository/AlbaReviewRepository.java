package com.core.foreign.api.albareview.repository;

import com.core.foreign.api.albareview.entity.AlbaReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbaReviewRepository extends JpaRepository<AlbaReview, Long> {

    // 조회 수 증가
    @Modifying
    @Query("update AlbaReview a set a.readCount = a.readCount + 1 where a.id = :reviewId")
    void incrementReadCount(@Param("reviewId") Long reviewId);

    // 댓글 수 증가
    @Modifying
    @Query("update AlbaReview a set a.commentCount = a.commentCount + 1 where a.id = :reviewId")
    void incrementCommentCount(@Param("reviewId") Long reviewId);

    // 댓글 수 감소
    @Modifying
    @Query("update AlbaReview a set a.commentCount = case when a.commentCount > 0 then a.commentCount - 1 else 0 end where a.id = :reviewId")
    void decrementCommentCount(@Param("reviewId") Long reviewId);

}
