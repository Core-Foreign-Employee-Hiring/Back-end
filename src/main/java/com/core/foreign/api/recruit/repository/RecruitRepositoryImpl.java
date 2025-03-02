package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.member.entity.QEmployer;
import com.core.foreign.api.recruit.entity.QRecruit;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.RecruitPublishStatus;
import com.core.foreign.api.recruit.entity.RecruitType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
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

    @Override
    public Page<Recruit> searchRecruit(String searchQuery, Pageable pageable) {

        QRecruit qRecruit = QRecruit.recruit;
        QEmployer qEmployer = QEmployer.employer;

        // 검색어 전처리: 좌우 공백 제거 후 소문자로 변환
        String lowerQuery = searchQuery.trim().toLowerCase();

        // 검색 조건:
        // 1. 공고 제목이 검색어를 포함하거나
        // 2. treat()를 사용하여 employer를 QEmployer로 변환한 후 회사점포명(companyName)이 검색어를 포함하는 경우
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(
                qRecruit.title.toLowerCase().contains(lowerQuery)
                        .or(
                                JPAExpressions.treat(qRecruit.employer, QEmployer.class)
                                        .companyName.toLowerCase().contains(lowerQuery)
                        )
        );

        // 연관도 점수: 제목이 완전 일치하면 1, 제목이 검색어로 시작하면 2, 제목에 포함되면 3, 그 외에는 4
        NumberExpression<Integer> relevance = new CaseBuilder()
                .when(qRecruit.title.equalsIgnoreCase(searchQuery)).then(1)
                .when(qRecruit.title.startsWithIgnoreCase(searchQuery)).then(2)
                .when(qRecruit.title.containsIgnoreCase(searchQuery)).then(3)
                .otherwise(4);

        // 정렬: 연관도 오름차순 정렬, 동일하면 createdAt 내림차순(최신순) 정렬
        List<Recruit> content = queryFactory
                .selectFrom(qRecruit)
                .where(predicate)
                .orderBy(relevance.asc(), qRecruit.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(qRecruit.count())
                .from(qRecruit)
                .where(predicate);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

}
