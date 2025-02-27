package com.core.foreign.api.contract.dto;

import com.core.foreign.api.contract.entity.ContractMetadata;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Employee;
import com.core.foreign.api.member.entity.Employer;
import com.core.foreign.api.recruit.entity.Recruit;
import lombok.Getter;

@Getter
public class AdminContractPreviewResponseDTO {
    private Long contractId;  // 사실 contractMetadataId
    private String companyName;
    private String recruitTitle;
    private String employeeName;
    private String employerName;
    private String companyZipcode;
    private String companyAddress1;
    private String companyAddress2;


    public static AdminContractPreviewResponseDTO from(ContractMetadata contractMetadata) {
        AdminContractPreviewResponseDTO dto = new AdminContractPreviewResponseDTO();
        Recruit recruit = contractMetadata.getResume().getRecruit();
        Employer employer = (Employer)recruit.getEmployer();
        Employee employee = contractMetadata.getResume().getEmployee();
        Address companyAddress = employer.getCompanyAddress();

        dto.contractId = contractMetadata.getId();
        dto.companyName = employer.getCompanyName();
        dto.recruitTitle=recruit.getTitle();
        dto.employeeName = employee.getName();
        dto.employerName = employer.getName();
        dto.companyZipcode=companyAddress.getZipcode();
        dto.companyAddress1=companyAddress.getAddress1();
        dto.companyAddress2=companyAddress.getAddress2();

        return dto;
    }

}
