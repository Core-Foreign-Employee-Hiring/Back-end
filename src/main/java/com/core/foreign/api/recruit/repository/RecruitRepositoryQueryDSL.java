package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.RecruitPublishStatus;
import com.core.foreign.api.recruit.entity.RecruitType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruitRepositoryQueryDSL {
    Page<Recruit> getMyRecruits(Long employerId, RecruitType recruitType, RecruitPublishStatus recruitPublishStatus,
                                boolean excludeExpired , Pageable pageable);

    Page<Recruit> searchRecruit(String searchQuery, Pageable pageable);
}
