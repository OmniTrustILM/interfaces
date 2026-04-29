package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.*;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.cryptography.tokenprofile.AddTokenProfileRequestDto;
import com.otilm.api.model.client.cryptography.tokenprofile.BulkTokenProfileKeyUsageRequestDto;
import com.otilm.api.model.client.cryptography.tokenprofile.EditTokenProfileRequestDto;
import com.otilm.api.model.client.cryptography.tokenprofile.TokenProfileKeyUsageRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.core.cryptography.tokenprofile.TokenProfileDetailDto;
import com.otilm.api.model.core.cryptography.tokenprofile.TokenProfileDto;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/v1")
@Tag(name = "Token Profile Management", description = "Token Profile Management API")
@ApiResponses(
        value = {
                @ApiResponse(
                        responseCode = "502",
                        description = "Connector Error",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                ),
                @ApiResponse(
                        responseCode = "503",
                        description = "Connector Communication Error",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                ),
        })

public interface TokenProfileController extends AuthProtectedController {
    @Operation(
            summary = "List of available Token Profiles"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Token Profiles retrieved")
            })
    @GetMapping(
            path = "/tokenProfiles",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    List<TokenProfileDto> listTokenProfiles(Optional<Boolean> enabled);

    @Operation(
            summary = "Details of Token Profile"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Token Profile details retrieved"),
                    @ApiResponse(responseCode = "404", description = "Token Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @GetMapping(
            path = "/tokens/{tokenInstanceUuid}/tokenProfiles/{uuid}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    TokenProfileDetailDto getTokenProfile(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String uuid)
            throws NotFoundException;

    @Operation(
            summary = "Create Token Profile"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Token Profile added"),
                    @ApiResponse(responseCode = "422", description = "Unprocessible Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
                    @ApiResponse(responseCode = "404", description = "Token Instance not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PostMapping(
            path = "/tokens/{tokenInstanceUuid}/tokenProfiles",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<TokenProfileDetailDto> createTokenProfile(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @RequestBody AddTokenProfileRequestDto request)
            throws AlreadyExistException, ValidationException, ConnectorException, AttributeException, NotFoundException;

    @Operation(
            summary = "Edit Token Profile"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Token Profile updated"),
                    @ApiResponse(responseCode = "422", description = "Unprocessible Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
                    @ApiResponse(responseCode = "404", description = "Token Profile or Token instance not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PutMapping(
            path = "/tokens/{tokenInstanceUuid}/tokenProfiles/{uuid}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    TokenProfileDetailDto editTokenProfile(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String uuid,
            @RequestBody EditTokenProfileRequestDto request)
            throws ConnectorException, AttributeException, NotFoundException;

    @Operation(
            summary = "Delete Token Profile"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Token Profile deleted"),
                    @ApiResponse(responseCode = "404", description = "Token Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @DeleteMapping(
            path = "/tokens/{tokenInstanceUuid}/tokenProfiles/{uuid}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTokenProfile(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String uuid)
            throws NotFoundException;

    @Operation(
            summary = "Delete Token Profile",
            operationId = "deleteRaProfileWithoutTokenInstance"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Token Profile deleted"),
                    @ApiResponse(responseCode = "404", description = "Token Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @DeleteMapping(
            path = "/tokenProfiles/{uuid}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTokenProfile(
            @Parameter(description = "Token Profile UUID") @PathVariable String uuid)
            throws NotFoundException;

    @Operation(
            summary = "Disable Token Profile"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Token Profile disabled"),
                    @ApiResponse(responseCode = "404", description = "Token Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PatchMapping(
            path = "/tokens/{tokenInstanceUuid}/tokenProfiles/{uuid}/disable",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableTokenProfile(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String uuid)
            throws NotFoundException;

    @Operation(
            summary = "Enable Token Profile"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Token Profile enabled"),
                    @ApiResponse(responseCode = "404", description = "Token Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            })
    @PatchMapping(
            path = "/tokens/{tokenInstanceUuid}/tokenProfiles/{uuid}/enable",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableTokenProfile(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String uuid)
            throws NotFoundException;

    @Operation(
            summary = "Delete multiple Token Profiles"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Token Profiles deleted"),
                    @ApiResponse(responseCode = "422", description = "Unprocessible Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @DeleteMapping(path = "/tokenProfiles",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTokenProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Token Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                                @RequestBody List<String> uuids);

    @Operation(
            summary = "Disable multiple Token Profiles"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Token Profiles disabled")
            })
    @PatchMapping(
            path = "/tokenProfiles/disable",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableTokenProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Token Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                              @RequestBody List<String> uuids);

    @Operation(
            summary = "Enable multiple Token Profiles"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Token Profiles enabled")
            })
    @PatchMapping(path = "/tokenProfiles/enable",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableTokenProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Token Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                             @RequestBody List<String> uuids);

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // Usages
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(
            summary = "Update Key Usage"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Key Usages Updated"),
                    @ApiResponse(responseCode = "404", description = "Token Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
            }
    )
    @PutMapping(
            path = "/tokens/{tokenInstanceUuid}/tokenProfiles/{tokenProfileUuid}/usages",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateKeyUsages(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String tokenProfileUuid,
            @RequestBody TokenProfileKeyUsageRequestDto request)
            throws NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(
            summary = "Update Key Usages for Multiple Token Profiles"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Key Usages Updated")
            }
    )
    @PutMapping(
            path = "/tokens/usages",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateKeysUsages(@RequestBody BulkTokenProfileKeyUsageRequestDto request);
}
