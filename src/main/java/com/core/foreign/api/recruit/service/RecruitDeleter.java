package com.core.foreign.api.recruit.service;

import com.core.foreign.api.recruit.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecruitDeleter {
    private final RecruitRepository recruitRepository;

    @Transactional
    public void deleteRecruitOnEmployeeWithdrawal(Long employerId){
        recruitRepository.softDeleteByEmployerId(employerId);
    }
}
