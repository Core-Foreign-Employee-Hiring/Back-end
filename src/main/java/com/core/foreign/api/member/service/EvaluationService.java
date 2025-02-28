package com.core.foreign.api.member.service;

import com.core.foreign.api.member.entity.*;
import com.core.foreign.api.member.repository.*;
import com.core.foreign.api.recruit.entity.EvaluationStatus;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.RecruitmentStatus;
import com.core.foreign.api.recruit.entity.Resume;
import com.core.foreign.api.recruit.repository.ResumeRepository;
import com.core.foreign.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static com.core.foreign.common.response.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final ResumeRepository resumeRepository;
    private final EmployeeEvaluationRepository employeeEvaluationRepository;
    private final EmployerEvaluationRepository employerEvaluationRepository;
    private final ResumeEvaluationRepository resumeEvaluationRepository;
    private final MemberRepository memberRepository;


    /**
     *  고용인이 피고용인을 평가.<p>
     *   온라인 지원 내용 확인에서 넘어온다.
     */
    @Transactional
    public void evaluateEmployee(Long resumeId, List<EvaluationCategory> evaluationCategory){
        Resume resume = resumeRepository.findResumeWithEmployeeAndRecruitIncludingDeleted(resumeId)
                .orElseThrow(() -> {
                    log.warn("[evaluateEmployee][이력서가 없어 평가할 수 없음].[resumeId={}]", resumeId);
                    return new BadRequestException(EVALUATION_NOT_ALLOWED_FOR_USER_EXCEPTION.getMessage());
                });

        validateApprovalStatus(resume);

        if(resume.getIsEmployeeEvaluatedByEmployer()== EvaluationStatus.COMPLETED){
            log.warn("[evaluateEmployee][고용인이 피고용인을 이미 평가했음.][resumeId= {}]", resume.getId());
            throw new BadRequestException(EVALUATION_ALREADY_COMPLETED_EXCEPTION.getMessage());
        }


        Employee employee = resume.getEmployee();
        Set<EvaluationCategory> uniqueEvaluationCategories = new HashSet<>(evaluationCategory);

        validateEvaluationType(uniqueEvaluationCategories, EvaluationType.EMPLOYER_TO_EMPLOYEE);

        List<Evaluation> byType = evaluationRepository.findByType(EvaluationType.EMPLOYER_TO_EMPLOYEE);
        Map<String, Long> map=new HashMap<>();
        for (Evaluation evaluation : byType) {
            map.put(evaluation.getCategory(), evaluation.getId());
        }

        List<ResumeEvaluation> resumeEvaluations=new ArrayList<>();
        for (EvaluationCategory uniqueEvaluationCategory : uniqueEvaluationCategories) {
            Long evaluationId = map.get(uniqueEvaluationCategory.getDescription());
            employeeEvaluationRepository.evaluateEmployee(employee.getId(), evaluationId);

            Evaluation evaluation = evaluationRepository.findById(evaluationId).get();

            ResumeEvaluation resumeEvaluation = new ResumeEvaluation(resume, evaluation);

            resumeEvaluations.add(resumeEvaluation);
        }


        resumeEvaluationRepository.saveAll(resumeEvaluations);

        memberRepository.increaseEvaluationJoinCount(employee.getId());
        resume.evaluateEmployee();
    }


    /**
     *     피고용인이 고용인을 평가.<P>
     *    채용정보 상세보기에서 넘어온다.
     */
    @Transactional
    public void evaluateEmployer(Long employeeId, Long recruitId, List<EvaluationCategory> evaluationCategory){
        Resume resume = resumeRepository.findByEmployeeIdAndRecruitIdIncludingDeleted(employeeId, recruitId)
                .orElseThrow(() -> {
                    log.warn("[evaluateEmployer][이력서가 없어 평가할 수 없음.][employeeId={}, recruitId={}]", employeeId, recruitId);
                    return new BadRequestException(EVALUATION_NOT_ALLOWED_FOR_USER_EXCEPTION.getMessage());
                });


        validateApprovalStatus(resume);

        if(resume.getIsEmployerEvaluatedByEmployee()==EvaluationStatus.COMPLETED){
            log.warn("[evaluateEmployer][피고용인이 고용인을 이미 평가했음].[resumeId= {}]", resume.getId());
            throw new BadRequestException(EVALUATION_ALREADY_COMPLETED_EXCEPTION.getMessage());
        }

        Set<EvaluationCategory> uniqueEvaluationCategories = new HashSet<>(evaluationCategory);

        validateEvaluationType(uniqueEvaluationCategories, EvaluationType.EMPLOYEE_TO_EMPLOYER);

        List<Evaluation> byType = evaluationRepository.findByType(EvaluationType.EMPLOYEE_TO_EMPLOYER);

        Map<String, Long> map=new HashMap<>();
        for (Evaluation evaluation : byType) {
            map.put(evaluation.getCategory(), evaluation.getId());
        }

        Recruit recruit = resume.getRecruit();
        Employer employer = (Employer) recruit.getEmployer();

        List<ResumeEvaluation> resumeEvaluations=new ArrayList<>();
        for (EvaluationCategory uniqueEvaluationCategory : uniqueEvaluationCategories) {
            Long evaluationId = map.get(uniqueEvaluationCategory.getDescription());
            employerEvaluationRepository.evaluateEmployer(employer.getId(), evaluationId);

            Evaluation evaluation = evaluationRepository.findById(evaluationId).get();

            ResumeEvaluation resumeEvaluation = new ResumeEvaluation(resume, evaluation);

            resumeEvaluations.add(resumeEvaluation);
        }


        resumeEvaluationRepository.saveAll(resumeEvaluations);

        memberRepository.increaseEvaluationJoinCount(employer.getId());
        resume.evaluateEmployer();
    }


    private void validateApprovalStatus(Resume resume) {
        // 모집 승인 상태가 아니거나, 계약서 완료부터 30일이 지나지 않았으면
        if (resume.getRecruitmentStatus() != RecruitmentStatus.APPROVED
                || resume.getContractCompletionDate().plusMonths(1).isAfter(LocalDate.now())) {

            log.warn("[validateApprovalStatus][모집 승인 상태가 아닌 경우 또는 승인일로부터 30일이 지나지 않았습니다.]"
                            + "[현재 상태: {}, 계약서 완료 날짜: {}, 30일 후 날짜: {}, 현재 날짜: {}]",
                    resume.getRecruitmentStatus(),
                    resume.getApprovedAt(),
                    resume.getApprovedAt().plusMonths(1),
                    LocalDate.now());

            throw new BadRequestException(EVALUATION_NOT_ALLOWED_BEFORE_APPROVAL_DATE.getMessage());
        }
    }

    private void validateEvaluationType(Set<EvaluationCategory> uniqueEvaluationCategories, EvaluationType type){
        for (EvaluationCategory uniqueEvaluationCategory : uniqueEvaluationCategories) {
            if(uniqueEvaluationCategory.getType()!=type){
                log.warn("[validateEvaluationType][평가 타입이 다름.][EvaluationCategory({}, {}) 필요 타입=({})]", uniqueEvaluationCategory.getDescription(), uniqueEvaluationCategory.getType(), type);
                throw new BadRequestException(EVALUATION_TYPE_MISMATCH_EXCEPTION.getMessage());
            }
        }
    }


    public List<EvaluationCategory> getEvaluation(Long memberId, Long resumeId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("[getEvaluation][유저 못 찾음.][memberId= {}]", memberId);
                    return new BadRequestException(USER_NOT_FOUND_EXCEPTION.getMessage());
                });


        EvaluationType type = (member instanceof Employer) ? EvaluationType.EMPLOYER_TO_EMPLOYEE : EvaluationType.EMPLOYEE_TO_EMPLOYER;

        List<ResumeEvaluation> byResumeIdAndType = resumeEvaluationRepository.findByResumeIdAndType(resumeId, type);

        List<EvaluationCategory> response = byResumeIdAndType.stream()
                .map((resumeEvaluation) -> EvaluationCategory.getByDescription(resumeEvaluation.getEvaluation().getCategory()))
                .toList();

        return response;

    }
}
