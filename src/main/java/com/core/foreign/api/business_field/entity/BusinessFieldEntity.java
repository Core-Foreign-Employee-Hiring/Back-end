package com.core.foreign.api.business_field.entity;


import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.business_field.BusinessFieldTarget;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class BusinessFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(STRING)
    private BusinessField businessField;


    @Enumerated(STRING)
    @Column(name="target")
    private BusinessFieldTarget target;

    @Column(name="target_id")
    private Long targetId;
}
