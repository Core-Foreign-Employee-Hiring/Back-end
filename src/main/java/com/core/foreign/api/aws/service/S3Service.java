package com.core.foreign.api.aws.service;

import com.core.foreign.api.file.FileDirAndName;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName; // S3 버킷 이름

    @Value("${cloud.aws.s3.url}")
    private String s3Domain; // S3 도메인

    // 채용 공고 포스터 이미지 업로드 메서드
    public String uploadRecruitPostImage(MultipartFile file) throws IOException {
        String dir = "recruit-post-image";
        // 현재 날짜/시간
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        // 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 파일명 예시: post_2025-01-26_19:32:12.jpg
        String fileName = "post_" + currentDateTime + extension;
        // 난수 문자열
        String randomString = RandomStringUtils.randomAlphanumeric(16);
        // 최종 S3 경로
        String fileKey = dir + "/" + randomString + "/" + fileName;

        // 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .acl("public-read")
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // 업로드 후 S3 도메인 + 경로 형태의 URL 반환
        return s3Domain + "/" + fileKey;
    }

    // 이미지 업로드 메서드
    public String uploadImage(MultipartFile file, FileDirAndName dirAndName) throws IOException {
        String dir = dirAndName.getDir();
        // 현재 날짜/시간
        String currentDateTime = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
        // 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 파일명 예시: post_2025-01-26_19:32:12.jpg
        String fileName = dirAndName.getFileName() + currentDateTime + extension;
        // 난수 문자열
        String randomString = RandomStringUtils.randomAlphanumeric(16);
        // 최종 S3 경로
        String fileKey = dir + "/" + randomString + "/" + fileName;

        // 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .acl("public-read")
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // 업로드 후 S3 도메인 + 경로 형태의 URL 반환
        return s3Domain + "/" + fileKey;
    }

    // 파일 삭제 메서드
    public void deleteFile(String imageUrl) {
        if (imageUrl != null && imageUrl.startsWith(s3Domain)) {
            // S3 도메인을 제외한 key 추출
            String fileKey = imageUrl.replace(s3Domain + "/", "");
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        }
    }
}
