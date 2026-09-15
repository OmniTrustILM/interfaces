package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.cryptography.operations.CipherDataRequestDto;
import com.otilm.api.model.client.cryptography.operations.DecryptDataResponseDto;
import com.otilm.api.model.client.cryptography.operations.EncryptDataResponseDto;
import com.otilm.api.model.client.cryptography.operations.RandomDataRequestDto;
import com.otilm.api.model.client.cryptography.operations.RandomDataResponseDto;
import com.otilm.api.model.client.cryptography.operations.SignDataRequestDto;
import com.otilm.api.model.client.cryptography.operations.SignDataResponseDto;
import com.otilm.api.model.client.cryptography.operations.VerifyDataRequestDto;
import com.otilm.api.model.client.cryptography.operations.VerifyDataResponseDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v1/operations/tokens/{tokenInstanceUuid}")
@Tag(name = "Cryptographic Operations Controller", description = "Cryptographic Operations Controller API")
public interface CryptographicOperationsController extends AuthProtectedController {

    /////////////////////////////////////////////////////////////////////////////////
    // cipher operations
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * @deprecated Use {@link #listEncryptAttributes(String, String, String, String)} or
     * {@link #listDecryptAttributes(String, String, String, String)} for operation-specific attributes.
     */
    @Deprecated(since = "2.20.0", forRemoval = true)
    @Operation(summary = "List of cipher Attributes", deprecated = true,
            description = "Legacy attribute discovery for v1 providers only. Use listEncryptAttributes or "
                    + "listDecryptAttributes instead. Not supported for v2 providers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Token instance, token profile, key or key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "200", description = "List of Attributes retrieved"),
            @ApiResponse(responseCode = "422", description = "Validation failed or the key algorithm is unsupported",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "501",
                    description = "Legacy attribute discovery is not supported for v2 providers; use the "
                            + "operation-specific attribute endpoints.",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(
            path = "/tokenProfiles/{tokenProfileUuid}/keys/{uuid}/items/{keyItemUuid}/cipher/{algorithm}/attributes",
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listCipherAttributes(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid,
            @Parameter(description = "Cryptographic algorithm") @PathVariable KeyAlgorithm algorithm)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "List encryption attributes",
            description = "Returns the encryption attribute schema for the specified token, profile and key item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Encrypt attribute schema retrieved"),
            @ApiResponse(responseCode = "404", description = "Token instance, token profile, key or key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/tokenProfiles/{tokenProfileUuid}/keys/{uuid}/items/{keyItemUuid}/encrypt/attributes",
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listEncryptAttributes(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Encrypt data using a Key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data encrypted"),
            @ApiResponse(responseCode = "404", description = "Token instance, token profile, key or key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(path = "/tokenProfiles/{tokenProfileUuid}/keys/{uuid}/items/{keyItemUuid}/encrypt",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    EncryptDataResponseDto encryptData(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid,
            @RequestBody CipherDataRequestDto request) throws ConnectorException, NotFoundException;

    @Operation(summary = "List decryption attributes",
            description = "Returns the decryption attribute schema for the specified token, profile and key item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Decrypt attribute schema retrieved"),
            @ApiResponse(responseCode = "404", description = "Token instance, token profile, key or key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/tokenProfiles/{tokenProfileUuid}/keys/{uuid}/items/{keyItemUuid}/decrypt/attributes",
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listDecryptAttributes(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Decrypt data using a Key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data decrypted"),
            @ApiResponse(responseCode = "404", description = "Token instance, token profile, key or key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(path = "/tokenProfiles/{tokenProfileUuid}/keys/{uuid}/items/{keyItemUuid}/decrypt",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    DecryptDataResponseDto decryptData(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid,
            @RequestBody CipherDataRequestDto request) throws ConnectorException, NotFoundException;

    /////////////////////////////////////////////////////////////////////////////////
    // signature operations
    /////////////////////////////////////////////////////////////////////////////////

    /**
     * @deprecated Use {@link #listSignAttributes(String, String, String, String)} or
     * {@link #listVerifyAttributes(String, String, String, String)} for operation-specific attributes.
     */
    @Deprecated(since = "2.20.0", forRemoval = true)
    @Operation(summary = "List of signature Attributes", deprecated = true,
            description = "Legacy attribute discovery for v1 providers only. Use listSignAttributes or "
                    + "listVerifyAttributes instead. Not supported for v2 providers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Token instance, token profile, key or key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "200", description = "List of Attributes retrieved"),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "501",
                    description = "Legacy attribute discovery is not supported for v2 providers; use the "
                            + "operation-specific attribute endpoints.",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(
            path = "/tokenProfiles/{tokenProfileUuid}/keys/{uuid}/items/{keyItemUuid}/signature/{algorithm}/attributes",
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listSignatureAttributes(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Key instance UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid,
            @Parameter(description = "Cryptographic algorithm") @PathVariable KeyAlgorithm algorithm)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "List signing attributes",
            description = "Returns the signing attribute schema for the specified token, profile and key item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sign attribute schema retrieved"),
            @ApiResponse(responseCode = "404", description = "Token instance, token profile, key or key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/tokenProfiles/{tokenProfileUuid}/keys/{uuid}/items/{keyItemUuid}/sign/attributes",
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listSignAttributes(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Sign data using a Key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data signed"),
            @ApiResponse(responseCode = "404", description = "Token instance, token profile, key or key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(path = "/tokenProfiles/{tokenProfileUuid}/keys/{uuid}/items/{keyItemUuid}/sign",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    SignDataResponseDto signData(@Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid,
            @RequestBody SignDataRequestDto request) throws ConnectorException, NotFoundException;

    @Operation(summary = "List verification attributes",
            description = "Returns the verification attribute schema for the specified token, profile and key item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verify attribute schema retrieved"),
            @ApiResponse(responseCode = "404", description = "Token instance, token profile, key or key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/tokenProfiles/{tokenProfileUuid}/keys/{uuid}/items/{keyItemUuid}/verify/attributes",
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listVerifyAttributes(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Verify data using a Key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Data verified"),
            @ApiResponse(responseCode = "404", description = "Token instance, token profile, key or key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(path = "/tokenProfiles/{tokenProfileUuid}/keys/{uuid}/items/{keyItemUuid}/verify",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    VerifyDataResponseDto verifyData(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid,
            @RequestBody VerifyDataRequestDto request) throws ConnectorException, NotFoundException;

    /////////////////////////////////////////////////////////////////////////////////
    // generate random operations
    /// //////////////////////////////////////////////////////////////////////////////

    @Operation(summary = "List of random generator Attributes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "200", description = "List of Attributes retrieved"),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/random/attributes", produces = {"application/json"})
    List<BaseAttribute> listRandomAttributes(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Generate random data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Random data generated"),
            @ApiResponse(responseCode = "404", description = "Token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(path = "/random", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    RandomDataResponseDto randomData(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @RequestBody RandomDataRequestDto request) throws ConnectorException, NotFoundException;
}
