package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.AttributeException;
import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.certificate.SearchRequestDto;
import com.czertainly.api.model.client.signing.protocols.ilm.IlmSigningProtocolConfigurationRequestDto;
import com.czertainly.api.model.common.BulkActionMessageDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.PaginationResponseDto;
import com.czertainly.api.model.core.search.SearchFieldDataByGroupDto;
import com.czertainly.api.model.client.signing.protocols.ilm.IlmSigningProtocolConfigurationDto;
import com.czertainly.api.model.client.signing.protocols.ilm.IlmSigningProtocolConfigurationListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/ilmSigningProtocolConfigurations")
@Tag(name = "ILM Signing Protocol Configuration Management", description = "Internal ILM Signing Protocol Configuration Management API")
@ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
public interface IlmSigningProtocolConfigurationController extends AuthProtectedController {

    @Operation(operationId = "listIlmSigningProtocolConfigurationSearchableFields", summary = "List search filters for ILM Signing Protocol Configurations")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of search filters retrieved")})
    @GetMapping(path = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(operationId = "listIlmSigningProtocolConfigurations", summary = "List of available ILM Signing Protocol Configurations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ILM Signing Protocol Configurations retrieved"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })
    @PostMapping(path = "/list", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<IlmSigningProtocolConfigurationListDto> listIlmSigningProtocolConfigurations(@RequestBody SearchRequestDto request);

    @Operation(operationId = "getIlmSigningProtocolConfiguration", summary = "Details of an ILM Signing Protocol Configuration")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "ILM Signing Protocol Configuration details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    IlmSigningProtocolConfigurationDto getIlmSigningProtocolConfiguration(@Parameter(description = "ILM Signing Protocol Configuration UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "createIlmSigningProtocolConfiguration", summary = "Add new ILM Signing Protocol Configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New ILM Signing Protocol Configuration added"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    IlmSigningProtocolConfigurationDto createIlmSigningProtocolConfiguration(@RequestBody @Valid IlmSigningProtocolConfigurationRequestDto request) throws AttributeException, NotFoundException;

    @Operation(operationId = "updateIlmSigningProtocolConfiguration", summary = "Update ILM Signing Protocol Configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ILM Signing Protocol Configuration updated"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),})
    @PutMapping(path = "/{uuid}", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    IlmSigningProtocolConfigurationDto updateIlmSigningProtocolConfiguration(@Parameter(description = "ILM Signing Protocol Configuration UUID") @PathVariable UUID uuid, @RequestBody @Valid IlmSigningProtocolConfigurationRequestDto request) throws NotFoundException, AttributeException;

    @Operation(operationId = "deleteIlmSigningProtocolConfiguration", summary = "Delete ILM Signing Protocol Configuration")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "ILM Signing Protocol Configuration deleted")})
    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteIlmSigningProtocolConfiguration(@Parameter(description = "ILM Signing Protocol Configuration UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkDeleteIlmSigningProtocolConfigurations", summary = "Delete multiple ILM Signing Protocol Configurations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ILM Signing Protocol Configurations deleted"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkDeleteIlmSigningProtocolConfigurations(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "ILM Signing Protocol Configuration UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "enableIlmSigningProtocolConfiguration", summary = "Enable ILM Signing Protocol Configuration")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "ILM Signing Protocol Configuration enabled")})
    @PatchMapping(path = "/{uuid}/enable", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableIlmSigningProtocolConfiguration(@Parameter(description = "ILM Signing Protocol Configuration UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkEnableIlmSigningProtocolConfigurations", summary = "Enable multiple ILM Signing Protocol Configurations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ILM Signing Protocol Configurations enabled"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })
    @PatchMapping(path = "/enable", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkEnableIlmSigningProtocolConfigurations(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "ILM Signing Protocol Configuration UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);

    @Operation(operationId = "disableIlmSigningProtocolConfiguration", summary = "Disable ILM Signing Protocol Configuration")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "ILM Signing Protocol Configuration disabled")})
    @PatchMapping(path = "/{uuid}/disable", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableIlmSigningProtocolConfiguration(@Parameter(description = "ILM Signing Protocol Configuration UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "bulkDisableIlmSigningProtocolConfigurations", summary = "Disable multiple ILM Signing Protocol Configurations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ILM Signing Protocol Configurations disabled"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })
    @PatchMapping(path = "/disable", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    List<BulkActionMessageDto> bulkDisableIlmSigningProtocolConfigurations(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "ILM Signing Protocol Configuration UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<UUID> uuids);
}
