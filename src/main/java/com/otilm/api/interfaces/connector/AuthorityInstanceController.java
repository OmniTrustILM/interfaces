package com.otilm.api.interfaces.connector;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.authority.AuthorityProviderInstanceDto;
import com.otilm.api.model.connector.authority.AuthorityProviderInstanceRequestDto;
import com.otilm.api.model.connector.authority.CertificateRevocationListRequestDto;
import com.otilm.api.model.connector.authority.CertificateRevocationListResponseDto;
import com.otilm.api.model.connector.authority.CaCertificatesRequestDto;
import com.otilm.api.model.connector.authority.CaCertificatesResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/authorityProvider/authorities")
@Tag(name = "Authority Management", description = "Authority Management API")
public interface AuthorityInstanceController extends AuthProtectedConnectorController {

    @Operation(
            summary = "List Authority instances"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authority instance list retrieved"
                    )
            })
    @GetMapping(produces = {"application/json"})
    List<AuthorityProviderInstanceDto> listAuthorityInstances();

    @Operation(
            summary = "Get an Authority instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authority instance retrieved"
                    )
            })
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    AuthorityProviderInstanceDto getAuthorityInstance(@Parameter(description = "Authority Instance UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(
            summary = "Create Authority instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authority instance created"
                    )
            })
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    AuthorityProviderInstanceDto createAuthorityInstance(@RequestBody AuthorityProviderInstanceRequestDto request) throws AlreadyExistException;

    @Operation(
            summary = "Update Authority instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authority instance updated"
                    )
            })
    @PostMapping(path = "/{uuid}", consumes = {"application/json"}, produces = {"application/json"})
    AuthorityProviderInstanceDto updateAuthorityInstance(@Parameter(description = "Authority Instance UUID") @PathVariable String uuid, @RequestBody AuthorityProviderInstanceRequestDto request) throws NotFoundException;

    @Operation(
            summary = "Remove Authority instance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authority instance removed"
                    )
            })
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void removeAuthorityInstance(@Parameter(description = "Authority Instance UUID") @PathVariable String uuid) throws NotFoundException;

    @GetMapping(path = "/{uuid}/connect", produces = {"application/json"})
    @Operation(
            summary = "Connect to Authority"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Authority instance connected"
                    )
            })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void getConnection(@Parameter(description = "Authority Instance UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(
            summary = "List RA Profile Attributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "RA Profile Attributes retrieved"
                    )
            })
    @GetMapping(path = "/{uuid}/raProfile/attributes", produces = {"application/json"})
    List<BaseAttribute> listRAProfileAttributes(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(
            summary = "Validate RA Profile attributes"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "RA Profile Attributes information validated"
                    )
            })
    @PostMapping(path = "/{uuid}/raProfile/attributes/validate", consumes = {"application/json"}, produces = {"application/json"})
    void validateRAProfileAttributes(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody List<RequestAttribute>attributes) throws ValidationException, NotFoundException;

    @Operation(
            summary = "Get the latest CRL for the Authority Instance",
            description = "Returns the latest CRL for the Authority Instance. " +
            "If delta is true, the delta CRL is returned, otherwise the full CRL is returned. " +
            "When the CRL is not available for Authority Instance, null data is returned."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "CRL retrieved"
                    )
            })
    @PostMapping(
            path = "/{uuid}/crl",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    CertificateRevocationListResponseDto getCrl(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody CertificateRevocationListRequestDto request
    ) throws NotFoundException;
    
    @Operation(
            summary = "Get the Authority Instance's certificate chain",
            description = "Returns the Authority Instance's certificate chain. The chain is returned as a list of " +
                    "Base64 encoded certificates, starting with the Authority Instance's certificate " +
                    "and ending with the root certificate, if available."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authority Instance's certificate chain retrieved"
                    )
            })
    @PostMapping(
            path = "/{uuid}/caCertificates",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    CaCertificatesResponseDto getCaCertificates(
            @Parameter(description = "Authority Instance UUID") @PathVariable String uuid,
            @RequestBody CaCertificatesRequestDto raProfileAttributes
    ) throws ValidationException, NotFoundException;

}