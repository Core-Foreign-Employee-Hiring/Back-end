package com.core.foreign.api.member.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ResumeEvaluationId {
    private Long resumeId;
    private Long evaluationId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResumeEvaluationId that = (ResumeEvaluationId) o;
        return Objects.equals(getResumeId(), that.getResumeId()) && Objects.equals(getEvaluationId(), that.getEvaluationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getResumeId(), getEvaluationId());
    }
}
