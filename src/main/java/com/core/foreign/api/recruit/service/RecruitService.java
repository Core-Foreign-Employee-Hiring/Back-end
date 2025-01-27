package com.core.foreign.api.recruit.service;

import com.core.foreign.api.aws.service.S3Service;
import com.core.foreign.api.member.entity.Member;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.api.recruit.dto.RecruitRequestDTO;
import com.core.foreign.api.recruit.entity.*;
import com.core.foreign.api.recruit.repository.RecruitRepository;
import com.core.foreign.api.recruit.dto.RecruitResponseDTO;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.exception.InternalServerException;
import com.core.foreign.common.exception.NotFoundException;
import com.core.foreign.common.response.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final RecruitRepository recruitRepository;
    private final MemberRepository memberRepository;
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
                .businessFields(new ArrayList<>(request.getBusinessFields()))
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
                .applicationMethods(new ArrayList<>(request.getApplicationMethods()))
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
        // 포스터 이미지 업로드
        String posterImageUrl = uploadPosterImage(posterImage);

        PremiumRecruit premiumRecruit = PremiumRecruit.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .employer(employer)
                .businessFields(new ArrayList<>(request.getBusinessFields()))
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
                .applicationMethods(new ArrayList<>(request.getApplicationMethods()))
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
                .businessFields(new ArrayList<>(request.getBusinessFields()))
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
                .applicationMethods(new ArrayList<>(request.getApplicationMethods()))
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

        // 임시 저장 한 데이터 삭제
        deleteDraft(employer);

        // 포스터 이미지 업로드
        String posterImageUrl = uploadPosterImage(posterImage);

        PremiumRecruit draftRecruit = PremiumRecruit.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .employer(employer)
                .businessFields(new ArrayList<>(request.getBusinessFields()))
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
                .applicationMethods(new ArrayList<>(request.getApplicationMethods()))
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
                .businessFields(new ArrayList<>(request.getBusinessFields()))
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
                .applicationMethods(new ArrayList<>(request.getApplicationMethods()))
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

        // 이전 데이터 삭제
        recruitRepository.delete(recruit);

        // 포스터 이미지 업로드
        String posterImageUrl = uploadPosterImage(posterImage);

        PremiumRecruit newRecruit = PremiumRecruit.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .employer(recruit.getEmployer())
                .businessFields(new ArrayList<>(request.getBusinessFields()))
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
                .applicationMethods(new ArrayList<>(request.getApplicationMethods()))
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
    }

    // 사용자 임시저장 데이터 조회
    @Transactional
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
}
