package com.core.foreign.api.contract.dto;

import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.contract.entity.FileUploadContract;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.Resume;
import lombok.Getter;

import java.time.LocalDate;


@Getter
public class EmployeeCompletedContractResponseDTO {
    private String companyName;
    private LocalDate completedDate;
    private String contractUrl;



    public static EmployeeCompletedContractResponseDTO from(ContractMetadata contractMetadata) {
        EmployeeCompletedContractResponseDTO dto = new EmployeeCompletedContractResponseDTO();
        Resume resume = contractMetadata.getResume();
        Recruit recruit = resume.getRecruit();
        Employer employer = (Employer)recruit.getEmployer();

        dto.companyName = employer.getName();
        dto.completedDate = contractMetadata.getContractCompletionDate();

        // 이 부분 나중에 수정 필요.
        FileUploadContract contract = (FileUploadContract) contractMetadata.getContract();
        dto.contractUrl=contract.getEmployeeUploadedFile();


        return dto;
    }
}
