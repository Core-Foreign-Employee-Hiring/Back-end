package com.core.foreign.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Check", description = "Health Check 관련 API 입니다.")
@RestController
public class HealthCheckController {

    @Operation(
            summary = "Health Check API",
            description = "서버와의 응답이 정상인지 확인하는 API입니다."
    )
    @GetMapping("health")
    public String healthCheck(){
        return "OK";
    }

}