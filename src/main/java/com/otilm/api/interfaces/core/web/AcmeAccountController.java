package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.acme.AcmeAccountListResponseDto;
import com.otilm.api.model.client.acme.AcmeAccountResponseDto;
import com.otilm.api.model.common.ErrorMessageDto;
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

@RequestMapping("/v1")
@Tag(name = "ACME Account Management", description = "ACME Account Management API")
@ApiResponses(
        value = {
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                )
        })
public interface AcmeAccountController extends AuthProtectedController {

    @Operation(summary = "List ACME Accounts")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "ACME Accounts list retrieved")})
    @GetMapping(path = "/acmeAccounts", produces = {"application/json"})
    List<AcmeAccountListResponseDto> listAcmeAccounts();

    @Operation(summary = "Details of ACME Account")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "ACME Account details retrieved")})
    @GetMapping(path = "/acmeProfiles/{acmeProfileUuid}/acmeAccounts/{acmeAccountUuid}", produces = {"application/json"})
    AcmeAccountResponseDto getAcmeAccount(@Parameter(description = "ACME Profile UUID") @PathVariable String acmeProfileUuid, @Parameter(description = "ACME Account UUID") @PathVariable String acmeAccountUuid) throws NotFoundException;

    @Operation(summary = "Enable ACME Account")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "ACME Account enabled")})
    @PatchMapping(path = "/acmeProfiles/{acmeProfileUuid}/acmeAccounts/{acmeAccountUuid}/enable", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableAcmeAccount(@Parameter(description = "ACME Profile UUID") @PathVariable String acmeProfileUuid, @Parameter(description = "ACME Account UUID") @PathVariable String acmeAccountUuid) throws NotFoundException;

    @Operation(summary = "Disable ACME Account")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "ACME Account disabled")})
    @PatchMapping(path = "/acmeProfiles/{acmeProfileUuid}/acmeAccounts/{acmeAccountUuid}/disable", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableAcmeAccount(@Parameter(description = "ACME Profile UUID") @PathVariable String acmeProfileUuid, @Parameter(description = "ACME Account UUID") @PathVariable String acmeAccountUuid) throws NotFoundException;

    @Operation(summary = "Revoke ACME Account")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "ACME Account revoked")})
    @PostMapping(path = "/acmeProfiles/{acmeProfileUuid}/acmeAccounts/{acmeAccountUuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void revokeAcmeAccount(@Parameter(description = "ACME Profile UUID") @PathVariable String acmeProfileUuid, @Parameter(description = "ACME Account UUID") @PathVariable String acmeAccountUuid) throws NotFoundException;

    @Operation(summary = "Enable multiple ACME Accounts")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "ACME Accounts enabled")})
    @PatchMapping(path = "/acmeAccounts/enable", produces = {"application/json"}, consumes = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkEnableAcmeAccount(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "ACME Account UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples={@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids) throws NotFoundException;

    @Operation(summary = "Disable multiple ACME Accounts")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "ACME Accounts disabled")})
    @PatchMapping(path = "/acmeAccounts/disable", produces = {"application/json"}, consumes = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkDisableAcmeAccount(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "ACME Account UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples={@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids) throws NotFoundException;

    @Operation(summary = "Revoke multiple ACME Accounts")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "ACME Accounts revoked")})
    @PutMapping(path = "/acmeAccounts/revoke", produces = {"application/json"}, consumes = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkRevokeAcmeAccount(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "ACME Account UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples={@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids) throws NotFoundException;
}
