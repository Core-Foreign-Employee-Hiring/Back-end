package com.core.foreign.api.recruit.service;

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
        return (root, query, cb) ->
                cb.equal(root.get("recruitPublishStatus"), RecruitPublishStatus.PUBLISHED);
    }

    // 업직종 필터
    public static Specification<Recruit> businessFieldsIn(List<String> fields) {
        return (root, query, cb) -> {
            if (fields == null || fields.isEmpty()) {
                return cb.conjunction(); // 필터가 없으면 무조건 true
            }
            Join<Recruit, String> join = root.join("businessFields", JoinType.LEFT);
            return join.in(fields);
        };
    }

    // 근무기간 필터
    public static Specification<Recruit> workDurationIn(List<String> durations) {
        return (root, query, cb) -> {
            if (durations == null || durations.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("workDuration").in(durations);
        };
    }

    // 근무요일 필터
    public static Specification<Recruit> workDaysIn(List<String> days) {
        return (root, query, cb) -> {
            if (days == null || days.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("workDays").in(days);
        };
    }

    // 근무시간 필터
    public static Specification<Recruit> workTimeIn(List<String> times) {
        return (root, query, cb) -> {
            if (times == null || times.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("workTime").in(times);
        };
    }

    // 성별 필터
    public static Specification<Recruit> genderEq(String gender) {
        return (root, query, cb) -> {
            if (gender == null || gender.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("gender"), gender);
        };
    }

    // 급여형태 필터
    public static Specification<Recruit> salaryTypeEq(String salaryType) {
        return (root, query, cb) -> {
            if (salaryType == null || salaryType.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("salaryType"), salaryType);
        };
    }
}
