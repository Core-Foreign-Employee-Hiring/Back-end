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
    private String fileContractUrl;  // 고용인이 업로드한 파일
    private String rejectionReason; // 피고용인 반려 사유




    public void uploadContract(String fileContractUrl) {
        this.fileContractUrl = fileContractUrl;
    }


    public void rejectEmployer(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }


}
