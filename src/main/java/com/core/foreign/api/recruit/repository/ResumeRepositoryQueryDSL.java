package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.recruit.entity.ContractStatus;
import com.core.foreign.api.recruit.entity.RecruitmentStatus;
import com.core.foreign.api.recruit.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResumeRepositoryQueryDSL {
    Page<Resume> searchResumedByRecruitId(Long recruitId,
                                          String keyword, RecruitmentStatus recruitmentStatus, ContractStatus contractStatus,
                                          Pageable pageable);

    Page<Resume> getApplicationPortfolio(BusinessField businessField, Pageable pageable);
}
