package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.RecruitPublishStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RecruitRepository
        extends JpaRepository<Recruit, Long>, JpaSpecificationExecutor<Recruit> {

    Optional<Recruit> findAllByEmployerAndRecruitPublishStatus(Member employer, RecruitPublishStatus status);

    @Override
    @EntityGraph(attributePaths = {
            "businessFields",       // @ElementCollection
            "preferredConditions",  // @ElementCollection
            "applicationMethods",   // @ElementCollection
            "employer"             // @ManyToOne(LAZY)
    })
    Page<Recruit> findAll(Specification<Recruit> spec, Pageable pageable);

}
