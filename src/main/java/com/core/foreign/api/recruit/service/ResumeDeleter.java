package com.core.foreign.api.recruit.service;

import com.core.foreign.api.recruit.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResumeDeleter {
    private final ResumeRepository resumeRepository;

    @Transactional
    public void deleteResumeOnEmployeeWithdrawal(Long employeeId){
        resumeRepository.softDeleteByEmployeeId(employeeId);
    }
}
