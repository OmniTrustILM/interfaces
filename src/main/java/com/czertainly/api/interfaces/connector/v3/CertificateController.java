package com.czertainly.api.interfaces.connector.v3;

import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v3.certificate.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/v3/authorityProvider/certificates")
@Tag(name = "v3 Certificate Operations",
     description = "Stateless v3 certificate operations — every body carries authorityAttributes + raProfileAttributes")
public interface CertificateController {

    // ---- Issue ----

    @Operation(summary = "List dynamic attributes for issue (also used for renew, future rekey)")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Schema retrieved"))
    @PostMapping(path = "/issue/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listIssueAttributes(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Authority + RA-profile context used to scope the returned issue-attribute schema.",
                    required = true)
            @RequestBody CertificateAttributeListRequestDto request);

    @Operation(summary = "Issue a certificate (sync 200 or async 202)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Issued synchronously"),
        @ApiResponse(responseCode = "202", description = "Issuance accepted asynchronously; body carries meta tracking handle")
    })
    @PostMapping(path = "/issue", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CertificateDataResponseDto> issue(@RequestBody CertificateSignRequestDto body);

    @Operation(summary = "Get status of an async issue/renew/rekey operation")
    @PostMapping(path = "/issue/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateOperationStatusResponseDto getIssueStatus(@RequestBody CertificateOperationStatusRequestDto body);

    @Operation(summary = "Cancel an in-flight async issue/renew/rekey operation")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Aborted"),
        @ApiResponse(responseCode = "404", description = "Operation not tracked — treat as terminal cancellation"),
        @ApiResponse(responseCode = "422", description = "Refused — past point of no return")
    })
    @PostMapping(path = "/issue/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancelIssue(@RequestBody CertificateOperationCancelRequestDto body);

    // ---- Renew (status/cancel via /issue/*) ----

    @Operation(summary = "Renew a certificate (sync 200 or async 202)")
    @PostMapping(path = "/renew", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CertificateDataResponseDto> renew(@RequestBody CertificateRenewRequestDto body);

    // ---- Revoke ----

    @Operation(summary = "List dynamic attributes for revoke")
    @PostMapping(path = "/revoke/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listRevokeAttributes(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Authority + RA-profile context used to scope the returned revoke-attribute schema.",
                    required = true)
            @RequestBody CertificateAttributeListRequestDto request);

    @Operation(summary = "Revoke a certificate (sync 204 or async 202)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Revoked synchronously"),
        @ApiResponse(responseCode = "202", description = "Revocation accepted asynchronously; body carries meta tracking handle")
    })
    @PostMapping(path = "/revoke", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CertificateDataResponseDto> revoke(@RequestBody CertificateRevocationRequestDto body);

    @Operation(summary = "Get status of an async revoke operation")
    @PostMapping(path = "/revoke/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateOperationStatusResponseDto getRevokeStatus(@RequestBody CertificateOperationStatusRequestDto body);

    @Operation(summary = "Cancel an in-flight async revoke operation")
    @PostMapping(path = "/revoke/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancelRevoke(@RequestBody CertificateOperationCancelRequestDto body);

    // ---- Register ----

    @Operation(summary = "List dynamic attributes for register")
    @PostMapping(path = "/register/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listRegisterAttributes(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Authority + RA-profile context used to scope the returned register-attribute schema.",
                    required = true)
            @RequestBody CertificateAttributeListRequestDto request);

    @Operation(summary = "Pre-register a certificate's identity at the upstream CA (no CSR)")
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CertificateDataResponseDto> register(@RequestBody CertificateRegistrationRequestDto body);

    @Operation(summary = "Get status of an async register operation")
    @PostMapping(path = "/register/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateOperationStatusResponseDto getRegisterStatus(@RequestBody CertificateOperationStatusRequestDto body);

    @Operation(summary = "Cancel an in-flight async register operation")
    @PostMapping(path = "/register/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancelRegister(@RequestBody CertificateOperationCancelRequestDto body);

    // ---- Identify ----

    @Operation(summary = "Identify a certificate at the upstream CA (always synchronous)")
    @PostMapping(path = "/identify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateIdentificationResponseDto identify(@RequestBody CertificateIdentificationRequestDto body);
}
