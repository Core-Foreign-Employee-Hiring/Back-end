package com.core.foreign.api.contract.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("FILE")
@Getter
@NoArgsConstructor
public class FileUploadContract extends Contract{
    private String rejectionReason;
    private Long curVersion=0L;
    private Long adminViewVersion;


    public void synchronizeAdminViewVersion() {
        this.adminViewVersion = curVersion;
    }

    public void reject(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void incrementVersion() {
        this.curVersion++;
    }


}
