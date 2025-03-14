package com.core.foreign.api.file.controller;

import com.core.foreign.api.file.FileDirAndName;
import com.core.foreign.api.file.service.FileService;
import com.core.foreign.common.SecurityMember;
import com.core.foreign.common.response.ApiResponse;
import com.core.foreign.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "File", description = "File 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;


    @Operation(summary = "피고용인 재학증명서 업로드. API",
            description = "피고용인의 재학증명서를 업로드 합니다<br>." +
                    "파일 저장소에만 저장합니다.<br>" +
                    " 파일의 url 을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value="/employee/enrollment-certificate-image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadEnrollmentCertificateImage(@AuthenticationPrincipal SecurityMember securityMember,
                                                                @RequestPart(value = "enrollmentCertificateImage", required = false) MultipartFile enrollmentCertificateImage) {
        String url = fileService.uploadOnlyS3(enrollmentCertificateImage, FileDirAndName.EmployeeEnrollmentCertificateImage);

        return ApiResponse.success(SuccessStatus.UPLOAD_IMAGE_SUCCESS, url);
    }

    @Operation(summary = "피고용인 성적증명서 업로드. API",
            description = "피고용인의 성적증명서를 업로드 합니다<br>." +
                    "파일 저장소에만 저장합니다.<br>" +
                    " 파일의 url 을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value="/employee/transcript-image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadTranscriptImage(@AuthenticationPrincipal SecurityMember securityMember,
                                                                @RequestPart(value = "transcriptImage", required = false) MultipartFile transcriptImage) {
        String url = fileService.uploadOnlyS3(transcriptImage, FileDirAndName.EmployeeTranscriptImage);

        return ApiResponse.success(SuccessStatus.UPLOAD_IMAGE_SUCCESS, url);
    }

    @Operation(summary = "피고용인 시간제근로 허가서 업로드. API",
            description = "피고용인의 시간제근로 허가서를 업로드 합니다<br>." +
                    "파일 저장소에만 저장합니다.<br>" +
                    " 파일의 url 을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value="/employee/part-time-work-permit-image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadPartTimeWorkPermitImage(@AuthenticationPrincipal SecurityMember securityMember,
                                                                @RequestPart(value = "partTimeWorkPermitImage", required = false) MultipartFile partTimeWorkPermitImage) {
        String url = fileService.uploadOnlyS3(partTimeWorkPermitImage, FileDirAndName.EmployeePartTimeWorkPermitImage);

        return ApiResponse.success(SuccessStatus.UPLOAD_IMAGE_SUCCESS, url);
    }


    @Operation(summary = "프리미엄 공고 지원 시 파일 업로드. API",
            description = "프리미엄 공고 지원 시 파일 업로드 합니다<br>." +
                    "파일 저장소에만 저장합니다.<br>" +
                    " 파일의 url 을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/premium-recruit/portfolio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadPremiumRecruitPortfolio(@AuthenticationPrincipal SecurityMember securityMember,
                                                                        @RequestPart(value = "file", required = false) MultipartFile file) {
        String url = fileService.uploadOnlyS3(file, FileDirAndName.PremiumRecruitPortfolio);

        return ApiResponse.success(SuccessStatus.UPLOAD_IMAGE_SUCCESS, url);
    }


}
