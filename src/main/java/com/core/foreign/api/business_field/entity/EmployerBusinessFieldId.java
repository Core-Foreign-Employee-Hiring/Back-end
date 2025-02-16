package com.core.foreign.api.business_field.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class EmployerBusinessFieldId {
    private Long employerId;
    private Long businessFieldEntityId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployerBusinessFieldId that = (EmployerBusinessFieldId) o;
        return Objects.equals(getEmployerId(), that.getEmployerId()) && Objects.equals(getBusinessFieldEntityId(), that.getBusinessFieldEntityId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmployerId(), getBusinessFieldEntityId());
    }
}
