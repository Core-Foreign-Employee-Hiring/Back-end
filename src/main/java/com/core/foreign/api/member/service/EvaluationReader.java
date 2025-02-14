package com.core.foreign.api.member.service;

import com.core.foreign.api.member.dto.EmployeeEvaluationCountDTO;
import com.core.foreign.api.member.dto.EmployerEvaluationCountDTO;
import com.core.foreign.api.member.entity.EmployeeEvaluation;
import com.core.foreign.api.member.entity.EmployerEvaluation;
import com.core.foreign.api.member.entity.EvaluationCategory;
import com.core.foreign.api.member.repository.EmployeeEvaluationRepository;
import com.core.foreign.api.member.repository.EmployerEvaluationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EvaluationReader {
    private final EmployerEvaluationRepository employerEvaluationRepository;
    private final EmployeeEvaluationRepository employeeEvaluationRepository;


    public EmployerEvaluationCountDTO getEmployerEvaluation(Long employerId) {
        List<EmployerEvaluation> evaluations = employerEvaluationRepository.findByEmployerId(employerId);

        EmployerEvaluationCountDTO dto = new EmployerEvaluationCountDTO();

        for (EmployerEvaluation employerEvaluation : evaluations) {
            EvaluationCategory category = EvaluationCategory.getByDescription(employerEvaluation.getEvaluation().getCategory());

            // 평가 항목에 맞는 개수 증가
            switch (category) {
                case PAYS_ON_TIME:
                    dto.setPaysOnTime(employerEvaluation.getEvaluationCount());
                    break;
                case KEEPS_CONTRACT_DATES:
                    dto.setKeepsContractDates(employerEvaluation.getEvaluationCount());
                    break;
                case RESPECTS_EMPLOYEES:
                    dto.setRespectsEmployees(employerEvaluation.getEvaluationCount());
                    break;
                case FRIENDLY_BOSS:
                    dto.setFriendlyBoss(employerEvaluation.getEvaluationCount());
                    break;
                case FAIR_WORKLOAD:
                    dto.setFairWorkload(employerEvaluation.getEvaluationCount());
                    break;
                default:
                    break;
            }
        }

        // 임시.
        dto.setJoinCount(0);

        return dto;
    }

    public EmployeeEvaluationCountDTO getEmployeeEvaluation(Long employeeId) {
        List<EmployeeEvaluation> evaluations = employeeEvaluationRepository.findByEmployeeId(employeeId);

        EmployeeEvaluationCountDTO dto = new EmployeeEvaluationCountDTO();

        for (EmployeeEvaluation employeeEvaluation : evaluations) {
            EvaluationCategory category = EvaluationCategory.getByDescription(employeeEvaluation.getEvaluation().getCategory());

            // 평가 항목에 맞는 개수 증가
            switch (category) {
                case WORKS_DILIGENTLY:
                    dto.setWorksDiligently(employeeEvaluation.getEvaluationCount());
                    break;
                case NO_LATENESS_OR_ABSENCE:
                    dto.setNoLatenessOrAbsence(employeeEvaluation.getEvaluationCount());
                    break;
                case POLITE_AND_FRIENDLY:
                    dto.setPoliteAndFriendly(employeeEvaluation.getEvaluationCount());
                    break;
                case GOOD_CUSTOMER_SERVICE:
                    dto.setGoodCustomerService(employeeEvaluation.getEvaluationCount());
                    break;
                case SKILLED_AT_WORK:
                    dto.setSkilledAtWork(employeeEvaluation.getEvaluationCount());
                    break;
                default:
                    break;
            }
        }

        return dto;
    }

}
