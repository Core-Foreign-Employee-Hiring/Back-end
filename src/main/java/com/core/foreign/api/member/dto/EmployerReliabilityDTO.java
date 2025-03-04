package com.core.foreign.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployerReliabilityDTO {
    private Long approveCnt; // 모집 승인 개수
    private Long contractCompletedCnt; // 계약 완료 개수

    public Integer getReliability() {
        approveCnt = approveCnt == null ? 0 : approveCnt;
        contractCompletedCnt = contractCompletedCnt == null ? 0 : contractCompletedCnt;

        if (approveCnt == 0) {
            return 0;
        }

        if (contractCompletedCnt == 0) {
            return 0;
        }

        double reliability = ((double) contractCompletedCnt / approveCnt) * 100;

        if (reliability >= 100) {
            return 100;
        } else if (reliability >= 90) {
            return 90;
        } else if (reliability >= 70) {
            return 70;
        } else if (reliability >= 50) {
            return 50;
        } else {
            return 0;
        }
    }
}
