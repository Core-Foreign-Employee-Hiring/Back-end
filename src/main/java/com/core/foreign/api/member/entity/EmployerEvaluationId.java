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
public class EmployerEvaluationId{
    private Long employerId;
    private Long evaluationId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployerEvaluationId that = (EmployerEvaluationId) o;
        return Objects.equals(getEmployerId(), that.getEmployerId()) && Objects.equals(getEvaluationId(), that.getEvaluationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmployerId(), getEvaluationId());
    }
}
