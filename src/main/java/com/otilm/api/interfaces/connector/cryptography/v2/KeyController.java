package com.otilm.api.interfaces.connector.cryptography.v2;

import com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Connector-facing V2 key-management interface.
 *
 * <p>Requests carry the token and token-profile context. Existing-key operations additionally carry
 * connector-defined key metadata. Key creation and destruction support synchronous and asynchronous execution.
 * Asynchronous polling and cancellation are available when
 * {@link com.otilm.api.model.client.connector.v2.FeatureFlag#KEY_OPERATION_POLLING} is enabled.</p>
 */
@RequestMapping("/v2/cryptographyProvider/keys")
@Tag(name = "Key Management v2",
        description = "Key operations scoped by token, token-profile and key metadata supplied in requests")
public interface KeyController extends AuthProtectedConnectorController {

    // ---- Create ----

    @Operation(summary = "List supported key request types",
            description = "List key types supported for the supplied token and token-profile context")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Supported key types retrieved"))
    @PostMapping(path = "/types", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<@NotNull KeyRequestType> listSupportedKeyRequestTypes(
            @RequestBody @Valid TokenProfileScopedRequestV2Dto request);

    @Operation(summary = "List secret key creation attributes",
            description = "List attributes for creating a secret key. Definitions must not contain resolved "
                    + "credentials or secret values.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Schema retrieved"))
    @PostMapping(path = "/secret/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listCreateSecretKeyAttributes(@RequestBody @Valid TokenProfileScopedRequestV2Dto request);

    @Operation(summary = "Create secret key", description = "Create a secret key using the caller-selected execution mode (synchronous 200 or asynchronous 202)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created synchronously"),
            @ApiResponse(responseCode = "202", description = "Creation accepted asynchronously; body carries operationMeta tracking handle"),
            @ApiResponse(responseCode = "409",
                    description = "Connector detected reuse of keyCreationId with materially different creation data (RESOURCE_ALREADY_EXISTS)")
    })
    @PostMapping(path = "/secret", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SecretKeyDataResponseV2Dto> createSecretKey(@RequestBody @Valid CreateKeyRequestV2Dto request);

    @Operation(summary = "List key pair creation attributes",
            description = "List attributes for creating a key pair. Definitions must not contain resolved "
                    + "credentials or secret values.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Schema retrieved"))
    @PostMapping(path = "/pair/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listCreateKeyPairAttributes(@RequestBody @Valid TokenProfileScopedRequestV2Dto request);

    @Operation(summary = "Create key pair", description = "Create a key pair using the caller-selected execution mode (synchronous 200 or asynchronous 202)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created synchronously"),
            @ApiResponse(responseCode = "202", description = "Creation accepted asynchronously; body carries operationMeta tracking handle"),
            @ApiResponse(responseCode = "409",
                    description = "Connector detected reuse of keyCreationId with materially different creation data (RESOURCE_ALREADY_EXISTS)")
    })
    @PostMapping(path = "/pair", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<KeyPairDataResponseV2Dto> createKeyPair(@RequestBody @Valid CreateKeyRequestV2Dto request);

    @Operation(summary = "Get async key creation status", description = "Get status of an async secret key or key pair creation")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Key creation status retrieved"))
    @PostMapping(path = "/create/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    KeyOperationStatusResponseV2Dto getCreateKeyStatus(@RequestBody @Valid KeyOperationStatusRequestV2Dto request);

    @Operation(summary = "Cancel async key creation", description = "Cancel an in-flight async secret key or key pair creation")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Aborted"),
            @ApiResponse(responseCode = "404", description = "Operation not tracked — treat as terminal cancellation"),
            @ApiResponse(responseCode = "422", description = "Refused — past point of no return")
    })
    @PostMapping(path = "/create/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancelCreateKey(@RequestBody @Valid KeyOperationCancelRequestV2Dto request);

    // ---- Destroy ----

    @Operation(summary = "Destroy key", description = "Destroy a key using the caller-selected execution mode (synchronous 204 or asynchronous 202)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Destroyed synchronously"),
            @ApiResponse(responseCode = "202", description = "Destruction accepted asynchronously; body carries "
                    + "operationMeta tracking handle")
    })
    @PostMapping(path = "/destroy", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<KeyDataResponseV2Dto> destroyKey(@RequestBody @Valid DestroyKeyRequestV2Dto request);

    @Operation(summary = "Get async key destruction status", description = "Get status of an async key destruction")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Key destruction status retrieved"))
    @PostMapping(path = "/destroy/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    KeyOperationStatusResponseV2Dto getDestroyKeyStatus(@RequestBody @Valid KeyOperationStatusRequestV2Dto request);

    @Operation(summary = "Cancel async key destruction",
            description = "Cancel an in-flight asynchronous key destruction")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Aborted"),
            @ApiResponse(responseCode = "404", description = "Operation not tracked — treat as terminal cancellation"),
            @ApiResponse(responseCode = "422", description = "Refused — past point of no return")
    })
    @PostMapping(path = "/destroy/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancelDestroyKey(@RequestBody @Valid KeyOperationCancelRequestV2Dto request);
}
