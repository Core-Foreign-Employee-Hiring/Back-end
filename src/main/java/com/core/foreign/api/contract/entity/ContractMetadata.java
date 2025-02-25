package com.core.foreign.api.contract.entity;

import com.core.foreign.api.recruit.entity.Resume;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    @OneToOne(fetch=LAZY)
    @JoinColumn(name="contract_id")
    private Contract contract;

    private LocalDate contractCompletionDate; // 계약서 체결일 추가

    public void chooseContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }


    //  파일 업로드
    public void uploadContract(String fileContractUrl) {
        FileUploadContract fileUploadContract = (FileUploadContract) contract;

        fileUploadContract.uploadContract(fileContractUrl);
        this.employerContractStatus=ContractStatus.PENDING_APPROVAL;
        this.employeeContractStatus=ContractStatus.PENDING_APPROVAL;
    }


    public void completeFileUploadContract(){

        this.employeeContractStatus=ContractStatus.APPROVED;
        this.employerContractStatus=ContractStatus.APPROVED;

        completeContract();
    }


    private void completeContract(){
        this.contractStatus=ContractStatus.COMPLETED;
        this.contractCompletionDate=LocalDate.now();
        resume.completeContract();
    }


}
