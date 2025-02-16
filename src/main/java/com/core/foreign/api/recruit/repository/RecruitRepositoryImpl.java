package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.recruit.entity.QRecruit;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.RecruitPublishStatus;
import com.core.foreign.api.recruit.entity.RecruitType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.util.List;

import static com.core.foreign.api.recruit.entity.QRecruit.recruit;

public class RecruitRepositoryImpl implements RecruitRepositoryQueryDSL{
    private final JPAQueryFactory queryFactory;

    public RecruitRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Recruit> getMyRecruits(Long employerId, RecruitType recruitType, RecruitPublishStatus recruitPublishStatus, boolean excludeExpired, Pageable pageable) {
        List<Recruit> content = queryFactory
                .selectFrom(recruit)
                .innerJoin(recruit.employer).fetchJoin()
                .where(
                        recruit.employer.id.eq(employerId),
                        recruit.recruitPublishStatus.eq(recruitPublishStatus),
                        excludeExpiredEq(excludeExpired),
                        recruitTypeEq(recruitType)
                )
                .orderBy(recruit.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(recruit.count())
                .from(recruit)
                .where(
                        recruit.employer.id.eq(employerId),
                        recruit.recruitPublishStatus.eq(recruitPublishStatus),
                        excludeExpiredEq(excludeExpired),
                        recruitTypeEq(recruitType)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression excludeExpiredEq(boolean excludeExpired) {
        return excludeExpired ? recruit.recruitEndDate.after(LocalDate.now().minusDays(1)) : null;
    }

    private BooleanExpression recruitTypeEq(RecruitType recruitType) {
        return recruitType==null?null:recruit.recruitType.eq(recruitType);
    }



}
