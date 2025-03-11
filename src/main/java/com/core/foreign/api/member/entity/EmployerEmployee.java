package com.core.foreign.api.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor
public class EmployerEmployee {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name="employer_employee_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public EmployerEmployee(Employer employer, Employee employee) {
        this.employer = employer;
        this.employee = employee;
    }
}
