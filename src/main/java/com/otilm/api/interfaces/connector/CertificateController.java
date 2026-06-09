package com.otilm.api.interfaces.connector;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.core.authority.CertRevocationDto;
import com.otilm.api.model.core.authority.CertificateSignRequestDto;
import com.otilm.api.model.core.authority.CertificateSignResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/v1/authorityProvider/authorities/{uuid}/endEntityProfiles/{endEntityProfileName}/certificates")
@Tag(name = "Certificate Management", description = "Certificate Management API")
public interface CertificateController extends AuthProtectedConnectorController {

    @Operation(
            summary = "Issue Certificate"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate issued"
                    )
            })
    @PostMapping(path = "/issue", consumes = {"application/json"}, produces = {"application/json"})
    CertificateSignResponseDto issueCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @Parameter(description = "End Entity Profile Name") @PathVariable String endEntityProfileName,
            @RequestBody CertificateSignRequestDto request) throws NotFoundException;

    @Operation(
            summary = "Revoke Certificate"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate revoked"
                    )
            })
    @PostMapping(path = "/revoke", consumes = {"application/json"}, produces = {"application/json"})
    void revokeCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @Parameter(description = "End Entity Profile Name") @PathVariable String endEntityProfileName,
            @RequestBody CertRevocationDto request) throws NotFoundException;

}