package com.core.foreign.api.contract.dto;

import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.contract.entity.ContractType;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.Resume;
import lombok.Getter;

import java.time.LocalDate;


@Getter
public class EmployeeCompletedContractResponseDTO {
    private Long contractId;
    private ContractType contractType;
    private String companyName;
    private LocalDate completedDate;

    public static EmployeeCompletedContractResponseDTO from(ContractMetadata contractMetadata) {
        EmployeeCompletedContractResponseDTO dto = new EmployeeCompletedContractResponseDTO();
        Resume resume = contractMetadata.getResume();
        Recruit recruit = resume.getRecruit();
        Employer employer = (Employer)recruit.getEmployer();

        dto.contractId = contractMetadata.getId();
        dto.contractType = contractMetadata.getContractType();
        dto.companyName = employer.getName();
        dto.completedDate = contractMetadata.getContractCompletionDate();

        return dto;
    }
}
