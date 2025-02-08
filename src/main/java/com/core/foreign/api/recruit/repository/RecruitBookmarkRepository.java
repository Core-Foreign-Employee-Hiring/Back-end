package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.recruit.entity.RecruitBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RecruitBookmarkRepository extends JpaRepository<RecruitBookmark, Long> {
    @Query("select b from RecruitBookmark b" +
            " where b.recruit.id=:recruitId and b.member.id=:memberId")
    Optional<RecruitBookmark> findByRecruitIdAndMemberId(@Param("recruitId")Long recruitId, @Param("memberId")Long memberId);



    @Query("select b from RecruitBookmark  b" +
            " join fetch b.recruit r" +
            " join fetch r.employer" +
            " where b.member.id=:memberId")
    Page<RecruitBookmark> findByMemberId(@Param("memberId")Long memberId, Pageable pageable);
}
