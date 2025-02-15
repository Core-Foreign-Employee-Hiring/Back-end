package com.core.foreign.api.member.dto;

import com.core.foreign.api.member.entity.Address;
import com.core.foreign.api.member.entity.Employee;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EmployeeBasicResumeResponseDTO {
    private Long employeeId;
    private String name;
    private String nationality;
    private String education;
    private String visa;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;

    private String zipcode;
    private String address1;
    private String address2;

    public static EmployeeBasicResumeResponseDTO from(Employee employee) {
        EmployeeBasicResumeResponseDTO dto = new EmployeeBasicResumeResponseDTO();
        dto.employeeId = employee.getId();
        dto.name = employee.getName();
        dto.nationality = employee.getNationality();
        dto.education = employee.getEducation();
        dto.visa = employee.getVisa();
        dto.birthDate=employee.getBirthday();
        dto.email = employee.getEmail();
        dto.phoneNumber = employee.getPhoneNumber();

        Address address = employee.getAddress();
        dto.zipcode = address.getZipcode();
        dto.address1 = address.getAddress1();
        dto.address2 = address.getAddress2();
        return dto;

    }
}
