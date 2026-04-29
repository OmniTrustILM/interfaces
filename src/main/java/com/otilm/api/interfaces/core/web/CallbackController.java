package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.attribute.common.callback.RequestAttributeCallback;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Callback", description = "Callback API")
@ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))), @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})), @ApiResponse(responseCode = "502", description = "Connector Error", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))), @ApiResponse(responseCode = "503", description = "Connector Communication Error", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})

public interface CallbackController extends AuthProtectedController {

    @Operation(summary = "Connector Callback API", description = "API to trigger the Callback for Connector.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Callback executed")})
    @PostMapping(path = "/v1/connectors/{uuid}/{functionGroup}/{kind}/callback", consumes = {"application/json"}, produces = {"application/json"})
    Object callback(@Parameter(description = "Connector UUID") @PathVariable String uuid, @Parameter(description = "Function Group") @PathVariable String functionGroup, @Parameter(description = "Kind") @PathVariable String kind, @RequestBody RequestAttributeCallback callback) throws NotFoundException, ConnectorException, ValidationException, AttributeException;

    @Operation(operationId = "callbackV2", summary = "Connector Callback API v2", description = "API to trigger the Callback for Connector.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Callback executed")})
    @PostMapping(path = "/v2/connectors/{uuid}/callback", consumes = {"application/json"}, produces = {"application/json"})
    Object callback(@Parameter(description = "Connector UUID") @PathVariable UUID uuid, @RequestBody RequestAttributeCallback callback) throws ConnectorException, NotFoundException, AttributeException;

    @Operation(summary = "Resource Callback API", description = "API to trigger the Callback for resource.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Callback executed")})
    @PostMapping(path = "/v1/{resource}/{parentObjectUuid}/callback", consumes = {"application/json"}, produces = {"application/json"})
    Object resourceCallback(@Parameter(description = "Name of the resource") @PathVariable Resource resource, @Parameter(description = "Parent Object UUID") @PathVariable String parentObjectUuid, @RequestBody RequestAttributeCallback callback) throws NotFoundException, ConnectorException, ValidationException, AttributeException;

}
