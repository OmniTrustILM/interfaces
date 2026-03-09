package com.czertainly.api.interfaces.connector.secrets;

import com.czertainly.api.exception.AlreadyExistException;
import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.common.error.ProblemDetailExtended;
import com.czertainly.api.model.connector.secrets.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/secretProvider/secrets")
@Tag(name = "Secret Management", description = "Secret Management API")
public interface SecretController extends AuthProtectedConnectorController {

    @Operation(summary = "Get Secret Attributes")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Secret attributes retrieved")})
    @GetMapping(path = "/{secretType}/attributes", produces = {"application/json"})
    List<BaseAttribute> getSecretAttributes(@Parameter(description = "Secret type") @PathVariable SecretType secretType);

    @Operation(summary = "Get Secret Content")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Secret content retrieved"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found. Secret or secret version not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service Unavailable",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            )})
    @PostMapping(path = "/content", consumes = {"application/json"}, produces = {"application/json"})
    SecretContentResponseDto getSecretContent(@Parameter(description = "Secret request") @RequestBody SecretRequestDto request, @RequestParam(required = false, name = "version") String version) throws NotFoundException;

    @Operation(summary = "Create Secret")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Secret created"),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict. Secret already exists"),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity. Secret value validation failed.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service Unavailable",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            )})
    @PostMapping(consumes = {"application/json"})
    @ResponseStatus(HttpStatus.CREATED)
    SecretResponseDto createSecret(@Parameter(description = "Create Secret request") @RequestBody @Valid CreateSecretRequestDto request) throws AlreadyExistException;

    @Operation(summary = "Update Secret")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Secret updated"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity. Secret value validation failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service Unavailable",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            )})
    @PutMapping(consumes = {"application/json"})
    SecretResponseDto updateSecret(@Parameter(description = "Update Secret request") @RequestBody UpdateSecretRequestDto request) throws NotFoundException;

    @Operation(summary = "Delete Secret")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Secret deleted"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service Unavailable",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            )})
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteSecret(@Parameter(description = "Secret request") @RequestBody SecretRequestDto request) throws NotFoundException;

    @Operation(summary = "Get Rotate Attributes")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Rotate attributes retrieved")})
    @GetMapping(path = "/rotate/attributes", produces = {"application/json"})
    List<BaseAttribute> getRotateAttributes() throws NotFoundException;

    @Operation(summary = "Rotate Secret")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Secret rotated"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not Found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity. Secret value validation failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service Unavailable",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            )})
    @PostMapping(path = "/rotate")
    SecretResponseDto rotateSecret(@Parameter(description = "Secret request") @RequestBody SecretRequestDto request) throws NotFoundException;

}