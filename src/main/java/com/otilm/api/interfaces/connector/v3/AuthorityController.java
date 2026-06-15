package com.otilm.api.interfaces.connector.v3;

import com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.v3.authority.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v3/authorityProvider/authorities")
@Tag(name = "Authority Management v3",
     description = "Stateless v3 authority operations achieved by utilizing attributes in requests")
public interface AuthorityController extends AuthProtectedConnectorController {

    @Operation(summary = "List authority attributes", description = "Top-level authority attribute schema (for the create-authority UI)")
    @ApiResponse(responseCode = "200", description = "Authority attributes retrieved")
    @GetMapping(path = "/attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listAuthorityAttributes();

    @Operation(summary = "Check authority connection", description = "Validate authority attributes by attempting to reach the upstream CA")
    @ApiResponse(responseCode = "204", description = "Upstream CA is reachable with these attributes")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> checkAuthorityConnection(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Authority attributes", required = true)
            @RequestBody List<RequestAttribute> attributes);

    @Operation(summary = "List RA Profile Attributes", description = "RA profile attribute schema given authority context")
    @ApiResponse(responseCode = "200", description = "RA profile attributes retrieved")
    @PostMapping(path = "/raProfile/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listRaProfileAttributes(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Authority attributes", required = true)
            @RequestBody List<RequestAttribute> attributes);

    @Operation(summary = "Get the latest CRL for Authority", description = "Fetch CRL (full or delta) from the upstream CA based on request")
    @ApiResponse(responseCode = "200", description = "CRL retrieved")
    @PostMapping(path = "/crl", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CrlResponseDto getCrl(@RequestBody CrlRequestDtoV3 request);

    @Operation(summary = "Get certificate chain for Authority", description = "Fetch CA certificate chain from the upstream CA")
    @ApiResponse(responseCode = "200", description = "CA certificate chain retrieved")
    @PostMapping(path = "/caCertificates", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CaCertificatesResponseDto getCaCertificates(@RequestBody CaCertificatesRequestDtoV3 request);
}
