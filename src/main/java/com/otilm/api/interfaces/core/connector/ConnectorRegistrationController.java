package com.otilm.api.interfaces.core.connector;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.NoAuthController;
import com.otilm.api.model.client.connector.ConnectorRequestDto;
import com.otilm.api.model.common.UuidDto;
import com.otilm.api.model.core.connector.v2.ConnectorDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Connector Registration", description = "Connector Registration API")
public interface ConnectorRegistrationController extends NoAuthController {

    @Operation(summary = "Register a Connector")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Connector registration initiated")})
    @PostMapping(path = "/v1/connector/register", consumes = {"application/json"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    UuidDto register(@RequestBody ConnectorRequestDto request) throws ConnectorException, AlreadyExistException, AttributeException, NotFoundException;

    @Operation(summary = "Register a Connector v2", operationId = "registerV2")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Connector registration initiated")})
    @PostMapping(path = "/v2/connector/register", consumes = {"application/json"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    ConnectorDetailDto register(@RequestBody @Valid com.otilm.api.model.core.connector.v2.ConnectorRequestDto request) throws ConnectorException, AlreadyExistException, AttributeException, NotFoundException;
}
