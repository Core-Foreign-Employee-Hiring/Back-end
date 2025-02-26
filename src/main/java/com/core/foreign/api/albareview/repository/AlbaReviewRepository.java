package com.core.foreign.api.albareview.repository;

import com.core.foreign.api.albareview.entity.AlbaReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 알바 후기 검색, 연관도 높은 순으로 출력 단 같은 연관도 일경우 최신순으로 정렬
    @Query("SELECT a FROM AlbaReview a " +
            "WHERE a.title LIKE CONCAT('%', :keyword, '%') OR a.content LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY " +
            " (CASE WHEN a.title LIKE CONCAT('%', :keyword, '%') THEN 2 ELSE 0 END + " +
            "  CASE WHEN a.content LIKE CONCAT('%', :keyword, '%') THEN 1 ELSE 0 END) DESC, " +
            " a.createdAt DESC")
    Page<AlbaReview> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

}
