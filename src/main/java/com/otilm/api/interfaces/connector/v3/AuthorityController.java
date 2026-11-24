package com.otilm.api.interfaces.connector.v3;

import com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.v3.authority.CaCertificatesRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CaCertificatesResponseDto;
import com.otilm.api.model.connector.v3.authority.CertificateTypesRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CrlRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CrlResponseDto;
import com.otilm.api.model.connector.v3.authority.SupportedCertificateTypeDtoV3;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v3/authorityProvider/authorities")
@Tag(name = "Authority Management v3",
        description = "Stateless v3 authority operations achieved by utilizing attributes in requests")
public interface AuthorityController extends AuthProtectedConnectorController {

    @Operation(summary = "List authority attributes",
            description = "Top-level authority attribute schema (for the create-authority UI)")
    @ApiResponse(responseCode = "200", description = "Authority attributes retrieved")
    @GetMapping(path = "/attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listAuthorityAttributes();

    @Operation(summary = "Check authority connection",
            description = "Validate authority attributes by attempting to reach the upstream CA")
    @ApiResponse(responseCode = "204", description = "Upstream CA is reachable with these attributes")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> checkAuthorityConnection(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Authority attributes", required = true) @RequestBody List<RequestAttribute> attributes);

    @Operation(summary = "List RA Profile Attributes",
            description = "RA profile attribute schema given authority context")
    @ApiResponse(responseCode = "200", description = "RA profile attributes retrieved")
    @PostMapping(path = "/raProfile/attributes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listRaProfileAttributes(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Authority attributes", required = true) @RequestBody List<RequestAttribute> attributes);

    @Operation(summary = "Get the latest CRL for Authority",
            description = "Fetch CRL (full or delta) from the upstream CA based on request")
    @ApiResponse(responseCode = "200", description = "CRL retrieved")
    @PostMapping(path = "/crl", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    CrlResponseDto getCrl(@RequestBody CrlRequestDtoV3 request);

    @Operation(summary = "Get certificate chain for Authority",
            description = "Fetch CA certificate chain from the upstream CA")
    @ApiResponse(responseCode = "200", description = "CA certificate chain retrieved")
    @PostMapping(path = "/caCertificates", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    CaCertificatesResponseDto getCaCertificates(@RequestBody CaCertificatesRequestDtoV3 request);

    @Operation(summary = "List supported certificate types",
            description = "List the certificate types the authority issues, or the types it issues through one RA "
                    + "profile when the RA profile attributes are given. Each type appears at most once, and X.509 is "
                    + "listed like any other type: a non-empty response is the whole answer, and an empty one means "
                    + "X.509 only. Optional endpoint — see responses.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Supported certificate types retrieved; an empty array means X.509 only"),
            @ApiResponse(responseCode = "404",
                    description = "Endpoint not served by this connector — Core treats the authority as issuing X.509 only"),
            @ApiResponse(responseCode = "501",
                    description = "Connector declines the operation (ErrorCode.OPERATION_NOT_SUPPORTED) — Core treats the authority as issuing X.509 only")})
    @PostMapping(path = "/certificateTypes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<@NotNull SupportedCertificateTypeDtoV3> listCertificateTypes(
            @RequestBody @Valid CertificateTypesRequestDtoV3 request);
}
