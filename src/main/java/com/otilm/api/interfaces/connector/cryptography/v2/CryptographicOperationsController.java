package com.otilm.api.interfaces.connector.cryptography.v2;

import com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Connector-facing V2 cryptographic operations interface.
 *
 * <p>Requests carry the token, token-profile and key context required by each operation. Signing supports
 * synchronous and asynchronous execution when
 * {@link com.otilm.api.model.client.connector.v2.FeatureFlag#KEY_OPERATION_POLLING} is enabled. Encryption,
 * decryption, verification and random-data generation are synchronous.</p>
 */
@RequestMapping("/v2/cryptographyProvider/operations")
@Tag(name = "Cryptographic Operations v2",
        description = "Cryptographic operations scoped by token, token-profile and key metadata supplied in requests")
public interface CryptographicOperationsController extends AuthProtectedConnectorController {

    // ---- Cipher ----

    @Operation(summary = "List encryption attributes",
            description = "Returns the encryption parameter schema supported by the connector for the supplied token, profile and key context")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Encryption attributes retrieved"))
    @PostMapping(path = "/encrypt/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listEncryptAttributes(@RequestBody @Valid KeyScopedRequestV2Dto request);

    @Operation(summary = "Encrypt data", description = "Encrypt data with the given key (always synchronous)")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Data encrypted"))
    @PostMapping(path = "/encrypt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    EncryptDataResponseV2Dto encryptData(@RequestBody @Valid CipherDataRequestV2Dto request);

    @Operation(summary = "List decryption attributes",
            description = "Returns the decryption parameter schema supported by the connector for the supplied token, profile and key context")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Decryption attributes retrieved"))
    @PostMapping(path = "/decrypt/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listDecryptAttributes(@RequestBody @Valid KeyScopedRequestV2Dto request);

    @Operation(summary = "Decrypt data", description = "Decrypt data with the given key (always synchronous)")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Data decrypted"))
    @PostMapping(path = "/decrypt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    DecryptDataResponseV2Dto decryptData(@RequestBody @Valid CipherDataRequestV2Dto request);

    // ---- Sign ----

    @Operation(summary = "List signing attributes",
            description = "Returns the signing parameter schema supported by the connector for the supplied token, profile and key context")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Signing attributes retrieved"))
    @PostMapping(path = "/sign/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listSignAttributes(@RequestBody @Valid KeyScopedRequestV2Dto request);

    @Operation(summary = "Sign data",
            description = "Sign a batch using the caller-selected execution mode (synchronous 200 or asynchronous 202). "
                    + "The connector must not switch modes implicitly. The batch is tracked as one operation, "
                    + "so a 202 response carries one operationMeta handle for the whole batch")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Signed synchronously"),
            @ApiResponse(responseCode = "202", description = "Signing accepted asynchronously; body carries operationMeta tracking handle for the batch")
    })
    @PostMapping(path = "/sign", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<SignDataResponseV2Dto> signData(@RequestBody @Valid SignDataRequestV2Dto request);

    @Operation(summary = "Get async sign operation status",
            description = "Get per-item status of an async sign batch. There is no aggregate status: polling ends when "
                    + "every item is terminal, and the operation failed if any item failed")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Sign operation status retrieved"))
    @PostMapping(path = "/sign/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    SignOperationStatusResponseV2Dto getSignStatus(@RequestBody @Valid SignOperationScopedRequestV2Dto request);

    @Operation(summary = "Cancel async sign operation",
            description = "Cancel an in-flight asynchronous sign batch. Individual items cannot be cancelled separately")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Aborted"),
            @ApiResponse(responseCode = "404", description = "Operation not tracked — treat as terminal cancellation"),
            @ApiResponse(responseCode = "422", description = "Refused — past point of no return")
    })
    @PostMapping(path = "/sign/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Void> cancelSign(@RequestBody @Valid SignOperationScopedRequestV2Dto request);

    // ---- Verify ----

    @Operation(summary = "List verification attributes",
            description = "Returns the verification parameter schema supported by the connector for the supplied token, profile and key context")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Verification attributes retrieved"))
    @PostMapping(path = "/verify/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listVerifyAttributes(@RequestBody @Valid KeyScopedRequestV2Dto request);

    @Operation(summary = "Verify data", description = "Verify signatures with the given key (always synchronous)")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Signatures verified"))
    @PostMapping(path = "/verify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    VerifyDataResponseV2Dto verifyData(@RequestBody @Valid VerifyDataRequestV2Dto request);

    // ---- Random ----

    @Operation(summary = "List random generator attributes", description = "Random generator attribute schema given token context")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Random generator attributes retrieved"))
    @PostMapping(path = "/random/attributes", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listRandomAttributes(@RequestBody @Valid TokenProfileScopedRequestV2Dto request);

    @Operation(summary = "Generate random data", description = "Generate random data on the token (always synchronous)")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Random data generated"))
    @PostMapping(path = "/random", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    RandomDataResponseV2Dto randomData(@RequestBody @Valid RandomDataRequestV2Dto request);
}
