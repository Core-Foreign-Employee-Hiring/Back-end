package com.core.foreign.api.albareview.repository;

import com.core.foreign.api.albareview.entity.AlbaReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbaReviewRepository extends JpaRepository<AlbaReview, Long> {

    @Modifying
    @Query("update AlbaReview a set a.readCount = a.readCount + 1 where a.id = :reviewId")
    int incrementReadCount(@Param("reviewId") Long reviewId);
}
