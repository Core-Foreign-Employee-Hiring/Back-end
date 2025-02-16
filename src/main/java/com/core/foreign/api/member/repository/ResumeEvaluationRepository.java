package com.core.foreign.api.member.repository;

import com.core.foreign.api.member.entity.EvaluationType;
import com.core.foreign.api.member.entity.ResumeEvaluation;
import com.core.foreign.api.recruit.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResumeEvaluationRepository extends JpaRepository<ResumeEvaluation, Long> {

    @Query("select re from ResumeEvaluation re" +
            " join fetch re.evaluation e" +
            " where re.resume.id=:resumeId and e.type=:type")
    List<ResumeEvaluation> findByResumeIdAndType(@Param("resumeId")Long resumeId, @Param("type") EvaluationType type);
}
