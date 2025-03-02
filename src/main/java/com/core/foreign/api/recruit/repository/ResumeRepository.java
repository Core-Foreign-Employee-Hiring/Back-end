package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.recruit.entity.EvaluationStatus;
import com.core.foreign.api.recruit.entity.RecruitWithResumeCountDTO;
import com.core.foreign.api.recruit.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Long>, ResumeRepositoryQueryDSL {

    @Query(nativeQuery = true,
            value = "select recruit_id as recruitId, count(*) as resumeCount from resume" +
                    " where recruit_id in :recruitIds and is_deleted=false" +
                    " group by recruit_id")
    List<RecruitWithResumeCountDTO> findRecruitWithResumeCount(@Param("recruitIds") List<Long> recruitIds);

    @Query("select r from Resume r" +
            " join fetch r.employee" +
            " join fetch r.recruit" +
            " where r.id=:resumeId and r.isDeleted=false")
    Optional<Resume>findResumeWithEmployeeAndRecruit(@Param("resumeId")Long resumeId);

    @Query("select r from Resume r" +
            " join fetch r.employee employee" +
            " join fetch r.recruit" +
            " where r.id=:resumeId and r.isDeleted=false and r.isPublic=true and employee.isPortfolioPublic=true")
    Optional<Resume>findResumeWithEmployeeAndRecruitForPortfolio(@Param("resumeId")Long resumeId);

    @Query("select r from Resume r" +
            " join fetch r.employee" +
            " join fetch r.recruit" +
            " where r.id=:resumeId")
    Optional<Resume>findResumeWithEmployeeAndRecruitIncludingDeleted(@Param("resumeId")Long resumeId);

    @Query("select r from Resume r" +
            " join fetch r.recruit recruit" +
            " join fetch recruit.employer employer" +
            " where r.id = :resumeId and r.isDeleted = false")
    Optional<Resume> findByResumeIdWithRecruitAndEmployer(@Param("resumeId") Long resumeId);

    @Query("select r from Resume r" +
            " where r.recruit.id=:recruitId and r.employee.id=:employeeId and r.isDeleted=false")
    Optional<Resume> findByEmployeeIdAndRecruitId(@Param("employeeId")Long employeeId, @Param("recruitId")Long recruitId);

    @Query("select r from Resume r" +
            " join fetch r.employee" +
            " join fetch r.recruit re" +
            " join fetch re.employer" +
            " where r.recruit.id=:recruitId and r.employee.id=:employeeId")
    Optional<Resume> findByEmployeeIdAndRecruitIdIncludingDeleted(@Param("employeeId") Long employeeId, @Param("recruitId") Long recruitId);



    @Query("select r from Resume r" +
            " join fetch r.recruit re" +
            " join fetch re.employer" +
            " where r.employee.id=:employeeId and r.isDeleted=false")
    Page<Resume> findResumeByEmployeeId(@Param("employeeId")Long employeeId, Pageable pageable);


    @Query("select r from Resume r" +
            " join fetch r.recruit" +
            " where r.recruitmentStatus='APPROVED' and r.isEmployerEvaluatedByEmployee= :evaluationStatus")
    Page<Resume> findResumeByEmployeeIdAndEvaluationStatus(@Param("employeeId")Long employeeId,  @Param("evaluationStatus") EvaluationStatus evaluationStatus, Pageable pageable);

    @Modifying
    @Query("update Resume resume set resume.viewCount=resume.viewCount+1 where resume.id=:resumeId")
    void increaseViewCount(@Param("resumeId") Long resumeId);

}
