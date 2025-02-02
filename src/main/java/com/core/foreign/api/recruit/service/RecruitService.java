package com.core.foreign.api.recruit.service;

import com.core.foreign.api.aws.service.S3Service;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.recruit.dto.*;
import org.springframework.data.jpa.domain.Specification;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.recruit.entity.*;
import com.core.foreign.api.recruit.repository.PremiumManageRepository;
import com.core.foreign.api.recruit.repository.RecruitRepository;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.InternalServerException;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final RecruitRepository recruitRepository;
    private final MemberRepository memberRepository;
    private final PremiumManageRepository premiumManageRepository;
    private final S3Service s3Service;

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
                .recruitCount(request.getRecruitCount())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(request.getWorkDuration())
                .workTime(request.getWorkTime())
                .workDays(request.getWorkDays())
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
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
                .recruitCount(request.getRecruitCount())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(request.getWorkDuration())
                .workTime(request.getWorkTime())
                .workDays(request.getWorkDays())
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
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
                        .maxFileSize(p.getMaxFileSize())
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
                .recruitCount(request.getRecruitCount())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(request.getWorkDuration())
                .workTime(request.getWorkTime())
                .workDays(request.getWorkDays())
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
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

        // 프리미엄 공고 등록 가능 체크
        PremiumManage premiumManage = premiumManageRepository.findByEmployerId(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.PREMIUM_MANAGE_NOT_FOUND_EXCEPTION.getMessage()));

        if (premiumManage.getPremiumCount() == 0) {
            throw new BadRequestException(ErrorStatus.LEAK_PREMIUM_RECRUIT_PUBLISH_COUNT_EXCEPTION.getMessage());
        }

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
                .recruitCount(request.getRecruitCount())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(request.getWorkDuration())
                .workTime(request.getWorkTime())
                .workDays(request.getWorkDays())
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
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
                        .maxFileSize(p.getMaxFileSize())
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
                .recruitCount(request.getRecruitCount())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(request.getWorkDuration())
                .workTime(request.getWorkTime())
                .workDays(request.getWorkDays())
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
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
                .recruitCount(request.getRecruitCount())
                .gender(request.getGender())
                .education(request.getEducation())
                .otherConditions(request.getOtherConditions())
                .preferredConditions(new ArrayList<>(request.getPreferredConditions()))
                .workDuration(request.getWorkDuration())
                .workTime(request.getWorkTime())
                .workDays(request.getWorkDays())
                .workDaysOther(request.getWorkDaysOther())
                .salary(request.getSalary())
                .salaryType(request.getSalaryType())
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
                        .maxFileSize(p.getMaxFileSize())
                        .build())
                .toList();
        portfolios.forEach(newRecruit::addPortfolio);
        recruitRepository.save(newRecruit);

        // 프리미엄 공고 등록 횟수 감소
        PremiumManage updatedPremiumManage = premiumManage.decreasePremiumCount();
        premiumManageRepository.save(updatedPremiumManage);
    }

    // 사용자 임시저장 데이터 조회
    @Transactional(readOnly = true)
    public List<RecruitResponseDTO> getDrafts(Long memberId) {
        Member employer = getEmployer(memberId);
        Optional<Recruit> drafts = recruitRepository.findAllByEmployerAndRecruitPublishStatus(employer, RecruitPublishStatus.DRAFT);

        return drafts.stream()
                .map(draft -> {
                    List<RecruitResponseDTO.PortfolioDTO> portfolioDTOs = new ArrayList<>();
                    if (draft instanceof PremiumRecruit premiumRecruit) {
                        portfolioDTOs = premiumRecruit.getPortfolios().stream()
                                .map(portfolio -> RecruitResponseDTO.PortfolioDTO.builder()
                                        .title(portfolio.getTitle())
                                        .type(portfolio.getType())
                                        .isRequired(portfolio.isRequired())
                                        .maxFileCount(portfolio.getMaxFileCount())
                                        .maxFileSize(portfolio.getMaxFileSize())
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
                            .recruitCount(draft.getRecruitCount())
                            .gender(draft.getGender())
                            .education(draft.getEducation())
                            .otherConditions(draft.getOtherConditions())
                            .preferredConditions(draft.getPreferredConditions())
                            .workDuration(draft.getWorkDuration())
                            .workTime(draft.getWorkTime())
                            .workDays(draft.getWorkDays())
                            .workDaysOther(draft.getWorkDaysOther())
                            .salary(draft.getSalary())
                            .salaryType(draft.getSalaryType())
                            .businessFields(draft.getBusinessFields())
                            .applicationMethods(draft.getApplicationMethods())
                            .posterImageUrl(draft.getPosterImageUrl())
                            .latitude(draft.getLatitude())
                            .longitude(draft.getLongitude())
                            .portfolios(portfolioDTOs)
                            .build();
                })
                .toList();
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
                .orElseThrow(() -> new NotFoundException(ErrorStatus.RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));
    }

    // 프리미엄 공고 조회
    private PremiumRecruit getPremiumRecruit(Long recruitId) {
        return (PremiumRecruit) recruitRepository.findById(recruitId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));
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
                .orElseThrow(() -> new NotFoundException(ErrorStatus.RECRUIT_NOT_FOUND_EXCEPTION.getMessage()));

        String companyName;
        String companyIconImage;
        Address employerAddress = null;
        String employerContact = null;
        String representative = null;
        String employerEmail = null;
        String businessRegistrationNumber = null;

        Member employer = recruit.getEmployer();
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
                .recruitCount(recruit.getRecruitCount())
                .gender(recruit.getGender())
                .education(recruit.getEducation())
                .otherConditions(recruit.getOtherConditions())
                .preferredConditions(recruit.getPreferredConditions())
                .businessFields(recruit.getBusinessFields())
                .applicationMethods(recruit.getApplicationMethods())
                .workDuration(recruit.getWorkDuration())
                .workTime(recruit.getWorkTime())
                .workDays(recruit.getWorkDays())
                .workDaysOther(recruit.getWorkDaysOther())
                .salary(recruit.getSalary())
                .salaryType(recruit.getSalaryType())
                .latitude(recruit.getLatitude())
                .longitude(recruit.getLongitude())
                .posterImageUrl(recruit.getPosterImageUrl())
                .recruitType(recruit.getRecruitType())
                .employerAddress(employerAddress)
                .employerContact(employerContact)
                .representative(representative)
                .employerEmail(employerEmail)
                .businessRegistrationNumber(businessRegistrationNumber)
                .build();
    }

}
