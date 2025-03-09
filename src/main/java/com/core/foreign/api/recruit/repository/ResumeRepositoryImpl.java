package com.core.foreign.api.recruit.repository;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.recruit.entity.RecruitmentStatus;
import com.core.foreign.api.recruit.entity.Resume;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.Collections;
import java.util.List;

import static com.core.foreign.api.recruit.entity.QRecruit.recruit;
import static com.core.foreign.api.recruit.entity.QResume.resume;

@Slf4j
public class ResumeRepositoryImpl implements ResumeRepositoryQueryDSL{
    private final JPAQueryFactory queryFactory;

    public ResumeRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Resume> searchResumeByRecruitId(Long recruitId,
                                                String keyword, RecruitmentStatus recruitmentStatus, ContractStatus contractStatus,
                                                Pageable pageable) {

        if(keyword==null){
            keyword="";
        }

        List<Resume> content = queryFactory.select(resume)
                .from(resume)
                .innerJoin(resume.employee).fetchJoin()
                .where(
                        resume.isDeleted.eq(false),
                        resume.recruit.id.eq(recruitId),
                        (resume.employee.name.lower().contains(keyword)).or(resume.employee.phoneNumber.contains(keyword)),
                        statusEq(recruitmentStatus, contractStatus)


                )
                .orderBy(resume.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(resume.count())
                .from(resume)
                .where(
                        resume.isDeleted.eq(false),
                        resume.recruit.id.eq(recruitId),
                        (resume.employee.name.lower().contains(keyword)).or(resume.employee.phoneNumber.contains(keyword)),
                        statusEq(recruitmentStatus, contractStatus)


                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

    }


    @Override
    public Page<Resume> getApplicationPortfolio(List<BusinessField> businessField, Pageable pageable) {
        log.info("[getApplicationPortfolio][resumeId 조회]");
        //  특정 BusinessField와 연관된 Resume ID만 DISTINCT로 조회
        List<Long> resumeIds = queryFactory
                .selectDistinct(resume.id)
                .from(resume)
                .innerJoin(resume.recruit, recruit)
                .innerJoin(recruit.businessFields)
                .where(
                        isPublic(),
                        businessFieldEq(businessField), // 특정 비즈니스 필드에 대한 필터링
                        resume.isDeleted.eq(false)
                )
                .orderBy(resume.id.desc()) // 최신순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (resumeIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        log.info("[getApplicationPortfolio][resume 조회]");
        // ID를 이용해 Resume 엔티티들을 조회 (연관 데이터도 fetch join)
        List<Resume> content = queryFactory
                .selectFrom(resume)
                .innerJoin(resume.employee).fetchJoin()
                .innerJoin(resume.recruit).fetchJoin()
                .innerJoin(resume.recruit.businessFields).fetchJoin()
                .where(resume.id.in(resumeIds))
                .orderBy(resume.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(resume.count())
                .from(resume)
                .where(
                        isPublic(),
                        businessFieldEq(businessField)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanBuilder statusEq(RecruitmentStatus recruitmentStatus, ContractStatus contractStatus) {
        BooleanBuilder builder = new BooleanBuilder();

        if(recruitmentStatus!=null){
            builder.and(resume.recruitmentStatus.eq(recruitmentStatus));
            if(recruitmentStatus.equals(RecruitmentStatus.APPROVED)){
                builder.and(contractStatusEq(contractStatus));
            }
        }

        return builder;
    }

    private BooleanExpression contractStatusEq(ContractStatus contractStatus){
        return contractStatus==null?null:resume.contractStatus.eq(contractStatus);
    }


    private BooleanExpression businessFieldEq(List<BusinessField> businessField){
        return (businessField==null||businessField.isEmpty()) ? null : resume.recruit.businessFields.any().in(businessField);
    }
    private BooleanExpression isPublic(){
        return resume.isPublic.eq(true).and(resume.employee.isPortfolioPublic.eq(true));
    }


}
