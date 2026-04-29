package com.otilm.api.interfaces.core.client;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.authority.*;
import com.otilm.api.model.common.ErrorMessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

@RequestMapping("/v1/operations")
@ApiResponses(
        value = {
                @ApiResponse(
                        responseCode = "404",
                        description = "Not Found",
                        content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
                ),
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
@Tag(name = "Legacy Client Operations", description = "Client API for managing End Entities and Certificates")
public interface ClientOperationController extends AuthProtectedController {

    @Operation(summary = "Issue Certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate issued")})
    @PostMapping(path = "/{raProfileName}/certificate/issue", consumes = { "application/json" }, produces = { "application/json" })
    ClientCertificateSignResponseDto issueCertificate(
            @Parameter(description = "RA Profile name") @PathVariable String raProfileName,
            @RequestBody LegacyClientCertificateSignRequestDto request)
            throws NotFoundException, CertificateException, AlreadyExistException, ConnectorException, NoSuchAlgorithmException;

    @Operation(summary = "Revoke Certificate")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate revoked")})
    @PostMapping(path = "/{raProfileName}/certificate/revoke", consumes = { "application/json" }, produces = { "application/json" })
    void revokeCertificate(
            @Parameter(description = "RA Profile name") @PathVariable String raProfileName,
            @RequestBody LegacyClientCertificateRevocationDto request)
            throws NotFoundException, ConnectorException;

    @Operation(summary = "List all End Entities")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of entities retrieved")})
    @GetMapping(path = "/{raProfileName}/endentity", produces = {"application/json"})
    List<ClientEndEntityDto> listEntities(
            @Parameter(description = "RA Profile name") @PathVariable String raProfileName)
            throws NotFoundException, ConnectorException;

    @Operation(summary = "Add End Entity")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "End Entity added")})
    @PostMapping(path = "/{raProfileName}/endentity", consumes = { "application/json" }, produces = { "application/json" })
    void addEndEntity(
            @Parameter(description = "RA Profile name") @PathVariable String raProfileName,
            @RequestBody ClientAddEndEntityRequestDto request)
            throws NotFoundException, AlreadyExistException, ConnectorException;

    @Operation(summary = "Get End Entity information")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "End Entity detail retrieved")})
    @GetMapping(path = "/{raProfileName}/endentity/{username}", produces = {"application/json"})
    ClientEndEntityDto getEndEntity(
            @Parameter(description = "RA Profile name") @PathVariable String raProfileName,
            @Parameter(description = "Username") @PathVariable String username)
            throws NotFoundException, ConnectorException;

    @Operation(summary = "Edit End Entity")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "End Entity edited")})
    @PostMapping(path = "/{raProfileName}/endentity/{username}", consumes = { "application/json" }, produces = { "application/json" })
    void editEndEntity(
            @Parameter(description = "RA Profile name") @PathVariable String raProfileName,
            @Parameter(description = "Username") @PathVariable String username,
            @RequestBody ClientEditEndEntityRequestDto request)
            throws NotFoundException, ConnectorException;

    @Operation(summary = "Revoke all Certificates and delete End Entity")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "End Entity revoked and deleted")})
    @DeleteMapping(path = "/{raProfileName}/endentity/{username}", produces = {"application/json"})
    void revokeAndDeleteEndEntity(
            @Parameter(description = "RA Profile name") @PathVariable String raProfileName,
            @Parameter(description = "Username") @PathVariable String username)
            throws NotFoundException, ConnectorException;

    @Operation(summary = "Reset password for End Entity")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "End Entity password reset")})
    @PutMapping(path = "/{raProfileName}/endentity/{username}/resetPassword", produces = {"application/json"})
    void resetPassword(
            @Parameter(description = "RA Profile name") @PathVariable String raProfileName,
            @Parameter(description = "Username") @PathVariable String username)
            throws NotFoundException, ConnectorException;

}
