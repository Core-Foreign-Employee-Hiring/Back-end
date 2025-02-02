package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.RecruitPublishStatus;
import com.core.foreign.api.recruit.entity.RecruitType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("select r from Recruit r " +
            "left join fetch r.preferredConditions " +
            "left join fetch r.businessFields " +
            "left join fetch r.applicationMethods " +
            "join fetch r.employer " +
            "where r.id = :recruitId")
    Optional<Recruit> findByIdFetchJoin(@Param("recruitId") Long recruitId);



    @Query("select r from Recruit r" +
            " where r.employer.id=:employerId and r.recruitType=:recruitType")
    Page<Recruit> findAll(@Param("employerId")Long employerId, @Param("recruitType")RecruitType recruitType, Pageable pageable);
}
