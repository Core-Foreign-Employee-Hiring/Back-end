package com.core.foreign.api.member.entity;

import com.core.foreign.api.recruit.entity.Resume;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor
public class EmployerResume {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "employer_resume_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    public EmployerResume(Employer employer, Resume resume) {
        this.employer = employer;
        this.resume = resume;
    }
}
