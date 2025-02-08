package com.core.foreign.api.recruit.entity;

import com.core.foreign.api.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@NoArgsConstructor
@Getter
public class RecruitBookmark {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    private Recruit recruit;

    public RecruitBookmark(Recruit recruit, Member member) {
        this.recruit = recruit;
        this.member = member;
    }

    @ManyToOne
    private Member member;
}
