package com.otilm.api.interfaces.connector.cryptography.v2;

import com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.OperationTrackingRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.CreateKeyAttributesRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.CreateKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.DestroyKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyAttributesRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyResultRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyDestructionStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyOperationResponseV2Dto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Connector-facing V2 key-management interface.
 *
 * <p>
 * Requests carry the token and token-profile context. Existing-key operations additionally carry connector-defined key
 * metadata. Key creation and destruction support synchronous and asynchronous execution. Asynchronous polling and
 * cancellation are available when {@link com.otilm.api.model.client.connector.v2.FeatureFlag#ASYNCHRONOUS} is enabled.
 * </p>
 *
 * <p>
 * Key import is available when {@link com.otilm.api.model.client.connector.v2.FeatureFlag#KEY_IMPORT} is enabled. It
 * reuses the key-creation response shapes because the outcome is the same: a key now exists in the technology and the
 * platform receives its public part and its handle.
 * </p>
 *
 * <p>
 * Key export is available when {@link com.otilm.api.model.client.connector.v2.FeatureFlag#KEY_EXPORT} is enabled. Key
 * material only ever leaves a connector protected under a caller-supplied passphrase, and certificates are not part of
 * this interface: the platform holds them and assembles any user-facing container itself.
 * </p>
 */
@RequestMapping("/v2/cryptographyProvider/keys")
@Tag(name = "Key Management v2",
        description = "Key operations scoped by token, token-profile and key metadata supplied in requests")
@ApiResponses(@ApiResponse(responseCode = "400",
        description = "Request body cannot be read (errorCode BAD_REQUEST), including malformed JSON, "
                + "unknown properties, and values outside the published enum values",
        content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetailExtended.class))))
public interface KeyController extends AuthProtectedConnectorController {

    // ---- Create ----

    @Operation(summary = "List key creation attributes", description = """
            List attributes for creating the requested key type. Definitions must not contain resolved credentials or
            secret values.

            A connector that declares `keyExport` must include the reserved `keyExportable` attribute here: a v3
            data attribute named `keyExportable` with boolean content, exactly one content item, required, defaulting
            to `false`. That is how the exportable intent reaches key creation, so the create request needs no field
            of its own for it, and it takes part in `keyCreationId` replay equivalence like any other create-key
            attribute. A connector that does not declare `keyExport` must not publish it and is never sent it, and
            must create keys non-extractable wherever the technology expresses the distinction.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schema retrieved"),
            @ApiResponse(responseCode = "422",
                    description = "Request body was read successfully but violates a field validation rule "
                            + "(errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/create/attributes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listCreateKeyAttributes(@RequestBody @Valid CreateKeyAttributesRequestV2Dto request);

    @Operation(summary = "Create key", description = "Create a secret key or key pair synchronously or asynchronously")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Created synchronously, or existing equivalent operation returned synchronously"),
            @ApiResponse(responseCode = "202",
                    description = "Creation accepted asynchronously, or equivalent operation replayed asynchronously; "
                            + "body carries the original operationMeta tracking handle"),
            @ApiResponse(responseCode = "409",
                    description = "keyCreationId reused with a non-equivalent request (RESOURCE_ALREADY_EXISTS)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422", description = """
                    - `EXPORTABLE_NOT_SUPPORTED` — the reserved `keyExportable` attribute asked for an exportable key
                      and this token cannot hold one. A connector must refuse rather than create a key whose flag it
                      cannot deliver.
                    - `VALIDATION_FAILED` — the body is readable but breaks a field rule.
                    """,
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<KeyCreationResponseV2Dto> createKey(@RequestBody @Valid CreateKeyRequestV2Dto request);

    @Operation(summary = "Get async key creation status",
            description = "Get status of an async secret key or key pair creation using only its tracking handle")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Key creation status retrieved"),
            @ApiResponse(responseCode = "404", description = "Operation is not tracked"),
            @ApiResponse(responseCode = "422",
                    description = "Request body was read successfully but violates a field validation rule "
                            + "(errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/create/status", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    KeyCreationStatusResponseV2Dto getCreateKeyStatus(@RequestBody @Valid OperationTrackingRequestV2Dto request);

    @Operation(summary = "Cancel async key creation",
            description = "Cancel an in-flight async secret key or key pair creation using only its tracking handle")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Aborted"),
            @ApiResponse(responseCode = "404", description = "Operation not tracked"),
            @ApiResponse(responseCode = "422",
                    description = "Cancellation cannot be processed: the operation is already terminal or past the "
                            + "point of no return (errorCode OPERATION_PAST_POINT_OF_NO_RETURN), or the request body "
                            + "violates a field validation rule (errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/create/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancelCreateKey(@RequestBody @Valid OperationTrackingRequestV2Dto request);

    // ---- Destroy ----

    @Operation(summary = "Destroy key", description = "Destroy a key using the caller-selected execution mode "
            + "(synchronous 200 or asynchronous 202). After accepting asynchronous destruction, the connector must "
            + "reject new cryptographic operations for that key")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Destroyed synchronously; body contains an empty key-operation response"),
            @ApiResponse(responseCode = "202",
                    description = "Destruction accepted asynchronously; body carries "
                            + "operationMeta tracking handle"),
            @ApiResponse(responseCode = "422",
                    description = "Request body was read successfully but violates a field validation rule "
                            + "(errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/destroy", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<KeyOperationResponseV2Dto> destroyKey(@RequestBody @Valid DestroyKeyRequestV2Dto request);

    @Operation(summary = "Get async key destruction status",
            description = "Get status of an async key destruction using only its tracking handle")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Key destruction status retrieved"),
            @ApiResponse(responseCode = "404", description = "Operation is not tracked"),
            @ApiResponse(responseCode = "422",
                    description = "Request body was read successfully but violates a field validation rule "
                            + "(errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/destroy/status", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    KeyDestructionStatusResponseV2Dto getDestroyKeyStatus(@RequestBody @Valid OperationTrackingRequestV2Dto request);

    @Operation(summary = "Cancel async key destruction",
            description = "Cancel an in-flight asynchronous key destruction using only its tracking handle")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Aborted"),
            @ApiResponse(responseCode = "404", description = "Operation not tracked; cancellation outcome is unknown"),
            @ApiResponse(responseCode = "422",
                    description = "Cancellation cannot be processed: the operation is already terminal or past the "
                            + "point of no return (errorCode OPERATION_PAST_POINT_OF_NO_RETURN), or the request body "
                            + "violates a field validation rule (errorCode VALIDATION_FAILED)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/destroy/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancelDestroyKey(@RequestBody @Valid OperationTrackingRequestV2Dto request);

    // ---- Import ----

    @Operation(summary = "List importable key types",
            description = "List the key types the connector can import for the supplied token context, each with the "
                    + "algorithms it accepts, so material the token cannot hold is recognised before it is sent. Each "
                    + "key type appears at most once.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Importable key types retrieved"),
            @ApiResponse(responseCode = "422",
                    description = "`VALIDATION_FAILED` — the body is readable but breaks a field rule",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/import/keyTypes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<@NotNull ImportableKeyTypeV2Dto> listImportableKeyTypes(
            @RequestBody @Valid TokenProfileScopedRequestV2Dto request);

    @Operation(summary = "List key import attributes",
            description = "Returns the attribute schema used to import the requested key type. Definitions must not "
                    + "contain resolved credentials or secret values.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schema retrieved"),
            @ApiResponse(responseCode = "422",
                    description = "`VALIDATION_FAILED` — the body is readable but breaks a field rule",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/import/attributes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listImportKeyAttributes(@RequestBody @Valid ImportKeyAttributesRequestV2Dto request);

    @Operation(summary = "Import key", description = """
            Import protected key material synchronously or asynchronously.

            The `passphrase` protects this request alone and is generated by the platform, so it is never a user
            password. The connector decrypts `material`, stores the key bound to `keyReference`, and keeps no copy of
            the material or the passphrase. The response describes what was stored without carrying any of it: the
            derived public key of an imported key pair, the algorithm and length of an imported secret key.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Imported synchronously, or existing equivalent operation returned synchronously"),
            @ApiResponse(responseCode = "202",
                    description = "Import accepted asynchronously, or equivalent operation replayed asynchronously; "
                            + "body carries the original operationMeta tracking handle"),
            @ApiResponse(responseCode = "409",
                    description = "`RESOURCE_ALREADY_EXISTS` — `keyImportId` was reused with a request that is not "
                            + "equivalent to the first one",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422", description = """
                    - `KEY_TYPE_NOT_IMPORTABLE` — the key type or algorithm cannot be imported into this token.
                    - `KEY_MATERIAL_MISMATCH` — the decrypted material is not the declared key type or algorithm.
                    - `KEY_DECRYPTION_FAILED` — the material could not be decrypted with the supplied passphrase.
                    - `EXPORTABLE_NOT_SUPPORTED` — the token cannot hold a key that stays exportable.
                    - `VALIDATION_FAILED` — the body is readable but breaks a field rule, such as key material outside
                      the pinned protection profile.
                    """,
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/import", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<KeyCreationResponseV2Dto> importKey(@RequestBody @Valid ImportKeyRequestV2Dto request);

    @Operation(summary = "Get async key import status",
            description = "Get status of an async key import using only its tracking handle")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Key import status retrieved"),
            @ApiResponse(responseCode = "404",
                    description = "`OPERATION_NOT_TRACKED` — the connector is no longer tracking this operation",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "`VALIDATION_FAILED` — the body is readable but breaks a field rule",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/import/status", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    KeyCreationStatusResponseV2Dto getImportKeyStatus(@RequestBody @Valid OperationTrackingRequestV2Dto request);

    @Operation(summary = "Cancel async key import",
            description = "Cancel an in-flight async key import using only its tracking handle")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Aborted"),
            @ApiResponse(responseCode = "404",
                    description = "`OPERATION_NOT_TRACKED` — the connector is no longer tracking this operation, so "
                            + "the outcome of the cancellation is unknown",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422", description = """
                    - `OPERATION_PAST_POINT_OF_NO_RETURN` — the import is already terminal, or past the point where it
                      can be abandoned.
                    - `VALIDATION_FAILED` — the body is readable but breaks a field rule.
                    """,
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/import/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancelImportKey(@RequestBody @Valid OperationTrackingRequestV2Dto request);

    @Operation(summary = "Resolve a key import outcome", description = """
            Return the recorded state of an import identified by its `keyImportId`, so a caller that lost the original
            response can complete its own records or remove a key it cannot account for.

            - An import that is still running is reported with its current status rather than withheld, so a caller can
              poll here without holding the original tracking handle.
            - Records are kept for at least 24 hours after an import reaches a final state.
            - Within that period a missing record means the import was never accepted, which is the only case answered
              with HTTP 404.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Recorded import state retrieved, whether the import is still running or final"),
            @ApiResponse(responseCode = "404",
                    description = "`OPERATION_NOT_TRACKED` — no import was ever accepted for the supplied "
                            + "`keyImportId`",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "`VALIDATION_FAILED` — the body is readable but breaks a field rule",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/import/result", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    KeyCreationStatusResponseV2Dto getImportKeyResult(@RequestBody @Valid ImportKeyResultRequestV2Dto request);

    // ---- Export ----

    @Operation(summary = "List exportable key types",
            description = "List the key types the connector can export from the supplied token context, each with the "
                    + "algorithms it accepts, so a key the technology will never release is recognised before a user "
                    + "asks for it. Each key type appears at most once.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exportable key types retrieved"),
            @ApiResponse(responseCode = "422",
                    description = "`VALIDATION_FAILED` — the body is readable but breaks a field rule",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/export/keyTypes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<@NotNull ExportableKeyTypeV2Dto> listExportableKeyTypes(
            @RequestBody @Valid TokenProfileScopedRequestV2Dto request);

    @Operation(summary = "List key export attributes",
            description = "Returns the attribute schema used to export the key the metadata identifies. Definitions "
                    + "must not contain resolved credentials or secret values.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schema retrieved"),
            @ApiResponse(responseCode = "422",
                    description = "`VALIDATION_FAILED` — the body is readable but breaks a field rule",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/export/attributes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listExportKeyAttributes(@RequestBody @Valid KeyScopedRequestV2Dto request);

    @Operation(summary = "Export key", description = """
            Export the key `keyMeta` identifies as protected key material.

            Export is synchronous, so no protected material is ever held in asynchronous tracking state. The connector
            protects the material under the supplied `passphrase`, derives `keyData` from the material it is returning
            so the caller can check it against its own record of the key, and keeps no copy of the material or the
            passphrase.

            Only a key created or imported as exportable can be exported.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exported; body carries the protected key material"),
            @ApiResponse(responseCode = "422", description = """
                    - `KEY_NOT_EXPORTABLE` — the key was not created or imported as exportable.
                    - `KEY_TYPE_NOT_EXPORTABLE` — the key type or algorithm cannot be exported from this token.
                    - `KEY_MATERIAL_MISMATCH` — the key the metadata identifies is not the declared key type.
                    - `VALIDATION_FAILED` — the body is readable but breaks a field rule, such as an absent passphrase.
                    """,
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/export", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ExportKeyResponseV2Dto exportKey(@RequestBody @Valid ExportKeyRequestV2Dto request);
}
