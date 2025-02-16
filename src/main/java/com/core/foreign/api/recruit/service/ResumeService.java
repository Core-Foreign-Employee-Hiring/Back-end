package com.core.foreign.api.recruit.service;

import com.core.foreign.api.member.dto.TagResponseDTO;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.member.repository.EmployeePortfolioRepository;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.recruit.dto.*;
import com.core.foreign.api.recruit.entity.*;
import com.core.foreign.api.recruit.repository.PremiumRecruitRepository;
import com.core.foreign.api.recruit.repository.RecruitRepository;
import com.core.foreign.api.recruit.repository.ResumePortfolioRepository;
import com.core.foreign.api.recruit.repository.ResumeRepository;
import com.core.foreign.common.exception.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static com.core.foreign.common.response.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {
    private final RecruitRepository recruitRepository;
    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;
    private final PremiumRecruitRepository  premiumRecruitRepository;
    private final ResumePortfolioRepository resumePortfolioRepository;
    private final EmployeePortfolioRepository employeePortfolioRepository;
    private final ResumeReader resumeReader;

    @Transactional
    public Long applyResume(Long employeeId, Long recruitId, GeneralResumeRequestDTO dto) {
        Recruit recruit = recruitRepository.findById(recruitId).orElseThrow(() -> new BadRequestException(RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));

        if(recruit.getRecruitType()!=RecruitType.GENERAL) {
            throw  new BadRequestException(INVALID_RECRUIT_TYPE_EXCEPTION.getMessage());
        }

        Resume resume = doApplyResume(employeeId, recruit, dto);
        return resume.getId();
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

        // 파일 개수 확인.
        Map<Long, Integer> map=new HashMap<>();  // key: portfolioId value: maxFileCount();
        Map<Long, Portfolio> map3=new HashMap<>();
        List<Portfolio> portfolios = premiumRecruit.getPortfolios();
        for (Portfolio portfolio : portfolios) {
            map3.put(portfolio.getId(), portfolio);
            if(portfolio.getType().equals(PortfolioType.FILE_UPLOAD)){
                map.put(portfolio.getId(), portfolio.getMaxFileCount());
            }
        }


        // 일단 이력서 저장.

        Resume resume = doApplyResume(employeeId, premiumRecruit, dto.getGeneralResumeRequestDTO());
        if (dto.isPublic()) {resume.makePublic();}
        else {resume.makePrivate();}


        // 이력서에 딸린 포트폴리오 저장.
        List<ResumePortfolio> resumePortfolioEntities = resumePortfolios.stream().map((p) -> p.toEntity(resume)).toList();
        Map<Long, Integer> map2=new HashMap<>();
        for (ResumePortfolio resumePortfolioEntity : resumePortfolioEntities) {
            map2.merge(resumePortfolioEntity.getRecruitPortfolioId(), 1, Integer::sum);
        }

        for (Long portfolioId : map.keySet()) {
            if(!map2.containsKey(portfolioId)){map2.put(portfolioId,0);}

            Integer request = map2.get(portfolioId);
            Integer required = map.get(portfolioId);
            if(!Objects.equals(request, required)){
                log.error("portfolioId= {}: 파일 개수 안 맞음. 필요= {}, 요청= {}", portfolioId, required, request);


                // 필수면 예외 터트림.
                if(map3.get(portfolioId).isRequired()){
                    log.error("필수라 예외 터트린다.");
                    throw new BadRequestException(FILE_COUNT_MISMATCH_EXCEPTION.getMessage());
                }
                log.warn("파일 개수 안 맞는데 필수는 아니라서 그냥 넘어간다.");
            }
        }


        resumePortfolioRepository.saveAll(resumePortfolioEntities);

        return resume.getId();
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
            log.error("필수 포트폴리오 없음.");
            throw new BadRequestException(REQUIRED_PORTFOLIO_MISSING_EXCEPTION.getMessage());
        }


    }


    /**
     * 해당 공고의 특정 지원자의 이력서를 조회. <p>
     * if) 피고용인
     *  뒤로가기
     * else if 고용인
     *  if 평가 x
     *   승인 또는 거절
     *  else if 거절
     *   뒤로가기
     *  else if 승인
     *   평가하기 또는 평가 보기
     *   계약서 작성하기 또는 보기
     */
    public ApplicationResumeResponseDTO getResume(Long memberId, Long resumeId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("유저를 찾을 수 없음. memberId= {}", memberId);
                    return new BadRequestException(USER_NOT_FOUND_EXCEPTION.getMessage());
                });

        ApplicationResumeResponseDTO response = resumeReader.getResume(resumeId);

        response.setRole(member.getRole());

        return response;
    }


    private Resume doApplyResume(Long employeeId, Recruit recruit, GeneralResumeRequestDTO dto){
        if(!dto.isThirdPartyConsent()){throw new BadRequestException(THIRD_PARTY_CONSENT_REQUIRED_EXCEPTION.getMessage());}

        Member member =  memberRepository.findById(employeeId).get();
        if(member instanceof Employer){
            log.error("고용인이 공고 지원 신청. memberId= {}", employeeId);
            throw new BadRequestException(EMPLOYER_CANNOT_APPLY_EXCEPTION.getMessage());
        }

        Employee employee = (Employee) member;

        Optional<Resume> findResume = resumeRepository.findByEmployeeIdAndRecruitId(employeeId, recruit.getId());

        if(findResume.isPresent()){
            log.error("중복 지원은 불가합니다. resumeId= {}", findResume.get().getId());
            throw new BadRequestException(DUPLICATE_APPLICATION_NOT_ALLOWED_EXCEPTION.getMessage());

        }




        Resume build = Resume.builder()
                .messageToEmployer(dto.getMessageToEmployer())
                .recruit(recruit)
                .employee(employee)
                .applyMethod(dto.getApplyMethod())
                .recruitmentStatus(RecruitmentStatus.PENDING)
                .contractStatus(ContractStatus.NOT_WRITTEN)
                .isDeleted(false)
                .isEmployeeEvaluatedByEmployer(EvaluationStatus.NONE)
                .isEmployerEvaluatedByEmployee(EvaluationStatus.NONE)
                .approvedAt(LocalDate.MAX)
                .build();

        // IDENTITY 전략이라 바로 사용해도 상관 x
        Resume response = resumeRepository.save(build);

        return response;
    }

    public Page<ApplicationResumePreviewResponseDTO> searchApplicationResume(Long recruitId,
                                                                             String keyword, RecruitmentStatus recruitmentStatus, ContractStatus contractStatus,
                                                                             Integer page){

        Page<ApplicationResumePreviewResponseDTO> response = resumeReader.searchApplicationResume(recruitId, keyword, recruitmentStatus, contractStatus, page);

        return response;
    }



    @Transactional
    public void rejectResume(Long resumeId){
        Resume resume = resumeRepository.findByResumeIdWithRecruit(resumeId).orElseThrow(() -> new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage()));
        resume.reject();
    }

    @Transactional
    public void approveResume(Long resumeId){
        Resume resume = resumeRepository.findByResumeIdWithRecruit(resumeId)
                .orElseThrow(() -> {
                    log.error("이력서 없음 resumeId= {}", resumeId);
                    return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });

        Recruit recruit = resume.getRecruit();

        if(resume.getRecruitmentStatus().equals(RecruitmentStatus.APPROVED)){
            log.error("이미 승인된 이력서 resumeId= {}", resumeId);
            throw new BadRequestException(ALREADY_APPROVED_RESUME_EXCEPTION.getMessage());
        }

        if(resume.getRecruitmentStatus().equals(RecruitmentStatus.REJECTED)){
            log.error("이미 거절된 이력서 resumeId= {}", resumeId);
            throw new BadRequestException(ALREADY_REJECTED_RESUME_EXCEPTION.getMessage());
        }

        resume.approve();
    }


    public Page<EmployeeApplicationStatusResponseDTO> getMyResumes(Long employeeId, Integer page){

        Page<EmployeeApplicationStatusResponseDTO> response = resumeReader.getMyResumes(employeeId, page);

        return response;
    }

    @Transactional
    public void removeMyResume(Long employeeId, Long resumeId){
        Resume resume = resumeRepository.findResumeWithEmployeeAndRecruit(resumeId)
                .orElseThrow(() -> {
                    log.error("이력서 없음. resumeId= {}", resumeId);
                    return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });


        Employee employee = resume.getEmployee();

        if(!Objects.equals(employeeId, employee.getId())){
            log.error("다른 사람 이력서 삭세 시도.");
            throw new BadRequestException(UNAUTHORIZED_RESUME_DELETE_EXCEPTION.getMessage());
        }


        resume.delete();
    }


    public Page<TagResponseDTO> getTags(Long employerId, EvaluationStatus evaluationStatus, Integer page, Integer size) {
        if(evaluationStatus==EvaluationStatus.NONE){
            log.error("평가 상태= {}", evaluationStatus);
           throw new BadRequestException(TAG_EVALUATION_STATUS_CANNOT_BE_NONE.getMessage());
        }
        Page<TagResponseDTO> response = resumeReader.getTags(employerId, evaluationStatus, page, size);

        return response;
    }

}
