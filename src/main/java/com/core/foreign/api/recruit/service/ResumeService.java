package com.core.foreign.api.recruit.service;

import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.contract.service.ContractCreator;
import com.core.foreign.api.member.dto.EmployeePortfolioDTO;
import com.core.foreign.api.member.dto.TagResponseDTO;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.EmployeePortfolio;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.member.repository.EmployeePortfolioRepository;
import com.core.foreign.api.member.repository.EmployeeRepository;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.recruit.dto.*;
import com.core.foreign.api.recruit.dto.internal.ResumeDTO;
import com.core.foreign.api.recruit.entity.*;
import com.core.foreign.api.recruit.repository.PremiumRecruitRepository;
import com.core.foreign.api.recruit.repository.RecruitRepository;
import com.core.foreign.api.recruit.repository.ResumePortfolioRepository;
import com.core.foreign.api.recruit.repository.ResumeRepository;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ResumeReader resumeReader;
    private final ContractCreator contractCreator;
    private final EmployeeRepository employeeRepository;
    private final EmployeePortfolioRepository employeePortfolioRepository;

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

        List<ResumePortfolioRequestDTO> resumePortfolios = dto.getResumePortfolios();

        checkPortfolio(premiumRecruit, resumePortfolios);

        // 일단 이력서 저장.

        Resume resume = doApplyResume(employeeId, premiumRecruit, dto.getGeneralResumeRequestDTO());
        if (dto.isPortfolioPublic()) {resume.makePublic();}
        else {resume.makePrivate();}

        // 개수 확인.
        Map<Long, Integer> map=new HashMap<>();  // original: key: portfolioId value: maxFileCount();
        Map<Long, Portfolio> map3=new HashMap<>();  //   original: key: portfolioId value: Portfolio();
        List<Portfolio> portfolios = premiumRecruit.getPortfolios();
        for (Portfolio portfolio : portfolios) {
            map3.put(portfolio.getId(), portfolio);
            if(portfolio.getType().equals(PortfolioType.FILE_UPLOAD)){
                map.put(portfolio.getId(), portfolio.getMaxFileCount());
            }
        }

        List<ResumePortfolio> resumePortfolioEntities = resumePortfolios.stream().map((p) -> p.toEntity(resume, map3)).toList();
        Map<Long, Integer> map2=new HashMap<>();  //  request: key: portfolioId, value: 개수
        for (ResumePortfolio resumePortfolioEntity : resumePortfolioEntities) {
            map2.merge(resumePortfolioEntity.getRecruitPortfolioId(), 1, Integer::sum);
        }

        // 장문 또는 단문인데 2개 이상 예외.

        for(Long portfolioId:map2.keySet()){
            if(map2.get(portfolioId)==1){continue;}

            Portfolio portfolio = map3.get(portfolioId);

            if(portfolio.getType().equals(PortfolioType.FILE_UPLOAD)){continue;}

            log.warn("[applyPremiumResume][장문 또는 단문인데 2개 이상 요청.][portfolioId= {}", portfolioId);

            throw new BadRequestException(DUPLICATE_PORTFOLIO_TYPE_FOR_SAME_PORTFOLIO_EXCEPTION.getMessage());
        }


        // 파일 개수 판단.
        for (Long portfolioId : map.keySet()) {
            if(!map2.containsKey(portfolioId)){map2.put(portfolioId,0);}

            Integer request = map2.get(portfolioId);
            Integer max = map.get(portfolioId);
            if(max<request){
                log.warn("[applyPremiumResume][파일 개수 초과][portfolioId= {}, max= {}, 요청= {}]", portfolioId, max, request);

                throw new BadRequestException(FILE_COUNT_MISMATCH_EXCEPTION.getMessage());
            }

            if(map3.get(portfolioId).isRequired() && max!=0 && request==0){
                log.warn("[applyPremiumResume][필수인데 0개 입력][portfolioId= {}, max= {}, 요청= {}]", portfolioId, max, request);

                throw new BadRequestException(FILE_COUNT_MISMATCH_EXCEPTION.getMessage());
            }
        }


        // 이력서에 딸린 포트폴리오 저장.
        resumePortfolioRepository.saveAll(resumePortfolioEntities);

        return resume.getId();
    }


    /**
     * 이상한 거 넘겼는지 and 필수 없는지
     */
    private void checkPortfolio(PremiumRecruit premiumRecruit , List<ResumePortfolioRequestDTO> dto) {

        List<Portfolio> portfolios = premiumRecruit.getPortfolios();

        Set<Long >requestHashSet=new HashSet<>();
        for (ResumePortfolioRequestDTO dto1 : dto) {
            requestHashSet.add(dto1.getPortfolioId());
        }

        List<Long> requests = requestHashSet.stream().toList();

        // 이상한 거 넘겼는지
        List<Long> portfolioIds = portfolios.stream().map(Portfolio::getId).toList();

        if (!new HashSet<>(portfolioIds).containsAll(requests)){
            List<Long> invalids=new ArrayList<>(requests);
            invalids.removeAll(portfolioIds);

            log.warn("[ResumeService][checkPortfolio][이상한 포트폴리오 넘겼음][invalidPortfolioIds={}]",invalids);
            throw new BadRequestException(INVALID_PORTFOLIO_EXCEPTION.getMessage());
        }

        // 필수
        List<Long> requires=new ArrayList<>();

        for (Portfolio portfolio : portfolios) {
            if(portfolio.isRequired()){
                requires.add(portfolio.getId());
            }
        }

        // requires 가 requests 의 부분 집합이면 성공.

        if (!new HashSet<>(requests).containsAll(requires)){
            log.warn("필수 포트폴리오 없음.");
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
    public ApplicationResumeResponseDTO getResumeForEmployer(Long resumeId) {
        // 이력서
        ResumeDTO resume = resumeReader.getResumeForEmployer(resumeId);

        Long employeeId = resume.getEmployeeId();

        // 피고용인 회원 정보
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.warn("[getMyResume][employee not found][employeeId= {}]", employeeId);
                    return new BadRequestException(ErrorStatus.USER_NOT_FOUND_EXCEPTION.getMessage());
                });

        // 피고용인 스펙 및 경력

        EmployeePortfolioDTO employeePortfolioDTO = employeePortfolioRepository.findEmployeePortfolioByEmployeeId(employeeId)
                .map(EmployeePortfolioDTO::from)
                .orElseGet(EmployeePortfolioDTO::emptyPortfolio);

        ApplicationResumeResponseDTO response = ApplicationResumeResponseDTO.of(resume, employee, employeePortfolioDTO);

        return response;

    }


    private Resume doApplyResume(Long employeeId, Recruit recruit, GeneralResumeRequestDTO dto){
        if(!dto.isThirdPartyConsent()){throw new BadRequestException(THIRD_PARTY_CONSENT_REQUIRED_EXCEPTION.getMessage());}

        if(recruit.getRecruitPublishStatus().equals(RecruitPublishStatus.DRAFT)){
            log.warn("[doApplyResume][임시 저장된 공고 지원 불가][recruitId= {}]", recruit.getId());
            throw new BadRequestException(TEMPORARY_RECRUIT_APPLICATION_NOT_ALLOWED_EXCEPTION.getMessage());
        }

        Member member =  memberRepository.findById(employeeId).get();
        if(member instanceof Employer){
            log.warn("고용인이 공고 지원 신청. memberId= {}", employeeId);
            throw new BadRequestException(EMPLOYER_CANNOT_APPLY_EXCEPTION.getMessage());
        }

        Employee employee = (Employee) member;

        Optional<Resume> findResume = resumeRepository.findByEmployeeIdAndRecruitId(employeeId, recruit.getId());

        if(findResume.isPresent()){
            log.warn("[doApplyResume][중복 지원은 불가합니다.][resumeId= {}]", findResume.get().getId());
            throw new BadRequestException(DUPLICATE_APPLICATION_NOT_ALLOWED_EXCEPTION.getMessage());

        }

        if(recruit.getRecruitType().equals(RecruitType.PREMIUM)){
            EmployeePortfolio employeePortfolio = employeePortfolioRepository.findEmployeePortfolioByEmployeeId(employee.getId())
                    .orElseThrow(() -> {
                        log.warn("[doApplyResume][포트폴리오 없음][employeeId= {}]", employeeId);
                        return new NotFoundException(PORTFOLIO_NOT_FOUND_EXCEPTION.getMessage());
                    });

            if(employeePortfolio.getIntroduction()==null||employeePortfolio.getIntroduction().isEmpty()||
                    employeePortfolio.getEnrollmentCertificateUrl()==null||employeePortfolio.getEnrollmentCertificateUrl().isEmpty()||
                    employeePortfolio.getTranscriptUrl()==null||employeePortfolio.getTranscriptUrl().isEmpty()||
                    employeePortfolio.getPartTimeWorkPermitUrl()==null||employeePortfolio.getPartTimeWorkPermitUrl().isEmpty()
            ){
                log.warn("[doApplyResume][필수 항수 없음][employeeId= {}]", employeeId);
                throw new BadRequestException(MISSING_REQUIRED_SPEC_OR_EXPERIENCE_EXCEPTION.getMessage());
            }
        }

        Resume build = Resume.builder()
                .messageToEmployer(dto.getMessageToEmployer())
                .recruit(recruit)
                .employee(employee)
                .applyMethod(dto.getApplyMethod())
                .recruitmentStatus(RecruitmentStatus.PENDING)
                .contractStatus(ContractStatus.NOT_COMPLETED)
                .isDeleted(false)
                .isEmployeeEvaluatedByEmployer(EvaluationStatus.NONE)
                .isEmployerEvaluatedByEmployee(EvaluationStatus.NONE)
                .approvedAt(LocalDate.of(9999, 12, 31))
                .viewCount(0)
                .isPublic(false)
                .build();

        // IDENTITY 전략이라 바로 사용해도 상관 x
        Resume response = resumeRepository.save(build);

        return response;
    }

    public PageResponseDTO<ApplicationResumePreviewResponseDTO> searchApplicationResume(Long recruitId,
                                                                                        String keyword, RecruitmentStatus recruitmentStatus, ContractStatus contractStatus,
                                                                                        Integer page, Integer size) {

        PageResponseDTO<ApplicationResumePreviewResponseDTO> response = resumeReader.searchApplicationResume(recruitId, keyword, recruitmentStatus, contractStatus, page, size);

        return response;
    }

    @Transactional
    public void approveOrRejectResume(Long memberId, Long resumeId, RecruitmentStatus recruitmentStatus){
        Resume resume = resumeRepository.findByResumeIdWithRecruitAndEmployer(resumeId)
                .orElseThrow(() -> {
                    log.warn("이력서 없음 resumeId= {}", resumeId);
                    return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });

        Recruit recruit = resume.getRecruit();

        Employer employer = (Employer) recruit.getEmployer();

        if(!employer.getId().equals(memberId)){
            log.warn("[approveResume][고용주가 아닌데 변경 시도.][employerId= {}, memberId= {}]", employer.getId(), memberId);
            throw new BadRequestException(INVALID_USER_EXCEPTION.getMessage());
        }

        if(resume.getRecruitmentStatus().equals(RecruitmentStatus.APPROVED)){
            log.warn("이미 승인된 이력서 resumeId= {}", resumeId);
            throw new BadRequestException(ALREADY_APPROVED_RESUME_EXCEPTION.getMessage());
        }

        if(resume.getRecruitmentStatus().equals(RecruitmentStatus.REJECTED)){
            log.warn("이미 거절된 이력서 resumeId= {}", resumeId);
            throw new BadRequestException(ALREADY_REJECTED_RESUME_EXCEPTION.getMessage());
        }

        if(recruitmentStatus.equals(RecruitmentStatus.APPROVED)){
            resume.approve();
            contractCreator.createContractMetadata(resume);
        }
        else if(recruitmentStatus.equals(RecruitmentStatus.REJECTED)){
            resume.reject();
        }

    }


    public PageResponseDTO<EmployeeApplicationStatusResponseDTO> getMyResumes(Long employeeId, Integer page, Integer size){

        PageResponseDTO<EmployeeApplicationStatusResponseDTO> response = resumeReader.getMyResumes(employeeId, page, size);

        return response;
    }

    @Transactional
    public void removeMyResume(Long employeeId, Long resumeId){
        Resume resume = resumeRepository.findResumeWithEmployeeAndRecruit(resumeId)
                .orElseThrow(() -> {
                    log.warn("이력서 없음. resumeId= {}", resumeId);
                    return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });


        Employee employee = resume.getEmployee();

        if(!Objects.equals(employeeId, employee.getId())){
            log.warn("다른 사람 이력서 삭세 시도.");
            throw new BadRequestException(UNAUTHORIZED_RESUME_DELETE_EXCEPTION.getMessage());
        }

        /**
         * 모집 승인된 거 어떻게 처리해야 하지.
         */

        resume.delete();
    }


    public PageResponseDTO<TagResponseDTO> getTags(Long employerId, EvaluationStatus evaluationStatus, Integer page, Integer size) {
        if(evaluationStatus==EvaluationStatus.NONE){
            log.warn("평가 상태= {}", evaluationStatus);
           throw new BadRequestException(TAG_EVALUATION_STATUS_CANNOT_BE_NONE.getMessage());
        }
        PageResponseDTO<TagResponseDTO> response = resumeReader.getTags(employerId, evaluationStatus, page, size);

        return response;
    }

    @Transactional
    public void flipResumePublic(Long employeeId, Long resumeId){
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> {
                    log.warn("[flipResumePublic][resume not found][resumeId= {}]", resumeId);
                    return new BadRequestException(RESUME_NOT_FOUND_EXCEPTION.getMessage());
                });

        Employee employee = resume.getEmployee();

        if(!Objects.equals(employeeId, employee.getId())){
            log.warn("[flipResumePublic][다른 사람 이력서 공개/비공개 변경 시도][requestEmployerId= {}, employerIdOfResume= {}]", employeeId, employee.getId());
            throw new BadRequestException(INVALID_RESUME_OWNER_EXCEPTION.getMessage());
        }

        resume.flipPublic();

    }

}
