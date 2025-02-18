package com.core.foreign.api.contract.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("FILE")
@Getter
@NoArgsConstructor()
public class FileUploadContract extends Contract{
    private String employerUploadedFile;  // 고용인이 업로드한 파일
    private String employeeUploadedFile;    // 피고용인이 업로드한 파일

    private String employerRejectionReason; // 고용인 반려 사유
    private String employeeRejectionReason; // 피고용인 반려 사유



    // 고용인이 파일 업로드
    public void uploadEmployerFile(String fileUrl) {
        this.employerUploadedFile = fileUrl;
    }

    // 피고용인이 파일 업로드
    public void uploadEmployeeFile(String fileUrl) {
        this.employeeUploadedFile = fileUrl;
    }

    public void rejectEmployer(String rejectionReason) {
        this.employeeRejectionReason = rejectionReason;
    }

    public void rejectEmployee(String rejectionReason) {
        this.employeeRejectionReason = rejectionReason;
    }

}
