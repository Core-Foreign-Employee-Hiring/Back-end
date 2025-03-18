package com.core.foreign.api.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileDirAndName {
    RecruitPostImage("recruit-post-image", "post_"),
    EmployerCompanyImage("employer-company-image", "company_"),
    EmployeeEnrollmentCertificateImage("employee-portfolio-image", "enrollment_certificate_"),
    EmployeeTranscriptImage("employee-portfolio-image", "transcript_"),
    EmployeePartTimeWorkPermitImage("employee-portfolio-image", "part_time_work_permit_"),
    FileContract("file-contract", "contract_"),
    PremiumRecruitPortfolio("premium-recruit", "portfolio_"),
    File("file","file_"),
    ;



    private final String dir;
    private final String fileName;
}
