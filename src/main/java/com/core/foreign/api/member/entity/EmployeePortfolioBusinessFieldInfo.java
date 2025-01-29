package com.core.foreign.api.member.entity;

import com.core.foreign.api.business_field.BusinessField;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class EmployeePortfolioBusinessFieldInfo {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(STRING)
    private BusinessField businessField;
    private String content;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(fetch = LAZY)
    private EmployeePortfolio employeePortfolio;

    @Enumerated(STRING)
    private EmployeePortfolioBusinessFieldType employeePortfolioBusinessFieldType;
}
