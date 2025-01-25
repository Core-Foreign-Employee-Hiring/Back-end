package com.core.foreign.api.member.dto;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Employer;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class EmployerCompanyInfoResponseDTO {

    private String companyName;
    private String name;  // 일단, Member 의 name 으로 할 것.
    private String businessRegistrationNumber;
    private LocalDate establishedDate;
    private List<BusinessField> businessFields;

    private String zipcode;
    private String address1;
    private String address2;

    private String companyEmail;
    private String mainPhoneNumber;


    public static EmployerCompanyInfoResponseDTO from(Employer employer, List<BusinessField> businessFields){
        EmployerCompanyInfoResponseDTO dto = new EmployerCompanyInfoResponseDTO();
        Address address = employer.getAddress();

        dto.companyName = employer.getCompanyName();
        dto.name = employer.getName();
        dto.businessRegistrationNumber = employer.getBusinessRegistrationNumber();
        dto.establishedDate = employer.getEstablishedDate();
        dto.businessFields = businessFields;
        dto.zipcode = address.getZipcode();
        dto.address1 = address.getAddress1();
        dto.address2 = address.getAddress2();
        dto.companyEmail = employer.getCompanyEmail();
        dto.mainPhoneNumber = employer.getMainPhoneNumber();
        return dto;
    }

}
