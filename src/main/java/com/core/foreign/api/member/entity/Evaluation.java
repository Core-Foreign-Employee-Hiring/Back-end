package com.core.foreign.api.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor
public class Evaluation {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String category;  // 평가 내용 (문자열)

    @Enumerated(STRING)
    private EvaluationType type;  // 고용인 -> 피고용인인지, 반대인지

    public Evaluation(String category, EvaluationType type) {
        this.category = category;
        this.type = type;
    }
}
