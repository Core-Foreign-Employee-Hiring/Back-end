package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.recruit.entity.*;
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
        extends JpaRepository<Recruit, Long>, JpaSpecificationExecutor<Recruit>, RecruitRepositoryQueryDSL {

    Optional<Recruit> findAllByEmployerAndRecruitPublishStatus(Member employer, RecruitPublishStatus status);

    @Override
    @EntityGraph(attributePaths = {
            "businessFields",       // @ElementCollection
            "preferredConditions",  // @ElementCollection
            "applicationMethods",
            "workDuration",       // @ElementCollection
            "workTime",  // @ElementCollection
            "workDays",   // @ElementCollection
            "employer"             // @ManyToOne(LAZY)
    })
    Page<Recruit> findAll(Specification<Recruit> spec, Pageable pageable);

    @Query("select r from Recruit r " +
            "left join fetch r.preferredConditions " +
            "left join fetch r.businessFields " +
            "left join fetch r.applicationMethods " +
            "left join fetch r.workDuration " +
            "left join fetch r.workTime " +
            "left join fetch r.workDays " +
            "join fetch r.employer " +
            "where r.id = :recruitId")
    Optional<Recruit> findByIdFetchJoin(@Param("recruitId") Long recruitId);

    @Query("select r from Recruit r" +
            " where r.employer.id=:employerId and r.recruitPublishStatus='PUBLISHED'")
    Page<Recruit> findPublishedRecruitsByEmployerId(Long employerId, Pageable pageable);

    // 해당 공고 유형(recruitType)이고 jumpDate가 설정된 공고를 jumpDate 내림차순으로 조회
    @Query("SELECT r FROM Recruit r WHERE r.recruitType = :recruitType AND r.jumpDate IS NOT NULL ORDER BY r.jumpDate DESC")
    Page<Recruit> findByRecruitTypeAndJumpDateIsNotNullOrderByJumpDateDesc(@Param("recruitType") RecruitType recruitType, Pageable pageable);

    @Query("select gr from GeneralRecruit gr where gr.id = :id and gr.recruitPublishStatus = :status")
    Optional<GeneralRecruit> findGeneralDraftById(Long id, RecruitPublishStatus status);

    default Optional<GeneralRecruit> findGeneralDraftById(Long id) {
        return findGeneralDraftById(id, RecruitPublishStatus.DRAFT);
    }

    @Query("select pr from PremiumRecruit pr where pr.id = :id and pr.recruitPublishStatus = :status")
    Optional<PremiumRecruit> findPremiumDraftById(Long id, RecruitPublishStatus status);

    default Optional<PremiumRecruit> findPremiumDraftById(Long id) {
        return findPremiumDraftById(id, RecruitPublishStatus.DRAFT);
    }

    boolean existsByEmployerAndRecruitPublishStatus(Member employer, RecruitPublishStatus status);

}
