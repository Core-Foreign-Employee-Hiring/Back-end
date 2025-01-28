package com.core.foreign.api.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileDirAndName {
    RecruitPostImage("recruit-post-image", "post_"),
    EmployerCompanyImage("employer-company-image", "company_"),;



    private final String dir;
    private final String fileName;
}
