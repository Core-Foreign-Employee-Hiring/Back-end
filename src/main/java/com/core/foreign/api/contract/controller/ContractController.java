package com.core.foreign.api.contract.controller;

import com.core.foreign.api.contract.dto.ContractPreviewResponseDTO;
import com.core.foreign.api.contract.entity.ContractType;
import com.core.foreign.api.contract.service.ContractService;
import com.core.foreign.api.member.entity.Role;
import com.core.foreign.api.recruit.dto.PageResponseDTO;
import com.core.foreign.common.SecurityMember;
import com.core.foreign.common.exception.BadRequestException;
import com.core.foreign.common.response.ApiResponse;
import com.core.foreign.common.response.ErrorStatus;
import com.core.foreign.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Contract", description = "Contract 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/contract")
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;


    @Operation(summary = "미완료된 계약서 조회. API",
            description = "미완료된 계약서 조회. <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/not-completed")
    public ResponseEntity<ApiResponse<PageResponseDTO<ContractPreviewResponseDTO>>> getNotCompletedContract(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                 @RequestParam(value = "page", defaultValue = "0")Integer page) {
        PageResponseDTO<ContractPreviewResponseDTO> contractMetadataOfEmployee = contractService.getNotCompletedContractMetadata(securityMember.getRole(), securityMember.getId(), page);

        return ApiResponse.success(SuccessStatus.INCOMPLETE_CONTRACT_VIEW_SUCCESS, contractMetadataOfEmployee);
    }


    @Operation(summary = "계약서 형태 선택. API",
            description = "계약서 형태 선택. API <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "선택 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/{contract-id}/type")
    public ResponseEntity<ApiResponse<Void>> chooseContractType(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                @PathVariable("contract-id") Long contractMetadataId,
                                                                                @RequestParam("contractType") ContractType contractType) {

        if(securityMember.getRole().equals(Role.EMPLOYEE)){
            throw new BadRequestException(ErrorStatus.EMPLOYEE_CANNOT_SELECT_CONTRACT_TYPE_EXCEPTION.getMessage());
        }

        if(contractType.equals(ContractType.UNKNOWN)){
            throw new BadRequestException(ErrorStatus.CONTRACT_TYPE_SELECTION_REQUIRED_EXCEPTION.getMessage());
        }



        contractService.chooseContractType(securityMember.getId(), contractMetadataId, contractType);

        return ApiResponse.success_only(SuccessStatus.CONTRACT_TYPE_SELECTION_SUCCESS);
    }


    @Operation(summary = "실물 계약서 최소 업로드. API",
            description = "실물 계약서 최소 업로드. API <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/{contract-id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadFileContract(@AuthenticationPrincipal SecurityMember securityMember,
                                                                @PathVariable("contract-id") Long contractMetadataId,
                                                                @RequestPart(value = "contract", required = false) MultipartFile contract) {
        String url = contractService.uploadFileContract(securityMember.getId(), contractMetadataId, contract);

        return ApiResponse.success(SuccessStatus.CONTRACT_UPLOAD_SUCCESS, url);
    }

    @Operation(summary = "실물 계약서 수정. API",
            description = "실물 계약서 수정. API <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping(value = "/{contract-id}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> modifyFileContract(@AuthenticationPrincipal SecurityMember securityMember,
                                                                  @PathVariable("contract-id") Long contractMetadataId,
                                                                  @RequestPart(value = "contract", required = false) MultipartFile contract) {
        String url = contractService.uploadFileContract(securityMember.getId(), contractMetadataId, contract);

        return ApiResponse.success(SuccessStatus.CONTRACT_UPLOAD_SUCCESS, url);
    }


    @Operation(summary = "테스트 :: 파일 업로드 계약서 승인. API",
            description = "테스트 :: 파일 업로드 계약서 승인. <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "승인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/test/approve-contract")
    public ResponseEntity<ApiResponse<Void>> test_approveContract(@AuthenticationPrincipal SecurityMember securityMember,
                                                                  @RequestParam("contractMetadataId") Long contractMetadataId) {
        contractService.test_approveContract(contractMetadataId);

        return ApiResponse.success_only(SuccessStatus.CONTRACT_UPLOAD_SUCCESS);
    }
}
