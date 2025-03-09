package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.recruit.entity.RecruitmentStatus;
import com.core.foreign.api.recruit.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResumeRepositoryQueryDSL {
    Page<Resume> searchResumeByRecruitId(Long recruitId,
                                         String keyword, RecruitmentStatus recruitmentStatus, ContractStatus contractStatus,
                                         Pageable pageable);

    Page<Resume> getApplicationPortfolio(List<BusinessField> businessField, Pageable pageable);
}
