package com.core.foreign.api.albareview.repository;

import com.core.foreign.api.albareview.entity.AlbaReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbaReviewCommentRepository extends JpaRepository<AlbaReviewComment, Long> {
    Optional<AlbaReviewComment> findByParentComment_Id(Long parentCommentId);
    List<AlbaReviewComment> findByAlbaReview_IdOrderByCreatedAtDesc(Long reviewId);
}
