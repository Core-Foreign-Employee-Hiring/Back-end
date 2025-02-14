package com.core.foreign.api.member.entity;

import com.core.foreign.api.recruit.entity.Resume;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class ResumeEvaluation {
    @EmbeddedId
    private ResumeEvaluationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("resumeId")
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @ManyToOne(fetch=FetchType.LAZY)
    @MapsId("evaluationId") // EmployerEvaluationId의 evaluationId와 매핑
    @JoinColumn(name = "evaluation_id")
    private Evaluation evaluation;  // 평가.


    public ResumeEvaluation(Resume resume, Evaluation evaluation) {
        this.id = new ResumeEvaluationId(resume.getId(), evaluation.getId());
        this.resume = resume;
        this.evaluation = evaluation;
    }
}
