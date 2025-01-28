package com.core.foreign.api.member.service;

import com.core.foreign.api.member.entity.CompanyValidation;
import com.core.foreign.api.member.repository.CompanyValidationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyValidationService {
    @Value("${business.secretKey}")
    private String serviceKey;

    private final CompanyValidationRepository companyValidationRepository;


    public boolean isCompanyValidate(String businessNo, String startDate, String representativeName) {
        Map<String, Object> result = new HashMap<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            String decodedServicekey = URLDecoder.decode(serviceKey, "UTF-8");
            String url = "https://api.odcloud.kr/api/nts-businessman/v1/validate?serviceKey=" + decodedServicekey;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // 요청 데이터 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("businesses", Collections.singletonList(Map.of(
                    "b_no", businessNo,
                    "start_dt", startDate,
                    "p_nm", representativeName,
                    "p_nm2", "",
                    "b_nm", "",
                    "corp_no", "",
                    "b_sector", "",
                    "b_type", "",
                    "b_adr", ""
            )));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // API 호출
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            // 응답 결과 처리
            result = response.getBody();

            // 응답 데이터가 존재하는지 확인
            if (result != null && result.containsKey("data")) {
                // data는 List로 처리해야 한다.
                List<Map<String, Object>> dataList = (List<Map<String, Object>>) result.get("data");
                if (!dataList.isEmpty()) {
                    Map<String, Object> data = dataList.get(0); // 첫 번째 데이터 가져오기
                    String valid = (String) data.get("valid");

                    // valid 값을 기반으로 성공/실패 여부 판단
                    if ("01".equals(valid)) {
                        // 성공적인 경우

                        // dp 저장.

                        companyValidationRepository.delete(businessNo, startDate, representativeName);

                        CompanyValidation companyValidation = new CompanyValidation(businessNo, startDate, representativeName);
                        companyValidationRepository.save(companyValidation);

                        return true;
                    } else {
                        log.info("invalid company validation");
                        return false;
                    }
                } else {
                    log.info("data가 비어있습니다.");
                    return false;
                }
            } else {
                log.info("응답 데이터가 없습니다.");
                return false;
            }

        } catch (HttpClientErrorException e) {
            // HTTP 오류 발생 시 로그만 찍기
            log.error("HTTP error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (UnsupportedEncodingException e) {
            // 서비스 키 디코딩 오류 시 로그만 찍기
            log.error("Encoding error: {}", e.getMessage());
        } catch (Exception e) {
            // 기타 예외 처리 시 로그만 찍기
            log.error("Unexpected error: {}", e.getMessage());
        }

        return false;
    }

}
