package com.core.foreign.api.contract.dto;

import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.contract.entity.ContractType;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.recruit.entity.Recruit;
import com.core.foreign.api.recruit.entity.Resume;
import lombok.Getter;

@Getter
public class ContractPreviewResponseDTO {

    private Long contractId;  // 사실 contractMetadataId 임.

    private String recruitTitle; // 공고 제목

    private ContractType contractType;

    private String employeeName;
    private ContractStatus employeeContractStatus;
    private boolean employeeSign;

    private String companyName;
    private ContractStatus companyContractStatus;
    private boolean companySign;


    public static ContractPreviewResponseDTO from(ContractMetadata contractMetadata) {
        ContractPreviewResponseDTO dto = new ContractPreviewResponseDTO();

        Resume resume = contractMetadata.getResume();
        Employee employee = resume.getEmployee();

        Recruit recruit = resume.getRecruit();
        Employer employer = (Employer)recruit.getEmployer();

        dto.contractId = contractMetadata.getId();

        dto.recruitTitle = recruit.getTitle();
        dto.contractType = contractMetadata.getContractType();

        dto.employeeName = employee.getName();
        dto.employeeContractStatus=contractMetadata.getEmployeeContractStatus();
        dto.employeeSign=false;

        dto.companyName = employer.getCompanyName();
        dto.companyContractStatus=contractMetadata.getEmployerContractStatus();
        dto.companySign=false;

        return dto;
    }

}
