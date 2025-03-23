package com.core.foreign.api.member.service;

import com.core.foreign.api.member.dto.EvaluationCategoryResponseDTO;
import com.core.foreign.api.member.entity.*;
import com.core.foreign.api.member.repository.*;
import com.core.foreign.api.recruit.entity.EvaluationStatus;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.RecruitmentStatus;
import com.core.foreign.api.recruit.entity.Resume;
import com.core.foreign.api.recruit.repository.ResumeRepository;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.NotFoundException;
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
    private final EvaluationReader evaluationReader;
    private final EvaluationDeleter evaluationDeleter;


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
                    resume.getContractCompletionDate(),
                    resume.getContractCompletionDate().plusMonths(1),
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

    /**
     * 고용인 -> 피고용인 평가 항목 갖고 오기
     */
    public EvaluationCategoryResponseDTO getEmployerToEmployeeEvaluation(Long resumeId) {
        List<EvaluationCategory> evaluationCategories = evaluationReader.getEvaluation(resumeId, EvaluationType.EMPLOYER_TO_EMPLOYEE);

        EvaluationCategoryResponseDTO response = new EvaluationCategoryResponseDTO(resumeId, evaluationCategories);

        return response;

    }

    /**
     * 피고용인 -> 고용인 평가 항목 갖고 오기
     */
    public EvaluationCategoryResponseDTO getEmployeeToEmployerEvaluation(Long employeeId, Long recruitId) {
        Long resumeId = resumeRepository.findResumeIdByEmployeeIdAndRecruitId(employeeId, recruitId)
                .orElseThrow(() -> {
                    log.warn("[getEmployeeToEmployerEvaluation][이력서 찾을 수 없음.][employeeId= {}, recruitId= {}]", employeeId, recruitId);
                    return new NotFoundException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });

        List<EvaluationCategory> evaluationCategories = evaluationReader.getEvaluation(resumeId, EvaluationType.EMPLOYEE_TO_EMPLOYER);

        EvaluationCategoryResponseDTO response = new EvaluationCategoryResponseDTO(resumeId, evaluationCategories);

        return response;
    }

    /**
     * 고용인 -> 피고용인 평가 삭제
     */
    public void deleteEmployerToEmployeeEvaluation(Long employerId, Long resumeId) {
        boolean b = resumeRepository.exitsByResumeIdAndEmployerId(resumeId, employerId);

        if(!b){
            log.warn("[deleteEmployerToEmployeeEvaluation][이력서 없음][resumeId= {}, employerId= {}]", resumeId, employerId);
            throw  new NotFoundException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
        }

        evaluationDeleter.deleteEvaluations(resumeId, EvaluationType.EMPLOYER_TO_EMPLOYEE);

    }

    /**
     * 피고용인 -> 고용인 평가 평가 삭제
     */
    public void deleteEmployeeToEmployerEvaluation(Long employeeId, Long resumeId) {
        boolean b = resumeRepository.exitsByResumeIdAndEmployeeId(resumeId, employeeId);

        if(!b){
            log.warn("[deleteEmployerToEmployeeEvaluation][이력서 없음][resumeId= {}, employeeId= {}]", resumeId, employeeId);
            throw  new NotFoundException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
        }

        evaluationDeleter.deleteEvaluations(resumeId, EvaluationType.EMPLOYEE_TO_EMPLOYER);
    }

}
