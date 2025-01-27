package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.recruit.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
