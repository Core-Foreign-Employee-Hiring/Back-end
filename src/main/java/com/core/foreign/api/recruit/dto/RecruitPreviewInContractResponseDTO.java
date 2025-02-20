package com.core.foreign.api.recruit.dto;

import com.core.foreign.api.business_field.BusinessField;
import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.recruit.entity.ApplyMethod;
import com.core.foreign.api.recruit.entity.Recruit;
import lombok.Getter;

import java.util.List;

@Getter
public class RecruitPreviewInContractResponseDTO {
    private Long recruitId;
    private List<BusinessField> businessFields;
    private String zipcode;  // 우편번호
    private String address1; // 주소
    private String address2; // 상세 주소
    private String salary; // 급여 정보
    private List<ApplyMethod> applyMethods;



    public static RecruitPreviewInContractResponseDTO from(Recruit recruit){
        RecruitPreviewInContractResponseDTO dto = new RecruitPreviewInContractResponseDTO();
        Address address = recruit.getAddress();

        dto.recruitId = recruit.getId();
        dto.businessFields=recruit.getBusinessFields().stream().toList();

        dto.zipcode=address.getZipcode();
        dto.address1=address.getAddress1();
        dto.address2=address.getAddress2();
        dto.salary=recruit.getSalary();

        dto.applyMethods=recruit.getApplicationMethods().stream().toList();

        return dto;

    }

}
