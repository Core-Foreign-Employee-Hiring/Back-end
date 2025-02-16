package com.core.foreign.api.member.service;

import com.core.foreign.api.member.entity.*;
import com.core.foreign.api.member.repository.EmployeeEvaluationRepository;
import com.core.foreign.api.member.repository.EmployerEvaluationRepository;
import com.core.foreign.api.member.repository.EvaluationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EvaluationCreator {
    private final EvaluationRepository evaluationRepository;
    private final EmployeeEvaluationRepository employeeEvaluationRepository;
    private final EmployerEvaluationRepository employerEvaluationRepository;


    @Transactional
    public void initializeEmployerEvaluation(Employer employer) {
        List<EmployerEvaluation> employerEvaluation=new ArrayList<>();

        for(EvaluationCategory category :EvaluationCategory.getByType(EvaluationType.EMPLOYEE_TO_EMPLOYER)){
            Evaluation evaluation = getEvaluation(category, EvaluationType.EMPLOYEE_TO_EMPLOYER);
            EmployerEvaluation eE = new EmployerEvaluation(employer, evaluation);

            employerEvaluation.add(eE);
        }

        employerEvaluationRepository.saveAll(employerEvaluation);
    }

    @Transactional
    public void initializeEmployeeEvaluation(Employee employee) {
        List<EmployeeEvaluation> employeeEvaluation=new ArrayList<>();

        for(EvaluationCategory category :EvaluationCategory.getByType(EvaluationType.EMPLOYER_TO_EMPLOYEE)){
            Evaluation evaluation = getEvaluation(category, EvaluationType.EMPLOYER_TO_EMPLOYEE);
            EmployeeEvaluation eE = new EmployeeEvaluation(employee, evaluation);

            employeeEvaluation.add(eE);
        }

        employeeEvaluationRepository.saveAll(employeeEvaluation);
    }


    private Evaluation getEvaluation(EvaluationCategory evaluationCategory, EvaluationType type) {
        if(evaluationCategory.getType()!=type){
            log.error("다른 타입 get 시도 EvaluationCategory({}, {}) EvaluationType({})", evaluationCategory.getDescription(),evaluationCategory.getType() , type);
            return null;
        }
        return evaluationRepository.findByCategoryAndType(evaluationCategory.getDescription(), type)
                .orElseGet(() -> {
                    log.error("DB에 evaluation({},{})가 없음. 임시로 여기서 insert 하지만 DB 확인 필요.",
                            evaluationCategory.getDescription(), type);

                    Evaluation newEvaluation = new Evaluation(evaluationCategory.getDescription(), type);
                    return evaluationRepository.save(newEvaluation);
                });
    }
}
