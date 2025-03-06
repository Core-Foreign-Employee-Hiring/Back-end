package com.core.foreign.api.recruit.service;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.RecruitType;
import com.core.foreign.api.recruit.entity.RecruitPublishStatus;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;

public class RecruitSpecifications {

    // 공고 상태가 PUBLISHED 인지 필터
    public static Specification<Recruit> isPublished() {
        return (root, query, cb) ->
                cb.equal(root.get("recruitPublishStatus"), RecruitPublishStatus.PUBLISHED);
    }

    // 업직종 필터
    public static Specification<Recruit> businessFieldsIn(List<BusinessField> fields) {
        return (root, query, cb) -> {
            if (fields == null || fields.isEmpty()) {
                return cb.conjunction();
            }
            // businessFields 컬렉션의 타입을 명시적으로 지정
            Expression<Collection<BusinessField>> businessFields = root.get("businessFields");
            Predicate predicate = cb.disjunction();
            for (BusinessField field : fields) {
                predicate = cb.or(predicate, cb.isMember(field, businessFields));
            }
            return predicate;
        };
    }

    // 근무기간 필터
    public static Specification<Recruit> workDurationIn(List<String> durations) {
        return (root, query, cb) -> {
            if (durations == null || durations.isEmpty()) {
                return cb.conjunction();
            }
            Expression<Collection<String>> workDurations = root.get("workDuration");
            Predicate predicate = cb.disjunction();
            for (String duration : durations) {
                predicate = cb.or(predicate, cb.isMember(duration, workDurations));
            }
            return predicate;
        };
    }

    // 근무요일 필터
    public static Specification<Recruit> workDaysIn(List<String> days) {
        return (root, query, cb) -> {
            if (days == null || days.isEmpty()) {
                return cb.conjunction();
            }
            Expression<Collection<String>> workDays = root.get("workDays");
            Predicate predicate = cb.disjunction();
            for (String day : days) {
                predicate = cb.or(predicate, cb.isMember(day, workDays));
            }
            return predicate;
        };
    }

    // 근무시간 필터
    public static Specification<Recruit> workTimeIn(List<String> times) {
        return (root, query, cb) -> {
            if (times == null || times.isEmpty()) {
                return cb.conjunction();
            }
            Expression<Collection<String>> workTimes = root.get("workTime");
            Predicate predicate = cb.disjunction();
            for (String time : times) {
                predicate = cb.or(predicate, cb.isMember(time, workTimes));
            }
            return predicate;
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

    // 프리미엄 필터
    public static Specification<Recruit> isPremium() {
        return (root, query, cb) ->
                cb.equal(root.get("recruitType"), RecruitType.PREMIUM);
    }
}
