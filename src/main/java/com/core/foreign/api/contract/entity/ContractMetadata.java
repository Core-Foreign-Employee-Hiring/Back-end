package com.core.foreign.api.contract.entity;

import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.Resume;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
@Builder
@AllArgsConstructor(access = PROTECTED)
public class ContractMetadata {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name="contract_metadata_id")
    private Long id;

    @Enumerated(STRING)
    private ContractType contractType;

    @Enumerated(STRING)
    private ContractStatus employerContractStatus;
    @Enumerated(STRING)
    private ContractStatus employeeContractStatus;

    @Enumerated(STRING)
    private ContractStatus contractStatus;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name="resume_id")
    private Resume resume;

    @OneToOne(fetch= LAZY)
    @JoinColumn(name="recruit_id")
    private Recruit recruit;

    @OneToOne(fetch=LAZY)
    @JoinColumn(name="contract_id")
    private Contract contract;


    public void chooseContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }


    // 고용인이 파일 업로드
    public void uploadEmployerFile(String fileUrl) {
        FileUploadContract fileUploadContract = (FileUploadContract) contract;

        fileUploadContract.uploadEmployerFile(fileUrl);
        this.employerContractStatus=ContractStatus.PENDING_APPROVAL;
    }

    // 피고용인이 파일 업로드
    public void uploadEmployeeFile(String fileUrl) {
        FileUploadContract fileUploadContract = (FileUploadContract) contract;

        fileUploadContract.uploadEmployeeFile(fileUrl);
        this.employeeContractStatus=ContractStatus.PENDING_APPROVAL;
    }

}
