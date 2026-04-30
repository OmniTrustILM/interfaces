package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.AlreadyExistException;
import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.trustedcertificate.TrustedCertificateRequestDto;
import com.czertainly.api.model.client.trustedcertificate.TrustedCertificateDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.UuidDto;
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

@RequestMapping("/v1/trustedCertificates")
@Tag(name = "Trusted Certificate Management", description = "Trusted Certificate Management API")
@ApiResponses(
    value =
    @ApiResponse(
        responseCode = "404",
        description = "Not Found",
        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
    )
)
public interface TrustedCertificateController extends AuthProtectedController {

    @Operation(summary = "List Trusted Certificates")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of Trusted Certificates")})
    @GetMapping(produces = {"application/json"})
    List<TrustedCertificateDto> listTrustedCertificates();

    @Operation(summary = "Get details of a Trusted Certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Trusted Certificate details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    TrustedCertificateDto getTrustedCertificate(@Parameter(description = "Trusted Certificate UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Create a new Trusted Certificate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "New Trusted Certificate created", content = @Content(schema = @Schema(implementation = UuidDto.class))),
        @ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
    })
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<?> createTrustedCertificate(@RequestBody TrustedCertificateRequestDto request)
        throws AlreadyExistException;

    @Operation(summary = "Delete a Trusted Certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Trusted Certificate deleted")})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    void deleteTrustedCertificate(@Parameter(description = "Trusted Certificate UUID") @PathVariable String uuid) throws NotFoundException;

}