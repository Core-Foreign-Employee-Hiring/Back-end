package com.core.foreign.api.recruit.service;

import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.recruit.dto.GeneralResumeRequestDTO;
import com.core.foreign.api.recruit.dto.PremiumResumeRequestDTO;
import com.core.foreign.api.recruit.dto.ResumePortfolioRequestDTO;
import com.core.foreign.api.recruit.entity.*;
import com.core.foreign.api.recruit.repository.PremiumRecruitRepository;
import com.core.foreign.api.recruit.repository.RecruitRepository;
import com.core.foreign.api.recruit.repository.ResumePortfolioRepository;
import com.core.foreign.api.recruit.repository.ResumeRepository;
import com.core.foreign.common.exception.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.core.foreign.common.response.ErrorStatus.*;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final RecruitRepository recruitRepository;
    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final PremiumRecruitRepository  premiumRecruitRepository;
    private final ResumePortfolioRepository resumePortfolioRepository;




    @Transactional
    public Long applyResume(Long employeeId, Long recruitId, GeneralResumeRequestDTO dto) {
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new BadRequestException(RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));

        if(recruit.getRecruitType()!=RecruitType.GENERAL) {
            throw  new BadRequestException(INVALID_RECRUIT_TYPE_EXCEPTION.getMessage());
        }

        Long l = doApplyResume(employeeId, recruit, dto);
        return l;
    }


    @Transactional
    public Long applyPremiumResume(Long employeeId, Long recruitId, PremiumResumeRequestDTO dto) {


        PremiumRecruit premiumRecruit = premiumRecruitRepository.findPremiumRecruitWithPortfolioById(recruitId)
                .orElseThrow(() -> new BadRequestException(RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));

        if(premiumRecruit.getRecruitType()!=RecruitType.PREMIUM) {
            throw  new BadRequestException(INVALID_RECRUIT_TYPE_EXCEPTION.getMessage());
        }

        // 필수인데 작성 하지 않은 것들 예외 처리.
        List<ResumePortfolioRequestDTO> resumePortfolios = dto.getResumePortfolios();

        checkRequiredPortfolio(premiumRecruit, resumePortfolios);


        // 일단 이력서 저장.

        Long resumeId = doApplyResume(employeeId, premiumRecruit, dto.getGeneralResumeRequestDTO());


        // 이력서에 딸린 포트폴리오 저장.

        Resume resume = resumeRepository.findById(resumeId).get();

        List<ResumePortfolio> resumePortfolioEntities = resumePortfolios.stream().map((p) -> p.toEntity(resume)).toList();


        resumePortfolioRepository.saveAll(resumePortfolioEntities);

        return resumeId;
    }


    private void checkRequiredPortfolio(PremiumRecruit premiumRecruit , List<ResumePortfolioRequestDTO> dto) {

        List<Portfolio> portfolios = premiumRecruit.getPortfolios();

        List<Long> requires=new ArrayList<>();

        for (Portfolio portfolio : portfolios) {
            if(portfolio.isRequired()){
                requires.add(portfolio.getId());
            }
        }

        List<Long >requests=new ArrayList<>();
        for (ResumePortfolioRequestDTO dto1 : dto) {
            requests.add(dto1.getPortfolioId());
        }

        // requires 가 requests 의 부분 집합이면 성공.

        if (!new HashSet<>(requests).containsAll(requires)){
            throw new BadRequestException(REQUIRED_PORTFOLIO_MISSING_EXCEPTION.getMessage());
        }


    }


    private Long doApplyResume(Long employeeId, Recruit recruit, GeneralResumeRequestDTO dto){
        if(!dto.isThirdPartyConsent()){throw new BadRequestException(THIRD_PARTY_CONSENT_REQUIRED_EXCEPTION.getMessage());}


        Employee employee = (Employee) memberRepository.findById(employeeId).get();

        Resume build = Resume.builder()
                .messageToEmployer(dto.getMessageToEmployer())
                .recruit(recruit)
                .employee(employee)
                .applyMethod(ApplyMethod.ONLINE)
                .recruitmentStatus(RecruitmentStatus.PENDING)
                .evaluationStatus(EvaluationStatus.NOT_EVALUATED)
                .contractStatus(ContractStatus.NOT_WRITTEN)
                .build();


        return resumeRepository.save(build).getId();
    }
}
