package com.czertainly.api.interfaces.connector.v3;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v3.authority.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v3/authorityProvider/authorities")
@Tag(name = "v3 Authority Operations",
     description = "Stateless v3 authority operations — no createAuthorityInstance / lifecycle; "
                 + "authority identity is the attribute blob in the body")
public interface AuthorityController {

    @Operation(summary = "Top-level authority attribute schema (for the create-authority UI)")
    @GetMapping(path = "/attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listAuthorityAttributes();

    @Operation(summary = "Validate authority attributes by attempting to reach the upstream CA")
    @ApiResponse(responseCode = "204", description = "Upstream CA is reachable with these attributes")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> checkAuthorityConnection(@RequestBody List<RequestAttribute> body);

    @Operation(summary = "RA profile attribute schema given authority context")
    @PostMapping(path = "/raProfile/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listRaProfileAttributes(@RequestBody List<RequestAttribute> body);

    @Operation(summary = "Fetch CRL (full or delta) from the upstream CA")
    @PostMapping(path = "/crl", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CrlResponseDto getCrl(@RequestBody CrlRequestDto body);

    @Operation(summary = "Fetch CA certificate chain from the upstream CA")
    @PostMapping(path = "/caCertificates", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CaCertificatesResponseDto getCaCertificates(@RequestBody CaCertificatesRequestDto body);
}
