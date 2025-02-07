package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.recruit.entity.RecruitWithResumeCountDTO;
import com.core.foreign.api.recruit.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long>, ResumeRepositoryQueryDSL {

    @Query(nativeQuery = true,
            value = "select recruit_id as recruitId, count(*) as resumeCount from Resume" +
                    " where recruit_id in :recruitIds" +
                    " group by recruit_id")
    List<RecruitWithResumeCountDTO> findRecruitWithResumeCount(@Param("recruitIds") List<Long> recruitIds);

    @Query("select r from Resume r" +
            " join fetch r.employee" +
            " join fetch r.recruit" +
            " where r.id=:resumeId")
    Optional<Resume>findResumeWithEmployeeAndRecruit(@Param("resumeId")Long resumeId);

}
