package com.core.foreign.api.albareview.repository;

import com.core.foreign.api.albareview.entity.AlbaReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbaReviewRepository extends JpaRepository<AlbaReview, Long> {
}
