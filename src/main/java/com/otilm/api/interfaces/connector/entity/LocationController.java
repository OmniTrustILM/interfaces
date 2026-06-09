package com.otilm.api.interfaces.connector.entity;

import com.otilm.api.exception.LocationException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.entity.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/entityProvider/entities/{entityUuid}/locations")
@Tag(
        name = "Location Operations",
        description = "Interfaces to control certificates and key stores on Entities. " +
                "Locations provides capabilities of getting the certificates already on the Entity, " +
                "pushing new certificates, generation of new key pair and certificate signing requests, " +
                "removing certificates and management of the Entity end-to-end automation."
)
public interface LocationController extends AuthProtectedConnectorController {

    @Operation(
            summary = "Get Location Details",
            description = "Get information about the Location content. All identified certificates are returned"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Location detail and content retrieved"
                    )
            })
    @PostMapping(
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    LocationDetailResponseDto getLocationDetail(
            @Parameter(description = "Entity instance UUID") @PathVariable String entityUuid,
            @RequestBody LocationDetailRequestDto request
    ) throws NotFoundException, LocationException;

    @Operation(
            summary = "Push Certificate",
            description = "Push the Certificate into the Location"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate pushed to Location"
                    )
            })
    @PostMapping(
            path = "/push",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    PushCertificateResponseDto pushCertificateToLocation(
            @Parameter(description = "Entity instance UUID") @PathVariable String entityUuid,
            @RequestBody PushCertificateRequestDto request
    ) throws NotFoundException, LocationException;

    @Operation(
            summary = "List Push Certificate Attributes",
            description = "List of Attributes to push Certificate into Location"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Attributes for push retrieved"
                    )
            })
    @GetMapping(
            path = "/push/attributes",
            produces = {"application/json"}
    )
    List<BaseAttribute> listPushCertificateAttributes(
            @Parameter(description = "Entity instance UUID") @PathVariable String entityUuid
    ) throws NotFoundException;

    @Operation(
            summary = "Validate Push Certificate Attributes",
            description = "Validate list of Attributes to push Certificate into Location"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Attributes validated"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(
            path = "/push/attributes/validate",
            consumes = {"application/json"}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void validatePushCertificateAttributes(
            @Parameter(description = "Entity Instance UUID") @PathVariable String entityUuid,
            @RequestBody List<RequestAttribute>pushAttributes
    ) throws NotFoundException, ValidationException;

    @Operation(
            summary = "Remove Certificate",
            description = "Remove Certificate from Location"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Certificate removed"
                    )
            })
    @PostMapping(
            path = "/remove",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    RemoveCertificateResponseDto removeCertificateFromLocation(
            @Parameter(description = "Entity instance UUID") @PathVariable String entityUuid,
            @RequestBody RemoveCertificateRequestDto request
    ) throws NotFoundException, LocationException;

    @Operation(
            summary = "Generate CSR",
            description = "Generate key pair and CSR for the Location"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "CSR generated"
                    )
            })
    @PostMapping(
            path = "/csr",
            consumes = {"application/json"},
            produces = {"application/json"}
    )
    GenerateCsrResponseDto generateCsrLocation(
            @Parameter(description = "Entity instance UUID") @PathVariable String entityUuid,
            @RequestBody GenerateCsrRequestDto request
    ) throws NotFoundException, LocationException;

    @Operation(
            summary = "List CSR Attributes",
            description = "List of Attributes to generate key pair and CSR"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "CSR Attributes retrieved"
                    )
            })
    @GetMapping(
            path = "/csr/attributes",
            produces = {"application/json"}
    )
    List<BaseAttribute> listGenerateCsrAttributes(
            @Parameter(description = "Entity instance UUID") @PathVariable String entityUuid
    ) throws NotFoundException;

    @Operation(
            summary = "Validate CSR Attributes",
            description = "Validate list of Attributes to generate key pair and CSR"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Attributes validated"
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Unprocessable Entity",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                                    examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}
                            ))
            })
    @PostMapping(
            path = "/csr/attributes/validate",
            consumes = {"application/json"}
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void validateGenerateCsrAttributes(
            @Parameter(description = "Entity Instance UUID") @PathVariable String entityUuid,
            @RequestBody List<RequestAttribute>csrAttributes
    ) throws NotFoundException, ValidationException;
}
