package com.core.foreign.api.business_field.entity;

import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.member.entity.Evaluation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class EmployerBusinessField {

    @EmbeddedId
    private EmployerBusinessFieldId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employerId")
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @ManyToOne(fetch=FetchType.LAZY)
    @MapsId("businessFieldEntityId")
    @JoinColumn(name = "business_field_entity_id")
    private BusinessFieldEntity businessFieldEntity;


    public EmployerBusinessField(Employer employer, BusinessFieldEntity businessFieldEntity) {
        this.id=new EmployerBusinessFieldId(employer.getId(), businessFieldEntity.getId());
        this.employer = employer;
        this.businessFieldEntity = businessFieldEntity;
    }
}
