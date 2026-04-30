package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.AlreadyExistException;
import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.proxy.ProxyRequestDto;
import com.czertainly.api.model.client.proxy.ProxyUpdateRequestDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.UuidDto;
import com.czertainly.api.model.core.proxy.ProxyDto;
import com.czertainly.api.model.core.proxy.ProxyInstallInstructionsDto;
import com.czertainly.api.model.core.proxy.ProxyListDto;
import com.czertainly.api.model.core.proxy.ProxyStatus;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/proxies")
@Tag(name = "Proxy Management", description = "Proxy Management API")
@ApiResponses(
    value =
    @ApiResponse(
        responseCode = "404",
        description = "Not Found",
        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
    )
)
public interface ProxyController extends AuthProtectedController {

    @Operation(summary = "List Proxies by Status")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List all Proxies")})
    @GetMapping(produces = {"application/json"})
    List<ProxyListDto> listProxies(@RequestParam(required = false) ProxyStatus status);

    @Operation(summary = "Get details of a Proxy")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Proxy details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    ProxyDto getProxy(@Parameter(description = "Proxy UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Create a new Proxy")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "New Proxy created", content = @Content(schema = @Schema(implementation = UuidDto.class))),
        @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> createProxy(@RequestBody ProxyRequestDto request)
        throws AlreadyExistException;

    @Operation(summary = "Edit a Proxy")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proxy updated"),
        @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })
    @PutMapping(path = "/{uuid}", consumes = {"application/json"}, produces = {"application/json"})
    ProxyDto editProxy(@Parameter(description = "Proxy UUID") @PathVariable String uuid, @RequestBody ProxyUpdateRequestDto request)
        throws NotFoundException;

    @Operation(summary = "Delete a Proxy")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Proxy deleted")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    void deleteProxy(@Parameter(description = "Proxy UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Get instructions to install a Proxy")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Proxy installation instructions retrieved")})
    @GetMapping(path = "/{uuid}/instructions", produces = {"application/json"})
    ProxyInstallInstructionsDto getInstallationInstructions(@Parameter(description = "Proxy UUID") @PathVariable String uuid) throws NotFoundException;
}
