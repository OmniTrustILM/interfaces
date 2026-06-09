package com.otilm.api.interfaces.core.web.v2;

import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.compliance.v2.ComplianceCheckResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v2/compliance")
@Tag(name = "Compliance Management v2", description = "Compliance Management v2 API")
@ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))), @ApiResponse(responseCode = "502", description = "Connector Error", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))), @ApiResponse(responseCode = "503", description = "Connector Communication Error", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
public interface ComplianceController extends AuthProtectedController {

    @Operation(operationId = "checkComplianceV2", summary = "Initiate compliance check for requested compliance profiles")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Compliance check initiated")})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void checkCompliance(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Compliance Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UUID.class)))) @RequestBody List<UUID> uuids, @Parameter(description = "Filter objects checked by compliance based on its resource and evaluate only rules applicable to that resource") @RequestParam(required = false) Resource resource, @Parameter(description = "Filter objects checked by compliance based on its type and evaluate only rules applicable to that resource type", example = "X.509") @RequestParam(required = false) String type) throws ConnectorException, NotFoundException;

    @Operation(operationId = "checkResourceObjectsComplianceV2", summary = "Initiate compliance Check for requested resource objects")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Compliance check initiated")})
    @PostMapping(path = "/{resource}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void checkResourceObjectsCompliance(@Parameter(description = "Resource", required = true, example = Resource.Codes.RA_PROFILE) @PathVariable Resource resource, @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Resource object UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = UUID.class)))) @RequestBody List<UUID> objectUuids) throws ConnectorException, NotFoundException;

    @Operation(operationId = "checkResourceObjectComplianceV2", summary = "Initiate compliance Check for requested resource object")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Compliance check initiated")})
    @PostMapping(path = "/{resource}/{objectUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void checkResourceObjectCompliance(@Parameter(description = "Resource", required = true, example = Resource.Codes.RA_PROFILE) @PathVariable Resource resource, @Parameter(description = "Object UUID", required = true) @PathVariable UUID objectUuid) throws ConnectorException, NotFoundException;

    @Operation(operationId = "getComplianceCheckResultV2", summary = "Get the latest compliance check result for the specified resource object")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Compliance check initiated")})
    @GetMapping(path = "/{resource}/{objectUuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    ComplianceCheckResultDto getComplianceCheckResult(@Parameter(description = "Resource", required = true, example = Resource.Codes.RA_PROFILE) @PathVariable Resource resource, @Parameter(description = "Object UUID", required = true) @PathVariable UUID objectUuid) throws NotFoundException;

}
