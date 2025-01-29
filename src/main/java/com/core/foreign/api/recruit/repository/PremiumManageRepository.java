package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.recruit.entity.PremiumManage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PremiumManageRepository extends JpaRepository<PremiumManage, Long> {
    Optional<PremiumManage> findByEmployerId(Long employerId);
}
