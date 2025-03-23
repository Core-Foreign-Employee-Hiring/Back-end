package com.core.foreign.api.member.service;

import com.core.foreign.api.member.entity.EvaluationType;
import com.core.foreign.api.member.repository.ResumeEvaluationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EvaluationDeleter {
    private final ResumeEvaluationRepository resumeEvaluationRepository;


    public void deleteEvaluations(Long resumeId, EvaluationType type){
        resumeEvaluationRepository.deleteAllByResumeIdAndType(resumeId, type);
    }
}
