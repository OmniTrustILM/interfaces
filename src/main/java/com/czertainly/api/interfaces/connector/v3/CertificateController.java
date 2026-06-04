package com.czertainly.api.interfaces.connector.v3;

import com.czertainly.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v3.certificate.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/v3/authorityProvider/certificates")
@Tag(name = "Certificate Management v3",
        description = "Stateless v3 certificate operations by utilizing attributes in request for identifying authority or RA profile")
public interface CertificateController extends AuthProtectedConnectorController {

    // ---- Issue ----

    @Operation(summary = "List issue operation attributes", description = "List attributes for issue operation")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Schema retrieved"))
    @PostMapping(path = "/issue/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listIssueAttributes(@RequestBody @Valid CertificateAttributeListRequestDtoV3 request);

    @Operation(summary = "Issue certificate", description = "Issue a certificate (sync 200 or async 202)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Issued synchronously"),
            @ApiResponse(responseCode = "202", description = "Issuance accepted asynchronously; body carries meta tracking handle")
    })
    @PostMapping(path = "/issue", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CertificateDataResponseDto> issue(@RequestBody @Valid CertificateSignRequestDtoV3 request);

    @Operation(summary = "Get async issue operation status", description = "Get status of an async issue/renew/rekey operation")
    @PostMapping(path = "/issue/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateOperationStatusResponseDto getIssueStatus(@RequestBody @Valid CertificateOperationStatusRequestDtoV3 request);

    @Operation(summary = "Cancel async issue operation", description = "Cancel an in-flight async issue/renew/rekey operation")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Aborted"),
            @ApiResponse(responseCode = "404", description = "Operation not tracked — treat as terminal cancellation"),
            @ApiResponse(responseCode = "422", description = "Refused — past point of no return")
    })
    @PostMapping(path = "/issue/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancelIssue(@RequestBody @Valid CertificateOperationCancelRequestDtoV3 request);

    // ---- Renew (status/cancel via /issue/*) ----

    @Operation(summary = "Renew certificate", description = "Renew a certificate (sync 200 or async 202)")
    @PostMapping(path = "/renew", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CertificateDataResponseDto> renew(@RequestBody @Valid CertificateRenewRequestDtoV3 request);

    // ---- Revoke ----

    @Operation(summary = "List revoke operation attributes", description = "List dynamic attributes for revoke")
    @PostMapping(path = "/revoke/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listRevokeAttributes(@RequestBody @Valid CertificateAttributeListRequestDtoV3 request);

    @Operation(summary = "Revoke certificate", description = "Revoke a certificate (sync 204 or async 202)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Revoked synchronously"),
            @ApiResponse(responseCode = "202", description = "Revocation accepted asynchronously; body carries meta tracking handle")
    })
    @PostMapping(path = "/revoke", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CertificateDataResponseDto> revoke(@RequestBody @Valid CertificateRevocationRequestDtoV3 request);

    @Operation(summary = "Get async revoke operation status", description = "Get status of an async revoke operation")
    @PostMapping(path = "/revoke/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateOperationStatusResponseDto getRevokeStatus(@RequestBody @Valid CertificateOperationStatusRequestDtoV3 request);

    @Operation(summary = "Cancel async revoke operation", description = "Cancel an in-flight async revoke operation")
    @PostMapping(path = "/revoke/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancelRevoke(@RequestBody @Valid CertificateOperationCancelRequestDtoV3 request);

    // ---- Register ----

    @Operation(summary = "List register operation attributes", description = "List dynamic attributes for register")
    @PostMapping(path = "/register/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listRegisterAttributes(@RequestBody @Valid CertificateAttributeListRequestDtoV3 request);

    @Operation(summary = "Register certificate", description = "Pre-register a certificate's identity at the upstream CA (no CSR)")
    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<CertificateDataResponseDto> register(@RequestBody @Valid CertificateRegistrationRequestDtoV3 request);

    @Operation(summary = "Get async register operation status", description = "Get status of an async register operation")
    @PostMapping(path = "/register/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateOperationStatusResponseDto getRegisterStatus(@RequestBody @Valid CertificateOperationStatusRequestDtoV3 request);

    @Operation(summary = "Cancel async register operation", description = "Cancel an in-flight async register operation")
    @PostMapping(path = "/register/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancelRegister(@RequestBody @Valid CertificateOperationCancelRequestDtoV3 request);

    // ---- Identify ----

    @Operation(summary = "Identify certificate", description = "Identify a certificate at the upstream CA (always synchronous)")
    @PostMapping(path = "/identify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateIdentificationResponseDto identify(@RequestBody @Valid CertificateIdentificationRequestDtoV3 request);
}
