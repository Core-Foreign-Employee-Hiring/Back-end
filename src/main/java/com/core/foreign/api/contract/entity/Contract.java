package com.core.foreign.api.contract.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "contract_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Contract {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name="contract_id")
    private Long id;


}
