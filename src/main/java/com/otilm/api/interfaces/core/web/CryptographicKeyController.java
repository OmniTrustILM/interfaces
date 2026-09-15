package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.client.cryptography.CryptographicKeyResponseDto;
import com.otilm.api.model.client.cryptography.key.BulkCompromiseKeyItemRequestDto;
import com.otilm.api.model.client.cryptography.key.BulkCompromiseKeyRequestDto;
import com.otilm.api.model.client.cryptography.key.BulkKeyItemUsageRequestDto;
import com.otilm.api.model.client.cryptography.key.BulkKeyUsageRequestDto;
import com.otilm.api.model.client.cryptography.key.CompromiseKeyRequestDto;
import com.otilm.api.model.client.cryptography.key.EditKeyItemDto;
import com.otilm.api.model.client.cryptography.key.EditKeyRequestDto;
import com.otilm.api.model.client.cryptography.key.KeyExportRequestDto;
import com.otilm.api.model.client.cryptography.key.KeyImportRequestDto;
import com.otilm.api.model.client.cryptography.key.KeyRequestDto;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.client.cryptography.key.UpdateKeyUsageRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.core.cryptography.key.KeyDetailDto;
import com.otilm.api.model.core.cryptography.key.KeyDto;
import com.otilm.api.model.core.cryptography.key.KeyEventHistoryDto;
import com.otilm.api.model.core.cryptography.key.KeyItemDetailDto;
import com.otilm.api.model.core.search.ConfigurableColumnsDocs;
import com.otilm.api.model.core.search.SearchFieldDataByGroupDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/v1")
@Tag(name = "Cryptographic Key Management", description = "Cryptographic Key Management API")
public interface CryptographicKeyController extends AuthProtectedController {

    // Token Instance Operation APIs
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // List and Detail Operation
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(operationId = "getCryptographicKeySearchableFields",
            summary = "Get CryptographicKey searchable fields information",
            description = ConfigurableColumnsDocs.CATALOGUE_FLAGS)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "CryptographicKey searchable field information retrieved")})
    @GetMapping(path = "/keys/search", produces = {"application/json"})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(summary = "List cryptographic keys",
            description = ConfigurableColumnsDocs.SORT_AND_COLUMNS + ConfigurableColumnsDocs.ATTRIBUTE_PROJECTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all the cryptographic keys"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/keys", produces = MediaType.APPLICATION_JSON_VALUE)
    CryptographicKeyResponseDto listCryptographicKeys(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(schema = @Schema(implementation = SearchRequestDto.class),
                    examples = {@ExampleObject(name = "With ordering and columns", value = """
                            {
                              "pageNumber": 1,
                              "itemsPerPage": 10,
                              "filters": [],
                              "sort": {"fieldSource": "property", "fieldIdentifier": "CKI_NAME", "direction": "asc"},
                              "columns": [
                                {"fieldSource": "property", "fieldIdentifier": "CKI_NAME"},
                                {"fieldSource": "property", "fieldIdentifier": "CKI_STATE"},
                                {"fieldSource": "custom", "fieldIdentifier": "businessUnit|STRING"}
                              ]
                            }""")})) @Valid @RequestBody SearchRequestDto request);

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "List Cryptographic Keys with full Key Pairs",
            description = "This API contains the logic to get the keys that contains the full key pair (private and public Key)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Cryptographic Keys retrieved")})
    @GetMapping(path = "/keys/pairs", produces = MediaType.APPLICATION_JSON_VALUE)
    List<KeyDto> listKeyPairs(@RequestParam(required = false) Optional<String> tokenProfileUuid);

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * @deprecated
     */
    @Deprecated(since = "2.14.1", forRemoval = true)
    @Operation(deprecated = true, operationId = "getKeyWithToken",
            summary = "Get Cryptographic Key Detail with Token Instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cryptographic Key detail retrieved"),
            @ApiResponse(responseCode = "404", description = "Cryptographic Key not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @GetMapping(path = "/tokens/{tokenInstanceUuid}/keys/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    KeyDetailDto getKey(@Parameter(description = "UUID of the Token Instance") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "UUID of the Key") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Get Cryptographic Key Detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cryptographic Key detail retrieved"),
            @ApiResponse(responseCode = "404", description = "Cryptographic Key not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @GetMapping(path = "/keys/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    KeyDetailDto getKey(@Parameter(description = "UUID of the Key") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Get Cryptographic Key Detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cryptographic Key item detail retrieved"),
            @ApiResponse(responseCode = "404", description = "Cryptographic Key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @GetMapping(path = "/keys/{uuid}/items/{keyItemUuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    KeyItemDetailDto getKeyItem(@Parameter(description = "UUID of the Key") @PathVariable String uuid,
            @Parameter(description = "UUID of the Key Item") @PathVariable String keyItemUuid) throws NotFoundException;

    /**
     * @deprecated
     */
    @Deprecated(since = "2.14.1", forRemoval = true)
    @Operation(deprecated = true, operationId = "getKeyItemWithToken",
            summary = "Get Cryptographic Key Detail with Token Instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cryptographic Key detail retrieved"),
            @ApiResponse(responseCode = "404", description = "Cryptographic Key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

    })
    @GetMapping(path = "/tokens/{tokenInstanceUuid}/keys/{uuid}/items/{keyItemUuid}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    KeyItemDetailDto getKeyItem(
            @Parameter(description = "UUID of the Token Instance") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "UUID of the Key") @PathVariable String uuid,
            @Parameter(description = "UUID of the Key Item") @PathVariable String keyItemUuid) throws NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // Create and Update Operation
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Create a new Cryptographic Key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "201", description = "Cryptographic Key Created Successfully"),
            @ApiResponse(responseCode = "404", description = "Token profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/tokens/{tokenInstanceUuid}/tokenProfiles/{tokenProfileUuid}/keys/{type}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    KeyDetailDto createKey(
            @Parameter(description = "UUID of the Token Instance") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "UUID of the Token Profile") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Type of the key to be created") @PathVariable KeyRequestType type,
            @RequestBody @Valid KeyRequestDto request) throws AlreadyExistException, ValidationException,
            ConnectorException, AttributeException, NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // Key Material Import and Export
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "List key import attributes", description = """
            Attributes the provider behind this token profile requires to import a key of the given type.

            Present so a caller can render the import form before it uploads anything. The result is the provider's own
            schema and never contains resolved credentials or secret values.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Import attribute schema retrieved"),
            @ApiResponse(responseCode = "404", description = "Token profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/tokens/{tokenInstanceUuid}/tokenProfiles/{tokenProfileUuid}/keys/{type}/import/attributes",
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listImportKeyAttributes(
            @Parameter(description = "UUID of the Token Instance") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "UUID of the Token Profile") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Type of the key to be imported") @PathVariable KeyRequestType type)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "List key export attributes", description = """
            Attributes the provider holding this key requires to export it.

            Present so a caller can render the export form before it asks for the key. The result is the provider's own
            schema and never contains resolved credentials or secret values.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Export attribute schema retrieved"),
            @ApiResponse(responseCode = "404", description = "Key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/keys/{uuid}/items/{keyItemUuid}/export/attributes",
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listExportKeyAttributes(@Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Import a key", description = """
            Import a key from an uploaded file into the token profile.

            The file travels base64-encoded in `file`, like every upload in the platform, and is held in memory rather
            than written anywhere; it is never echoed in an error, since it carries key material. `inputPassphrase`
            opens the uploaded file and is absent for a file that carries no protection of its own; the platform
            re-protects the material before it reaches the provider, so neither the file nor that passphrase is ever
            forwarded.

            Certificates found alongside a key are not imported here. Use the certificate import operation for a file
            that carries both.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Key imported"),
            @ApiResponse(responseCode = "404", description = "Token profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/tokens/{tokenInstanceUuid}/tokenProfiles/{tokenProfileUuid}/keys/{type}/import",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    KeyDetailDto importKey(
            @Parameter(description = "UUID of the Token Instance") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "UUID of the Token Profile") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Type of the key to be imported") @PathVariable KeyRequestType type,
            @RequestBody @Valid KeyImportRequestDto request) throws AlreadyExistException, ValidationException,
            ConnectorException, AttributeException, NotFoundException;

    @Operation(summary = "Export a key", description = """
            Export a key item as a protected PKCS#8 file in PEM form.

            This is a POST because the passphrase travels in the body: a URL is recorded by proxies, browser history
            and access logs, so a passphrase must never appear in one. The response is not cacheable and carries a
            sanitized download filename.

            Only a key created or imported as exportable can be exported.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Key exported",
                    headers = {
                            @Header(name = HttpHeaders.CONTENT_DISPOSITION, required = true,
                                    description = "Attachment with a sanitized filename, as RFC 6266 describes",
                                    schema = @Schema(type = "string")),
                            @Header(name = HttpHeaders.CACHE_CONTROL, required = true,
                                    description = "`no-store, no-cache`, so the file is not written to any cache",
                                    schema = @Schema(type = "string")),
                            @Header(name = HttpHeaders.PRAGMA, required = true,
                                    description = "`no-cache`, for caches that predate `Cache-Control`",
                                    schema = @Schema(type = "string")),
                            @Header(name = "X-Content-Type-Options", required = true, description = "`nosniff`",
                                    schema = @Schema(type = "string"))},
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/keys/{uuid}/items/{keyItemUuid}/export", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<Resource> exportKey(@Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid,
            @RequestBody @Valid KeyExportRequestDto request)
            throws ConnectorException, AttributeException, NotFoundException;

    @Operation(summary = "Disable export for a key item", description = """
            Make a key item non-exportable, by setting its `exportable` flag to false.

            There is no operation for the other direction. A key created or imported as non-exportable carries the
            guarantee that its material never leaves, and an operation lifting the flag would void that guarantee for
            every key that already exists. The technologies behind the providers set extractability once, at creation,
            for the same reason. A key that has to be exportable is created or imported as exportable.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Key item is no longer exportable"),
            @ApiResponse(responseCode = "404", description = "Key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping(path = "/keys/{uuid}/items/{keyItemUuid}/export/disable", produces = MediaType.APPLICATION_JSON_VALUE)
    KeyItemDetailDto disableKeyExport(@Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid) throws NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Edit Key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "200", description = "Key updated"),
            @ApiResponse(responseCode = "404", description = "Key or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PutMapping(path = "/keys/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    KeyDetailDto editKey(@Parameter(description = "Key UUID") @PathVariable String uuid,
            @RequestBody EditKeyRequestDto request) throws ConnectorException, AttributeException, NotFoundException;

    @Operation(summary = "Edit Key Item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Key Item updated"),
            @ApiResponse(responseCode = "404", description = "Key item or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PatchMapping(path = "/keys/{uuid}/items/{keyItemUuid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    KeyItemDetailDto editKeyItem(@Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid,
            @RequestBody EditKeyItemDto request) throws NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // Sync Keys
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Sync Keys from connector")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "204", description = "Key sync completed"),
            @ApiResponse(responseCode = "404", description = "Token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @PatchMapping(path = "/tokens/{tokenInstanceUuid}/sync")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void syncKeys(@Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid)
            throws ConnectorException, AttributeException, NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // Compromise
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Mark Key and its Items as Compromised",
            description = "If the request body is provided with the UUID of the items of Key, then only those items"
                    + "will be compromised. Else all the sub items of the key will be compromised")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Key marked as compromised"),
            @ApiResponse(responseCode = "404", description = "Key or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @PatchMapping(path = "/keys/{uuid}/compromise", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void compromiseKey(@Parameter(description = "Key UUID") @PathVariable String uuid,
            @RequestBody CompromiseKeyRequestDto request) throws NotFoundException;

    /**
     * @deprecated
     */
    @Deprecated(since = "2.14.1", forRemoval = true)
    @Operation(deprecated = true, operationId = "compromiseKeyWithToken",
            summary = "Mark Key and its Items as Compromised with Token Instance",
            description = "If the request body is provided with the UUID of the items of Key, then only those items"
                    + "will be compromised. Else all the sub items of the key will be compromised")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Key marked as compromised"),
            @ApiResponse(responseCode = "404", description = "Key or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @PatchMapping(path = "/tokens/{tokenInstanceUuid}/keys/{uuid}/compromise",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void compromiseKey(@Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @RequestBody CompromiseKeyRequestDto request) throws NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Mark Multiple Key and all its Items as Compromised",
            description = "This API can be used to mark multiple keys and its sub items to be marked as compromised."
                    + "Specific part of the key cannot be mentioned in this API")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Key marked as compromised")})
    @PatchMapping(path = "/keys/compromise", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void compromiseKeys(@RequestBody BulkCompromiseKeyRequestDto request);

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Mark Multiple Key Items as Compromised",
            description = "This API can be used to mark multiple keys items to be marked as compromised.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Key Items marked as compromised"),
            @ApiResponse(responseCode = "404", description = "Parent cryptographic key not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422",
                    description = "One or more requested key items do not exist. The entire batch is rejected "
                            + "and no key items are updated.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))})
    @PatchMapping(path = "/keys/items/compromise", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void compromiseKeyItems(@RequestBody BulkCompromiseKeyItemRequestDto request) throws NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // Destroy
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Destroy Cryptographic Key",
            description = "If the request body provided, only those key items will be destroyed. If the request body is "
                    + "not provided or given empty, then the entire key will be destroyed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "204", description = "Keys destroyed"),
            @ApiResponse(responseCode = "404", description = "Key or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @PatchMapping(path = "/keys/{uuid}/destroy", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void destroyKey(@Parameter(description = "Key UUID") @PathVariable String uuid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key UUIDs", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {
                            @ExampleObject(
                                    value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody(
                                            required = false) List<String> keyItemUuids)
            throws ConnectorException, NotFoundException;

    /**
     * @deprecated
     */
    @Deprecated(since = "2.14.1", forRemoval = true)
    @Operation(deprecated = true, operationId = "destroyKeyWithToken",
            summary = "Destroy Cryptographic Key with Token Instance",
            description = "If the request body provided, only those key items will be destroyed. If the request body is "
                    + "not provided or given empty, then the entire key will be destroyed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "204", description = "Keys destroyed"),
            @ApiResponse(responseCode = "404", description = "Key or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @PatchMapping(path = "/tokens/{tokenInstanceUuid}/keys/{uuid}/destroy", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void destroyKey(@Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key UUIDs", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {
                            @ExampleObject(
                                    value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody(
                                            required = false) List<String> keyItemUuids)
            throws ConnectorException, NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Destroy Multiple Cryptographic Key and its items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "204", description = "Keys destroyed"),
            @ApiResponse(responseCode = "404", description = "Key not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @PatchMapping(path = "/keys/destroy", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void destroyKeys(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> keyUuids)
            throws ConnectorException, NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Destroy Multiple Cryptographic Key items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "Parent cryptographic key not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422",
                    description = "One or more requested key items do not exist. The entire batch is rejected "
                            + "and no key items are updated.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))),
            @ApiResponse(responseCode = "204", description = "Keys Items destroyed")})
    @PatchMapping(path = "/keys/items/destroy", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void destroyKeyItems(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key Item UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> keyItemUuids)
            throws ConnectorException, NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // Delete
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Delete Cryptographic Key",
            description = "If the request body provided, only those key items will be deleted. If the request body is "
                    + "not provided or given empty, then the entire key will be destroyed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "204", description = "Key deleted"),
            @ApiResponse(responseCode = "404", description = "Key or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @DeleteMapping(path = "/keys/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteKey(@Parameter(description = "Key UUID") @PathVariable String uuid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key Item UUIDs", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {
                            @ExampleObject(
                                    value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody(
                                            required = false) List<String> keyItemUuids)
            throws ConnectorException, NotFoundException;

    /**
     * @deprecated
     */
    @Deprecated(since = "2.14.1", forRemoval = true)
    @Operation(deprecated = true, operationId = "deleteKeyWithToken",
            summary = "Delete Cryptographic Key with Token Instance",
            description = "If the request body provided, only those key items will be deleted. If the request body is "
                    + "not provided or given empty, then the entire key will be destroyed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "204", description = "Key deleted"),
            @ApiResponse(responseCode = "404", description = "Key or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @DeleteMapping(path = "/tokens/{tokenInstanceUuid}/keys/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteKey(@Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key Item UUIDs", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {
                            @ExampleObject(
                                    value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody(
                                            required = false) List<String> keyItemUuids)
            throws ConnectorException, NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Delete Multiple Cryptographic Key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "204", description = "Keys deleted")})
    @DeleteMapping(path = "/keys", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteKeys(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> keyUuids)
            throws ConnectorException;

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Delete Multiple Cryptographic Key Items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "204", description = "Key Items deleted")})
    @DeleteMapping(path = "/keys/items", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteKeyItems(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key Items UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> keyItemUuids)
            throws ConnectorException;

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // Enable
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Enable Key",
            description = "If the request body provided, only those key items will be enabled. If the request body is "
                    + "not provided or given empty, then the entire key will be enabled")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Key enabled"),
            @ApiResponse(responseCode = "404", description = "Key or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @PatchMapping(path = "/keys/{uuid}/enable", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableKey(@Parameter(description = "Key UUID") @PathVariable String uuid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key Item UUIDs", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {
                            @ExampleObject(
                                    value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody(
                                            required = false) List<String> keyItemUuids)
            throws NotFoundException;

    /**
     * @deprecated
     */
    @Deprecated(since = "2.14.1", forRemoval = true)
    @Operation(deprecated = true, operationId = "enableKeyWithToken", summary = "Enable Key with Token Instance",
            description = "If the request body provided, only those key items will be enabled. If the request body is "
                    + "not provided or given empty, then the entire key will be enabled")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Key enabled"),
            @ApiResponse(responseCode = "404", description = "Key or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @PatchMapping(path = "/tokens/{tokenInstanceUuid}/keys/{uuid}/enable", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableKey(@Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key Item UUIDs", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {
                            @ExampleObject(
                                    value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody(
                                            required = false) List<String> keyItemUuids)
            throws NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Enable multiple Keys")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Keys enabled")})
    @PatchMapping(path = "/keys/enable", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableKeys(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids);

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Enable multiple Key Items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Key Items enabled"),
            @ApiResponse(responseCode = "404", description = "Parent cryptographic key not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422",
                    description = "One or more requested key items do not exist. The entire batch is rejected "
                            + "and no key items are updated.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))})
    @PatchMapping(path = "/keys/items/enable", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableKeyItems(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key Item UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids)
            throws NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // Disable
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Disable Key",
            description = "If the request body provided, only those key items will be disabled. If the request body is "
                    + "not provided or given empty, then the entire key will be disabled")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Key disabled"),
            @ApiResponse(responseCode = "404", description = "Key or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @PatchMapping(path = "/keys/{uuid}/disable", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableKey(@Parameter(description = "Key UUID") @PathVariable String uuid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key Item UUIDs", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {
                            @ExampleObject(
                                    value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody(
                                            required = false) List<String> keyItemUuids)
            throws NotFoundException;

    /**
     * @deprecated
     */
    @Deprecated(since = "2.14.1", forRemoval = true)
    @Operation(deprecated = true, operationId = "disableKeyWithToken", summary = "Disable Key with Token Instance",
            description = "If the request body provided, only those key items will be disabled. If the request body is "
                    + "not provided or given empty, then the entire key will be disabled")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Key disabled"),
            @ApiResponse(responseCode = "404", description = "Key or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @PatchMapping(path = "/tokens/{tokenInstanceUuid}/keys/{uuid}/disable", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableKey(@Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key Item UUIDs", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {
                            @ExampleObject(
                                    value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody(
                                            required = false) List<String> keyItemUuids)
            throws NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Disable multiple Keys")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Keys disabled")})
    @PatchMapping(path = "/keys/disable", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableKeys(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids);

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Disable multiple Key Items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Key Items disabled"),
            @ApiResponse(responseCode = "404", description = "Parent cryptographic key not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422",
                    description = "One or more requested key items do not exist. The entire batch is rejected "
                            + "and no key items are updated.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))})
    @PatchMapping(path = "/keys/items/disable", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableKeyItems(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Key Item UUIDs",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)), examples = {
                    @ExampleObject(
                            value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids)
            throws NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // Usages
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Update Key Usage",
            description = "If the request body provided, only those key items will be updated. If the request body is "
                    + "not provided or given empty, then the entire key will be updated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Keys Usages Updates"),
            @ApiResponse(responseCode = "404", description = "Key or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @PutMapping(path = "/keys/{uuid}/usages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateKeyUsages(@Parameter(description = "Key UUID") @PathVariable String uuid,
            @RequestBody UpdateKeyUsageRequestDto request) throws NotFoundException, ValidationException;

    /**
     * @deprecated
     */
    @Deprecated(since = "2.14.1", forRemoval = true)
    @Operation(deprecated = true, operationId = "updateKeyUsagesWithToken",
            summary = "Update Key Usage with Token Instance",
            description = "If the request body provided, only those key items will be updated. If the request body is "
                    + "not provided or given empty, then the entire key will be updated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Keys Usages Updates"),
            @ApiResponse(responseCode = "404", description = "Key or token instance not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @PutMapping(path = "/tokens/{tokenInstanceUuid}/keys/{uuid}/usages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateKeyUsages(@Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @RequestBody UpdateKeyUsageRequestDto request) throws NotFoundException, ValidationException;

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Update Key Usages for Multiple Keys",
            description = "Update the key usages for multiple keys and all the items inside it")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Keys Usages Updated")})
    @PutMapping(path = "/keys/usages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateKeysUsages(@RequestBody BulkKeyUsageRequestDto request);

    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Update Key Usages for Multiple Key Items",
            description = "Update the key usages for multiple keys Items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Key Items Usages Updated"),
            @ApiResponse(responseCode = "404", description = "Parent cryptographic key not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422",
                    description = "One or more requested key items do not exist. The entire batch is rejected "
                            + "and no key items are updated.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))})
    @PutMapping(path = "/keys/items/usages", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateKeyItemUsages(@RequestBody BulkKeyItemUsageRequestDto request) throws NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // Attribute related API
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "List of Attributes to create a Key")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "502", description = "Connector Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),

            @ApiResponse(responseCode = "200", description = "List of Attributes retrieved"),
            @ApiResponse(responseCode = "404", description = "Token profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @GetMapping(path = "/tokens/{tokenInstanceUuid}/tokenProfiles/{tokenProfileUuid}/keys/{type}/attributes",
            produces = {"application/json"})
    List<BaseAttribute> listCreateKeyAttributes(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Token Profile UUID") @PathVariable String tokenProfileUuid,
            @Parameter(description = "Type of the key to be created") @PathVariable KeyRequestType type)
            throws ConnectorException, NotFoundException;

    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------
    // History API
    // -----------------------------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    @Operation(summary = "Get Key Item event history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificate event history retrieved"),
            @ApiResponse(responseCode = "404", description = "Key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @GetMapping(path = "/keys/{uuid}/items/{keyItemUuid}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    List<KeyEventHistoryDto> getEventHistory(@Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid) throws NotFoundException;

    /**
     * @deprecated
     */
    @Deprecated(since = "2.14.1", forRemoval = true)
    @Operation(deprecated = true, operationId = "getEventHistoryWithToken",
            summary = "Get Key Item event history with Token Instance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificate event history retrieved"),
            @ApiResponse(responseCode = "404", description = "Key item not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
    @GetMapping(path = "/tokens/{tokenInstanceUuid}/keys/{uuid}/items/{keyItemUuid}/history",
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<KeyEventHistoryDto> getEventHistory(
            @Parameter(description = "Token Instance UUID") @PathVariable String tokenInstanceUuid,
            @Parameter(description = "Key UUID") @PathVariable String uuid,
            @Parameter(description = "Key Item UUID") @PathVariable String keyItemUuid) throws NotFoundException;
}
