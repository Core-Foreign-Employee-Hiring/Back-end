package com.core.foreign.api.contract.controller;

import com.core.foreign.api.contract.dto.AdminContractPreviewResponseDTO;
import com.core.foreign.api.contract.dto.ContractPreviewResponseDTO;
import com.core.foreign.api.contract.entity.ContractStatus;
import com.core.foreign.api.contract.entity.ContractType;
import com.core.foreign.api.contract.service.ContractService;
import com.core.foreign.api.file.dto.FileUrlAndOriginalFileNameDTO;
import com.core.foreign.api.file.dto.request.Urls;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Contract", description = "Contract 관련 API 입니다.")
@RestController
@RequestMapping("/api/v1/contract")
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;


    @Operation(
            summary = "미완료된 계약서 조회 API (용범)",
            description = "미완료된 계약서 조회. <br>" +
                    "<p>" +
                    "호출 필드 정보)<br>" +
                    "page: 페이지 번호(예시: 1)<br>" +
                    "size: 한 페이지에 최대 몇 개?(예시: 5)>" +
                    "<p>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/not-completed")
    public ResponseEntity<ApiResponse<PageResponseDTO<ContractPreviewResponseDTO>>> getNotCompletedContract(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                            @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                            @RequestParam("size") Integer size) {
        PageResponseDTO<ContractPreviewResponseDTO> contractMetadataOfEmployee = contractService.getNotCompletedContractMetadata(securityMember.getRole(), securityMember.getId(), page, size);

        return ApiResponse.success(SuccessStatus.INCOMPLETE_CONTRACT_VIEW_SUCCESS, contractMetadataOfEmployee);
    }


    @Operation(
            summary = "계약서 형태 선택 API (용범)",
            description = "고용인이 계약서 형태를 선택합니다. <br>" +
                    "<p>" +
                    "호출 필드 정보)<br>" +
                    "contract-id: 계약서 ID(예시: 1)<br>" +
                    "contractType: 계약서 형태(예시: IMAGE_UPLOAD)" +
                    "<p>" +
                    "ENUM 정보: <a href=\"https://www.notion.so/enum-1bc244b92af28155acb1cfb57edb4fd3\" target=\"_blank\">이동하기</a>"
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


    @Operation(summary = "실물 계약서 최소 업로드 API (용범)",
            description = "실물 계약서 최소 업로드합니다. <br>" +
                    "<p>" +
                    "호출 필드 정보)<br>" +
                    "contract-id: 계약서 ID(예시: 1)<br>" +
                    "urls: 파일 링크들" +
                    "<p>" +
                    "요청 예시: <a href=\"https://www.notion.so/api-v1-contract-contract-id-file-19e98c9adc8a80eb85c4c44cfd49cad8?pvs=4\" target=\"_blank\">이동하기</a>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/{contract-id}/file")
    public ResponseEntity<ApiResponse<Void>> uploadFileContract(@AuthenticationPrincipal SecurityMember securityMember,
                                                                @PathVariable("contract-id") Long contractMetadataId,
                                                                @RequestBody Urls urls) {
        contractService.uploadFileContract(securityMember.getId(), contractMetadataId, urls.getUrls());

        return ApiResponse.success_only(SuccessStatus.CONTRACT_UPLOAD_SUCCESS);
    }

    @Operation(summary = "실물 계약서 수정 API (용범)",
            description = "실물 계약서 수정합니다. <br>" +
                    "<p>" +
                    "호출 필드 정보)<br>" +
                    "contract-id: 계약서 ID(예시: 1)<br>" +
                    "urls: 파일 링크들" +
                    "<p>" +
                    "요청 예시: <a href=\"https://www.notion.so/api-v1-contract-contract-id-file-1a598c9adc8a8007912fc461f213baa8?pvs=4\" target=\"_blank\">이동하기</a>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "업로드 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PatchMapping(value = "/{contract-id}/file")
    public ResponseEntity<ApiResponse<Void>> modifyFileContract(@AuthenticationPrincipal SecurityMember securityMember,
                                                                @PathVariable("contract-id") Long contractMetadataId,
                                                                @RequestBody Urls urls) {
        contractService.uploadFileContract(securityMember.getId(), contractMetadataId, urls.getUrls());

        return ApiResponse.success_only(SuccessStatus.CONTRACT_UPLOAD_SUCCESS);
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
        contractService.approveOrRejectFileUploadContract(contractMetadataId, ContractStatus.APPROVED, null);

        return ApiResponse.success_only(SuccessStatus.CONTRACT_UPLOAD_SUCCESS);
    }

    @Operation(summary = "테스트 :: 파일 업로드 계약서 거절. API",
            description = "테스트 :: 파일 업로드 계약서 거절. <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "거절 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping(value = "/test/reject-contract")
    public ResponseEntity<ApiResponse<Void>> test_rejectContract(@AuthenticationPrincipal SecurityMember securityMember,
                                                                  @RequestParam("contractMetadataId") Long contractMetadataId,
                                                                 @RequestParam("rejectionReason") String rejectionReason) {
        contractService.approveOrRejectFileUploadContract(contractMetadataId, ContractStatus.REJECTED, rejectionReason);

        return ApiResponse.success_only(SuccessStatus.CONTRACT_UPLOAD_SUCCESS);
    }

    @Operation(summary = "테스트 :: 관리자 미완료 계약서 조회. API",
            description = "테스트 :: 관리자 미완료 계약서 조회. <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/test/not-completed")
    public ResponseEntity<ApiResponse<PageResponseDTO<AdminContractPreviewResponseDTO>>> test_getNotCompleteFileUploadContractMetadata(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                                                       @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                                                                                       @RequestParam("size") Integer size) {
        size = size == null ? 3 : size;

        PageResponseDTO<AdminContractPreviewResponseDTO> response = contractService.getNotCompleteFileUploadContractMetadata(page, size);

        return ApiResponse.success(SuccessStatus.CONTRACT_UPLOAD_SUCCESS, response);
    }

    @Operation(summary = "테스트 :: 관리자 미완료 계약서 상세 조회. API",
            description = "테스트 :: 관리자 미완료 계약서 상세 조회. <br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @GetMapping(value = "/test/not-completed-one")
    public ResponseEntity<ApiResponse<List<FileUrlAndOriginalFileNameDTO>>> test_getNotCompleteFileUploadContractMetadata(@AuthenticationPrincipal SecurityMember securityMember,
                                                                                                                          @RequestParam("contractId")Long contractMetadataId) {

        List<FileUrlAndOriginalFileNameDTO> response = contractService.getNotFileUploadCompleteContractMetadata(contractMetadataId);

        return ApiResponse.success(SuccessStatus.CONTRACT_UPLOAD_SUCCESS, response);
    }


}
