package com.core.foreign.api.recruit.service;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.RecruitPublishStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

// 공고에 대한 동적 검색
public class RecruitSpecifications {

    // 공고 상태가 PUBLISHED 인지 필터
    public static Specification<Recruit> isPublished() {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            return cb.equal(root.get("recruitPublishStatus"), RecruitPublishStatus.PUBLISHED);
        };
    }

    // 업직종 필터
    public static Specification<Recruit> businessFieldsIn(List<BusinessField> fields) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            if (fields == null || fields.isEmpty()) {
                return cb.conjunction();
            }
            Join<Recruit, BusinessField> join = root.join("businessFields", JoinType.LEFT);
            return join.in(fields);
        };
    }

    // 근무기간 필터
    public static Specification<Recruit> workDurationIn(List<String> durations) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            if (durations == null || durations.isEmpty()) {
                return cb.conjunction();
            }
            Join<Recruit, String> join = root.join("workDurations", JoinType.LEFT);
            return join.in(durations);
        };
    }

    // 근무요일 필터
    public static Specification<Recruit> workDaysIn(List<String> days) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            if (days == null || days.isEmpty()) {
                return cb.conjunction();
            }
            Join<Recruit, String> join = root.join("workDays", JoinType.LEFT);
            return join.in(days);
        };
    }

    // 근무시간 필터
    public static Specification<Recruit> workTimeIn(List<String> times) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            if (times == null || times.isEmpty()) {
                return cb.conjunction();
            }
            Join<Recruit, String> join = root.join("workTimes", JoinType.LEFT);
            return join.in(times);
        };
    }

    // 성별 필터
    public static Specification<Recruit> genderEq(String gender) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            if (gender == null || gender.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("gender"), gender);
        };
    }

    // 급여형태 필터
    public static Specification<Recruit> salaryTypeEq(String salaryType) {
        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }
            if (salaryType == null || salaryType.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("salaryType"), salaryType);
        };
    }
}
