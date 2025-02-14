package com.core.foreign.api.member.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEvaluationId implements Serializable {
    private Long employeeId;
    private Long evaluationId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeEvaluationId that = (EmployeeEvaluationId) o;
        return Objects.equals(getEmployeeId(), that.getEmployeeId()) && Objects.equals(getEvaluationId(), that.getEvaluationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmployeeId(), getEvaluationId());
    }
}
