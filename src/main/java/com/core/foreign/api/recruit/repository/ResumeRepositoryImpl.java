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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.core.foreign.api.recruit.entity.QResume.resume;

public class ResumeRepositoryImpl implements ResumeRepositoryQueryDSL{
    private final JPAQueryFactory queryFactory;

    public ResumeRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Resume> searchResumedByRecruitId(Long recruitId,
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
    public Page<Resume> getApplicationPortfolio(BusinessField businessField, Pageable pageable) {
        List<Resume> content = queryFactory
                .select(resume)
                .from(resume)
                .innerJoin(resume.employee).fetchJoin()
                .innerJoin(resume.recruit).fetchJoin()
                .where(
                        isPublic(),
                        businessFieldEq(businessField)
                )
                .orderBy(resume.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
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


    private BooleanExpression businessFieldEq(BusinessField businessField){
        return null;
    }
    private BooleanExpression isPublic(){
        return resume.isPublic.eq(true).and(resume.employee.isPortfolioPublic.eq(true));
    }


}
