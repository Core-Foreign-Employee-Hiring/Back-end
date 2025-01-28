package com.core.foreign.api.file.service;

import com.core.foreign.api.aws.service.S3Service;
import com.core.foreign.api.file.FileDirAndName;
import com.core.foreign.api.member.repository.MemberRepository;
import com.core.foreign.common.exception.InternalServerException;
import com.core.foreign.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileService {
    private final S3Service s3Service;
    private final MemberRepository memberRepository;

    /**
     *
     *
     * 고용주의 회사 사진 업로드.
     */
    public void uploadCompanyImage(Long employerId, MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            try {
                // S3에 업로드.
                String url= s3Service.uploadImage(image, FileDirAndName.EmployerCompanyImage);
                log.info("[uploadImage][after]");

                // 고용주 정보 변경
                memberRepository.updateCompanyImage(employerId, url);
            } catch (IOException e) {
                throw new InternalServerException(ErrorStatus.FAIL_UPLOAD_EXCEPTION.getMessage());
            }
        }



    }
}
