package com.core.foreign.api.recruit.service;

import com.core.foreign.api.aws.service.S3Service;
import com.core.foreign.api.member.dto.EmployerEvaluationCountDTO;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.member.service.EvaluationReader;
import com.core.foreign.api.recruit.dto.*;
import com.core.foreign.api.recruit.entity.*;
import com.core.foreign.api.recruit.repository.*;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.InternalServerException;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static com.core.foreign.common.response.ErrorStatus.RECRUIT_NOT_FOUND_EXCEPTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitService {

    private final RecruitRepository recruitRepository;
    private final MemberRepository memberRepository;
    private final PremiumManageRepository premiumManageRepository;
    private final S3Service s3Service;
    private final ResumeRepository resumeRepository;
    private final RecruitBookmarkRepository recruitBookmarkRepository;
    private final EvaluationReader evaluationReader;
    private final PortfolioRepository portfolioRepository;

    // 일반 공고 등록
    @Transactional
    public void createGeneralRecruit(
            Long memberId,
            RecruitRequestDTO.GeneralRecruitRequest request,
            MultipartFile posterImage
    ) {
        Member employer = getEmployer(memberId);
        // 포스터 이미지 업로드
        String posterImageUrl = uploadPosterImage(posterImage);

        GeneralRecruit generalRecruit = GeneralRecruit.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .employer(employer)
                .businessFields(new HashSet<>(request.getBusinessFields()))
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .recruitStartDate(request.getRecruitStartDate())
                .recruitEndDate(request.getRecruitEndDate())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(new ArrayList<>(request.getWorkDuration()))
                .workDurationOther(request.getWorkDurationOther())
                .workTime(new ArrayList<>(request.getWorkTime()))
                .workTimeOther(request.getWorkTimeOther())
                .workDays(new ArrayList<>(request.getWorkDays()))
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
                .salaryOther(request.getSalaryOther())
                .applicationMethods(new HashSet<>(request.getApplicationMethods()))
                .posterImageUrl(posterImageUrl)
                .recruitPublishStatus(RecruitPublishStatus.PUBLISHED)
                .build();

        recruitRepository.save(generalRecruit);
    }

    // 프리미엄 공고 등록
    @Transactional
    public void createPremiumRecruit(
            Long memberId,
            RecruitRequestDTO.PremiumRecruitRequest request,
            MultipartFile posterImage
    ) {
        Member employer = getEmployer(memberId);

        // 프리미엄 공고 등록 가능 체크
        PremiumManage premiumManage = premiumManageRepository.findByEmployerId(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PREMIUM_MANAGE_NOT_FOUND_EXCEPTION.getMessage()));

        if (premiumManage.getPremiumCount() == 0) {
            throw new BadRequestException(ErrorStatus.LEAK_PREMIUM_RECRUIT_PUBLISH_COUNT_EXCEPTION.getMessage());
        }
        // 포스터 이미지 업로드
        String posterImageUrl = uploadPosterImage(posterImage);

        PremiumRecruit premiumRecruit = PremiumRecruit.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .employer(employer)
                .businessFields(new HashSet<>(request.getBusinessFields()))
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .recruitStartDate(request.getRecruitStartDate())
                .recruitEndDate(request.getRecruitEndDate())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(new ArrayList<>(request.getWorkDuration()))
                .workDurationOther(request.getWorkDurationOther())
                .workTime(new ArrayList<>(request.getWorkTime()))
                .workTimeOther(request.getWorkTimeOther())
                .workDays(new ArrayList<>(request.getWorkDays()))
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
                .salaryOther(request.getSalaryOther())
                .applicationMethods(new HashSet<>(request.getApplicationMethods()))
                .posterImageUrl(posterImageUrl)
                .recruitPublishStatus(RecruitPublishStatus.PUBLISHED)
                .build();

        List<Portfolio> portfolios = request.getPortfolios().stream()
                .map(p -> Portfolio.builder()
                        .title(p.getTitle())
                        .type(p.getType())
                        .isRequired(p.isRequired())
                        .maxFileCount(p.getMaxFileCount())
                        .build())
                .toList();

        portfolios.forEach(premiumRecruit::addPortfolio);
        recruitRepository.save(premiumRecruit);

        // 프리미엄 공고 등록 횟수 감소
        PremiumManage updatedPremiumManage = premiumManage.decreasePremiumCount();
        premiumManageRepository.save(updatedPremiumManage);
    }

    // 일반 공고 임시저장
    @Transactional
    public void saveGeneralRecruitDraft(
            Long memberId,
            RecruitRequestDTO.GeneralRecruitRequest request,
            MultipartFile posterImage
    ) {
        Member employer = getEmployer(memberId);

        // 임시 저장 한 데이터 삭제
        deleteDraft(employer);

        // 포스터 이미지 업로드
        String posterImageUrl = uploadPosterImage(posterImage);

        GeneralRecruit draftRecruit = GeneralRecruit.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .employer(employer)
                .businessFields(new HashSet<>(request.getBusinessFields()))
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .recruitStartDate(request.getRecruitStartDate())
                .recruitEndDate(request.getRecruitEndDate())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(new ArrayList<>(request.getWorkDuration()))
                .workDurationOther(request.getWorkDurationOther())
                .workTime(new ArrayList<>(request.getWorkTime()))
                .workTimeOther(request.getWorkTimeOther())
                .workDays(new ArrayList<>(request.getWorkDays()))
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
                .salaryOther(request.getSalaryOther())
                .applicationMethods(new HashSet<>(request.getApplicationMethods()))
                .posterImageUrl(posterImageUrl)
                .recruitPublishStatus(RecruitPublishStatus.DRAFT)
                .build();

        recruitRepository.save(draftRecruit);
    }

    // 프리미엄 공고 임시저장
    @Transactional
    public void savePremiumRecruitDraft(
            Long memberId,
            RecruitRequestDTO.PremiumRecruitRequest request,
            MultipartFile posterImage
    ) {
        Member employer = getEmployer(memberId);

        /*
        // 프리미엄 공고 등록 가능 체크
        PremiumManage premiumManage = premiumManageRepository.findByEmployerId(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PREMIUM_MANAGE_NOT_FOUND_EXCEPTION.getMessage()));

        if (premiumManage.getPremiumCount() == 0) {
            throw new BadRequestException(ErrorStatus.LEAK_PREMIUM_RECRUIT_PUBLISH_COUNT_EXCEPTION.getMessage());
        }
*/
        // 임시 저장 한 데이터 삭제
        deleteDraft(employer);

        // 포스터 이미지 업로드
        String posterImageUrl = uploadPosterImage(posterImage);

        PremiumRecruit draftRecruit = PremiumRecruit.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .employer(employer)
                .businessFields(new HashSet<>(request.getBusinessFields()))
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .recruitStartDate(request.getRecruitStartDate())
                .recruitEndDate(request.getRecruitEndDate())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(new ArrayList<>(request.getWorkDuration()))
                .workDurationOther(request.getWorkDurationOther())
                .workTime(new ArrayList<>(request.getWorkTime()))
                .workTimeOther(request.getWorkTimeOther())
                .workDays(new ArrayList<>(request.getWorkDays()))
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
                .salaryOther(request.getSalaryOther())
                .applicationMethods(new HashSet<>(request.getApplicationMethods()))
                .posterImageUrl(posterImageUrl)
                .recruitPublishStatus(RecruitPublishStatus.DRAFT)
                .build();

        List<Portfolio> portfolios = request.getPortfolios().stream()
                .map(p -> Portfolio.builder()
                        .title(p.getTitle())
                        .type(p.getType())
                        .isRequired(p.isRequired())
                        .maxFileCount(p.getMaxFileCount())
                        .build())
                .toList();

        portfolios.forEach(draftRecruit::addPortfolio);
        recruitRepository.save(draftRecruit);
    }

    // 일반 공고 퍼블리싱
    @Transactional
    public void publishGeneralRecruit(
            Long recruitId,
            RecruitRequestDTO.GeneralRecruitRequest request,
            MultipartFile posterImage
    ) {
        GeneralRecruit recruit = getGeneralRecruit(recruitId);
        validateDraftStatus(recruit);

        // 이전 데이터 삭제
        recruitRepository.delete(recruit);

        // 포스터 이미지 업로드
        String posterImageUrl = uploadPosterImage(posterImage);

        GeneralRecruit newRecruit = GeneralRecruit.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .employer(recruit.getEmployer())
                .businessFields(new HashSet<>(request.getBusinessFields()))
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .recruitStartDate(request.getRecruitStartDate())
                .recruitEndDate(request.getRecruitEndDate())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(new ArrayList<>(request.getWorkDuration()))
                .workDurationOther(request.getWorkDurationOther())
                .workTime(new ArrayList<>(request.getWorkTime()))
                .workTimeOther(request.getWorkTimeOther())
                .workDays(new ArrayList<>(request.getWorkDays()))
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
                .salaryOther(request.getSalaryOther())
                .applicationMethods(new HashSet<>(request.getApplicationMethods()))
                .posterImageUrl(posterImageUrl)
                .recruitPublishStatus(RecruitPublishStatus.PUBLISHED)
                .build();

        recruitRepository.save(newRecruit);
    }

    // 프리미엄 공고 퍼블리싱
    @Transactional
    public void publishPremiumRecruit(
            Long recruitId,
            RecruitRequestDTO.PremiumRecruitRequest request,
            MultipartFile posterImage
    ) {
        PremiumRecruit recruit = getPremiumRecruit(recruitId);
        validateDraftStatus(recruit);

        // 프리미엄 공고 등록 가능 체크
        PremiumManage premiumManage = premiumManageRepository.findByEmployerId(recruit.getEmployer().getId())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PREMIUM_MANAGE_NOT_FOUND_EXCEPTION.getMessage()));

        if (premiumManage.getPremiumCount() == 0) {
            throw new BadRequestException(ErrorStatus.LEAK_PREMIUM_RECRUIT_PUBLISH_COUNT_EXCEPTION.getMessage());
        }

        // 이전 데이터 삭제
        recruitRepository.delete(recruit);

        // 포스터 이미지 업로드
        String posterImageUrl = uploadPosterImage(posterImage);

        PremiumRecruit newRecruit = PremiumRecruit.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .employer(recruit.getEmployer())
                .businessFields(new HashSet<>(request.getBusinessFields()))
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .recruitStartDate(request.getRecruitStartDate())
                .recruitEndDate(request.getRecruitEndDate())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(new ArrayList<>(request.getWorkDuration()))
                .workDurationOther(request.getWorkDurationOther())
                .workTime(new ArrayList<>(request.getWorkTime()))
                .workTimeOther(request.getWorkTimeOther())
                .workDays(new ArrayList<>(request.getWorkDays()))
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
                .salaryOther(request.getSalaryOther())
                .applicationMethods(new HashSet<>(request.getApplicationMethods()))
                .posterImageUrl(posterImageUrl)
                .recruitPublishStatus(RecruitPublishStatus.PUBLISHED)
                .build();

        // Portfolio 추가
        List<Portfolio> portfolios = request.getPortfolios().stream()
                .map(p -> Portfolio.builder()
                        .title(p.getTitle())
                        .type(p.getType())
                        .isRequired(p.isRequired())
                        .maxFileCount(p.getMaxFileCount())
                        .build())
                .toList();
        portfolios.forEach(newRecruit::addPortfolio);
        recruitRepository.save(newRecruit);

        // 프리미엄 공고 등록 횟수 감소
        PremiumManage updatedPremiumManage = premiumManage.decreasePremiumCount();
        premiumManageRepository.save(updatedPremiumManage);
    }

        // 사용자 임시저장 공고 존재 여부 확인
    @Transactional(readOnly = true)
    public boolean hasDrafts(Long memberId) {
        Member employer = getEmployer(memberId);
        Optional<Recruit> draft = recruitRepository.findAllByEmployerAndRecruitPublishStatus(employer, RecruitPublishStatus.DRAFT);
        return draft.isPresent();
    }

    // 임시 저장 공고 데이터 조회
    @Transactional(readOnly = true)
    public RecruitResponseDTO getDraftById(Long recruitId, Long memberId) {
        Recruit draft = recruitRepository.findById(recruitId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));
        if (!RecruitPublishStatus.DRAFT.equals(draft.getRecruitPublishStatus())) {
            throw new BadRequestException(ErrorStatus.NOT_DRAFT_RECRUIT_EXCEPTION.getMessage());
        }
        if (!draft.getEmployer().getId().equals(memberId)) {
            throw new BadRequestException(ErrorStatus.INVALID_USER_EXCEPTION.getMessage());
        }
        return convertDraftToResponseDTO(draft);
    }

    private RecruitResponseDTO convertDraftToResponseDTO(Recruit draft) {
        List<RecruitResponseDTO.PortfolioDTO> portfolioDTOs = new ArrayList<>();
        if (draft instanceof PremiumRecruit premiumRecruit) {
            portfolioDTOs = premiumRecruit.getPortfolios().stream()
                    .map(portfolio -> RecruitResponseDTO.PortfolioDTO.builder()
                            .title(portfolio.getTitle())
                            .type(portfolio.getType())
                            .isRequired(portfolio.isRequired())
                            .maxFileCount(portfolio.getMaxFileCount())
                            .build())
                    .toList();
        }
        return RecruitResponseDTO.builder()
                .id(draft.getId())
                .title(draft.getTitle())
                .address(draft.getAddress())
                .recruitType(draft.getRecruitType())
                .recruitStartDate(draft.getRecruitStartDate())
                .recruitEndDate(draft.getRecruitEndDate())
                .gender(draft.getGender())
                .education(draft.getEducation())
                .otherConditions(draft.getOtherConditions())
                .preferredConditions(new ArrayList<>(draft.getPreferredConditions()))
                .workDuration(new ArrayList<>(draft.getWorkDuration()))
                .workDurationOther(draft.getWorkDurationOther())
                .workTime(new ArrayList<>(draft.getWorkTime()))
                .workTimeOther(draft.getWorkTimeOther())
                .workDays(new ArrayList<>(draft.getWorkDays()))
                .workDaysOther(draft.getWorkDaysOther())
                .salary(draft.getSalary())
                .salaryType(draft.getSalaryType())
                .salaryOther(draft.getSalaryOther())
                .businessFields(new HashSet<>(draft.getBusinessFields()))
                .applicationMethods(new HashSet<>(draft.getApplicationMethods()))
                .posterImageUrl(draft.getPosterImageUrl())
                .latitude(draft.getLatitude())
                .longitude(draft.getLongitude())
                .portfolios(portfolioDTOs)
                .build();
    }

    // 사용자 조회
    private Member getEmployer(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USERID_NOT_FOUND_EXCEPTION.getMessage()));
    }

    // 기존 임시 저장 데이터 삭제
    @Transactional
    protected void deleteDraft(Member employer) {
        recruitRepository.findAllByEmployerAndRecruitPublishStatus(employer, RecruitPublishStatus.DRAFT)
                .ifPresent(recruitRepository::delete);
    }

    // 포스터 이미지 업로드
    private String uploadPosterImage(MultipartFile posterImage) {
        if (posterImage != null && !posterImage.isEmpty()) {
            try {
                return s3Service.uploadRecruitPostImage(posterImage);
            } catch (IOException e) {
                throw new InternalServerException(ErrorStatus.FAIL_UPLOAD_EXCEPTION.getMessage());
            }
        }
        return null;
    }

    // 공고 상태 확인
    private void validateDraftStatus(Recruit recruit) {
        if (!RecruitPublishStatus.DRAFT.equals(recruit.getRecruitPublishStatus())) {
            throw new BadRequestException(ErrorStatus.ALEADY_PUBLISHED_RECRUIT_ARTICLE_EXCEPTION.getMessage());
        }
    }

    // 일반 공고 조회
    private GeneralRecruit getGeneralRecruit(Long recruitId) {
        return (GeneralRecruit) recruitRepository.findById(recruitId)
                .orElseThrow(() -> new NotFoundException(RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));
    }

    // 프리미엄 공고 조회
    private PremiumRecruit getPremiumRecruit(Long recruitId) {
        return (PremiumRecruit) recruitRepository.findById(recruitId)
                .orElseThrow(() -> new NotFoundException(RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));
    }

    // 등록 가능 공고 조회
    @Transactional(readOnly = true)
    public List<String> getAvailableRecruits(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USERID_NOT_FOUND_EXCEPTION.getMessage()));

        List<String> availableRecruits = new ArrayList<>();
        availableRecruits.add("일반 공고");

        // 프리미엄 공고 등록 가능 여부 확인
        Optional<PremiumManage> premiumManageOpt = premiumManageRepository.findByEmployerId(memberId);
        if (premiumManageOpt.isPresent()) {
            PremiumManage premiumManage = premiumManageOpt.get();

            // premiumCount가 1 이상이면 프리미엄 공고 가능
            if (premiumManage.getPremiumCount() > 0) {
                availableRecruits.add("프리미엄 공고");
            }
        }

        return availableRecruits;
    }

    // 필터를 통한 공고 전체 조회
    @Transactional(readOnly = true)
    public Page<RecruitListResponseDTO> getRecruitsWithFilters(RecruitSearchConditionDTO condition) {

        int page = (condition.getPage() != null) ? condition.getPage() : 0;
        int size = (condition.getSize() != null) ? condition.getSize() : 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        // 조회 필터
        Specification<Recruit> spec = Specification
                .where(RecruitSpecifications.isPublished())  // PUBLISHED 상태만
                .and(RecruitSpecifications.businessFieldsIn(condition.getBusinessFields()))
                .and(RecruitSpecifications.workDurationIn(condition.getWorkDurations()))
                .and(RecruitSpecifications.workDaysIn(condition.getWorkDays()))
                .and(RecruitSpecifications.workTimeIn(condition.getWorkTimes()))
                .and(RecruitSpecifications.genderEq(condition.getGender()))
                .and(RecruitSpecifications.salaryTypeEq(condition.getSalaryType()));

        Page<Recruit> recruitPage = recruitRepository.findAll(spec, pageable);
        return recruitPage.map(this::convertToRecruitListResponseDTO);
    }

    private RecruitListResponseDTO convertToRecruitListResponseDTO(Recruit recruit) {
        // (employer가 Employer 타입이면 -> companyName, 아니면 Member.getName() 사용)
        String companyName;
        Member employer = recruit.getEmployer();
        if (employer instanceof Employer emp) {
            companyName = emp.getCompanyName();      // EMPLOYER 엔티티의 회사명
        } else {
            companyName = employer.getName();        // 그냥 Member라면 이름
        }

        // 모집 기간 문자열 : "2025-01-01 ~ 2025-12-31"
        String recruitPeriod = null;
        if (recruit.getRecruitStartDate() != null && recruit.getRecruitEndDate() != null) {
            // 마감일이 2099-12-31일 경우 상시모집
            if(recruit.getRecruitEndDate().toString().equals("2099-12-31")){
                recruitPeriod = "상시모집";
            }else{
                recruitPeriod = recruit.getRecruitStartDate().toString()
                        + " ~ " + recruit.getRecruitEndDate().toString();
            }
        }

        return RecruitListResponseDTO.builder()
                .recruitId(recruit.getId())
                .companyName(companyName)
                .title(recruit.getTitle())
                .address(recruit.getAddress())
                .workTime(recruit.getWorkTime())
                .workDays(recruit.getWorkDays())
                .workDuration(recruit.getWorkDuration())
                .salary(recruit.getSalary())
                .salaryType(recruit.getSalaryType())
                .businessFields(recruit.getBusinessFields())
                .recruitPeriod(recruitPeriod)
                .applicationMethods(recruit.getApplicationMethods())
                .recruitType(recruit.getRecruitType())
                .build();
    }

    // 공고 상세 조회
    @Transactional(readOnly = true)
    public RecruitDetailResponseDTO getRecruitDetail(Long recruitId) {

        Recruit recruit = recruitRepository.findByIdFetchJoin(recruitId)
                .orElseThrow(() -> new NotFoundException(RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));

        String companyName;
        String companyIconImage;
        Address employerAddress = null;
        String employerContact = null;
        String representative = null;
        String employerEmail = null;
        String businessRegistrationNumber = null;

        Member employer = recruit.getEmployer();
        EmployerEvaluationCountDTO employerEvaluationCountDTO=evaluationReader.getEmployerEvaluation(employer.getId());
        if (employer instanceof Employer employerEntity) {
            companyName = employerEntity.getCompanyName();
            companyIconImage = employerEntity.getCompanyImageUrl();
            employerAddress = employerEntity.getAddress();
            employerContact = employerEntity.getMainPhoneNumber();
            representative = employerEntity.getName();
            employerEmail = employerEntity.getCompanyEmail();
            businessRegistrationNumber = employerEntity.getBusinessRegistrationNumber();
        } else {
            companyName = employer.getName();
            companyIconImage = null;
        }

        return RecruitDetailResponseDTO.builder()
                .recruitId(recruit.getId())
                .companyName(companyName)
                .companyIconImage(companyIconImage)
                .title(recruit.getTitle())
                .address(recruit.getAddress())
                .recruitStartDate(recruit.getRecruitStartDate())
                .recruitEndDate(recruit.getRecruitEndDate())
                .gender(recruit.getGender())
                .education(recruit.getEducation())
                .otherConditions(recruit.getOtherConditions())
                .preferredConditions(recruit.getPreferredConditions())
                .businessFields(recruit.getBusinessFields())
                .applicationMethods(recruit.getApplicationMethods())
                .workDuration(recruit.getWorkDuration())
                .workDurationOther(recruit.getWorkDurationOther())
                .workTime(recruit.getWorkTime())
                .workTimeOther(recruit.getWorkTimeOther())
                .workDays(recruit.getWorkDays())
                .workDaysOther(recruit.getWorkDaysOther())
                .salary(recruit.getSalary())
                .salaryType(recruit.getSalaryType())
                .salaryOther(recruit.getSalaryOther())
                .latitude(recruit.getLatitude())
                .longitude(recruit.getLongitude())
                .posterImageUrl(recruit.getPosterImageUrl())
                .recruitType(recruit.getRecruitType())
                .employerAddress(employerAddress)
                .employerContact(employerContact)
                .representative(representative)
                .employerEmail(employerEmail)
                .businessRegistrationNumber(businessRegistrationNumber)
                .employerEvaluationCountDTO(employerEvaluationCountDTO)
                .build();
    }


    public Page<MyRecruitResponseDTO> getMyRecruits(Long employerId, Integer page, Integer size, RecruitType recruitType, boolean excludeExpired){
        Pageable pageable= PageRequest.of(page, size);

        Page<MyRecruitResponseDTO> response = recruitRepository.getMyRecruits(employerId, recruitType, RecruitPublishStatus.PUBLISHED, excludeExpired, pageable)
                .map(MyRecruitResponseDTO::from);


        return response;
    }

    public Page<MyDraftRecruitResponseDTO> getMyDraftRecruits(Long employerId, Integer page, Integer size){
        Pageable pageable= PageRequest.of(page, size);

        Page<MyDraftRecruitResponseDTO> response = recruitRepository.getMyRecruits(employerId, null , RecruitPublishStatus.DRAFT, false, pageable)
                .map(MyDraftRecruitResponseDTO::from);

        return response;
    }

    public Page<RecruitmentApplyStatusDTO> getRecruitmentApplyStatus(Long employerId, Integer page){
        Pageable pageable = PageRequest.of(page, 8, Sort.by(Sort.Direction.DESC, "id"));

        Page<Recruit> byEmployerId = recruitRepository.findByEmployerId(employerId, pageable);
        List<Long> recruitIds=new ArrayList<>();
        List<Recruit> content = byEmployerId.getContent();
        for (Recruit recruit : content) {
            recruitIds.add(recruit.getId());
        }
        List<RecruitWithResumeCountDTO> recruitWithResumeCount = resumeRepository.findRecruitWithResumeCount(recruitIds);
        HashMap<Long, Long> resumeCount=new HashMap<>();
        for (RecruitWithResumeCountDTO recruitWithResumeCountDTO : recruitWithResumeCount) {
            resumeCount.put(recruitWithResumeCountDTO.getRecruitId(), recruitWithResumeCountDTO.getResumeCount());
        }

        Page<RecruitmentApplyStatusDTO> map = byEmployerId.map(recruit -> RecruitmentApplyStatusDTO.from(recruit, resumeCount.get(recruit.getId())));

        return map;
    }


    @Transactional
    public boolean flipRecruitBookmark(Long memberId, Long recruitId){
        Optional<RecruitBookmark> findBookmark = recruitBookmarkRepository.findByRecruitIdAndMemberId(recruitId, memberId);

        if(findBookmark.isPresent()){
            RecruitBookmark recruitBookmark = findBookmark.get();

            recruitBookmarkRepository.delete(recruitBookmark);

            return false;
        }
        else{
            Member member = memberRepository.findById(memberId).get();
            Recruit recruit = recruitRepository.findById(recruitId)
                    .orElseThrow(() -> {
                        log.error("공고 없음. recruitId= {}", recruitId);
                        return new BadRequestException(RECRUIT_NOT_FOUND_EXCEPTION.getMessage());
                    });


            RecruitBookmark recruitBookmark = new RecruitBookmark(recruit, member);

            recruitBookmarkRepository.save(recruitBookmark);

            return true;
        }
    }


    public Page<RecruitBookmarkResponseDTO> getMyRecruitBookmark(Long memberId, Integer page){
        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "id"));


        Page<RecruitBookmarkResponseDTO> response = recruitBookmarkRepository.findByMemberId(memberId, pageable)
                .map(RecruitBookmarkResponseDTO::from);

        return response;
    }

    public List<PortfolioResponseDTO> getPortfolios(Long recruitId){
        List<PortfolioResponseDTO> response = portfolioRepository.findByRecruitId(recruitId).stream()
                .map(PortfolioResponseDTO::from).toList();

        return response;
    }

}
