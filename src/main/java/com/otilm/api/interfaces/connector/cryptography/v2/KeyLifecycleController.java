package com.otilm.api.interfaces.connector.cryptography.v2;

import com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateIssuedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateIssuedResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateRequestCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CreateCertificateRequestV2Dto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Advertised as {@link com.otilm.api.model.client.connector.v2.ConnectorInterface#KEY_LIFECYCLE}. The technology's own
 * ceremony, such as binding and proof of possession, stays behind the hooks.
 */
@RequestMapping("/v2/cryptographyProvider/keyLifecycle")
@Tag(name = "Key Lifecycle v2",
        description = "Key lifecycle hooks for binding and certifying keys under a signature activation module")
@ApiResponses(@ApiResponse(responseCode = "400",
        description = "Request body cannot be read (errorCode `BAD_REQUEST`), including malformed JSON, "
                + "unknown properties, and values outside the published enum values",
        content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetailExtended.class))))
public interface KeyLifecycleController extends AuthProtectedConnectorController {

    @Operation(summary = "List signer attributes",
            description = "Returns the attributes that identify a signer on the token. "
                    + "The platform renders them like token attributes, validates the values against them and "
                    + "sends the values on the certificate request hook, or on create key to a connector "
                    + "declaring `signerBoundAtCreation`. The listing must hold at least one attribute, each a "
                    + "v3 data attribute with any value the token fixes pre-filled, and must not contain "
                    + "resolved credentials or secret values.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schema retrieved"),
            @ApiResponse(responseCode = "422",
                    description = "Request body was read successfully but violates a field validation rule "
                            + "(errorCode `VALIDATION_FAILED`)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/signer/attributes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listSignerAttributes(@RequestBody @Valid TokenProfileScopedRequestV2Dto request);

    @Operation(summary = "List certificate request attributes",
            description = "Returns the signature parameter schema for a certificate request of the key. "
                    + "The schema must include exactly one v3 string data attribute named signatureAlgorithm with UUID "
                    + "9180267f-c82f-4b7b-8160-d2363d813869, as a required, single-select, non-extensible list "
                    + "offering the algorithms the connector proves possession with for this key. "
                    + "The platform offers and accepts only these on its certificate request form; they may "
                    + "differ from the signing algorithms listed by "
                    + "`POST /v2/cryptographyProvider/operations/sign/attributes`.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schema retrieved"),
            @ApiResponse(responseCode = "422",
                    description = "Request body was read successfully but violates a field validation rule "
                            + "(errorCode `VALIDATION_FAILED`)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/csr/attributes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listCertificateRequestAttributes(@RequestBody @Valid KeyScopedRequestV2Dto request);

    @Operation(summary = "Create certificate request",
            description = "Create a PKCS#10 certificate request for the key from the request info, which the "
                    + "platform sends as the template. The connector resolves the signer from the signer "
                    + "attributes of an unbound key or from the signer of a bound one; a rekeyed key arrives "
                    + "bound to the signer of the key it replaces, and every key of a connector declaring "
                    + "`signerBoundAtCreation` arrives bound. The connector binds the key here when its "
                    + "technology binds at this point. It must refuse request info carrying another public key "
                    + "and must prove possession with exactly the requested signature algorithm. The PKCS#10 "
                    + "must carry the requested subject and the request info's public key in the same "
                    + "encoding; requested extensions are advisory, and the platform verifies the "
                    + "self-signature. The response carries the complete PKCS#10 and the canonical signer, "
                    + "which the platform persists as the key's binding. The hook is idempotent, and a request "
                    + "that fails after the technology bound the key leaves the key bound, so the same "
                    + "signer's retry completes it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Certificate request created"),
            @ApiResponse(responseCode = "409",
                    description = "The key is bound to another signer, which the problem names "
                            + "(errorCode `SIGNER_MISMATCH`), or the key is already certified and its "
                            + "technology proves possession only before certification " + "(errorCode `KEY_CERTIFIED`)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "The signer attributes do not identify a signer, and the problem names "
                            + "the attribute (errorCode `SIGNER_INVALID`); the request info carries another "
                            + "public key (errorCode `PUBLIC_KEY_MISMATCH`); the signature algorithm is outside "
                            + "the certificate request listing (errorCode `PARAMETER_UNSUPPORTED`); or the "
                            + "request body violates a field validation rule, such as request info that does "
                            + "not parse (errorCode `VALIDATION_FAILED`)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/csr", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateRequestCreationResponseV2Dto createCertificateRequest(
            @RequestBody @Valid CreateCertificateRequestV2Dto request);

    @Operation(summary = "Record issued certificate",
            description = "Record a certificate issued for the key. "
                    + "The platform calls the hook for every certificate of the key, renewals included, and "
                    + "the first one certifies the key. The connector must check that the certificate's "
                    + "public key matches the key before it hands the certificate to a technology that stores "
                    + "certificates, and it marks the key certified where the technology records that. "
                    + "The hook is idempotent on `keyMeta` and `certificateUuid`.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Certificate recorded"),
            @ApiResponse(responseCode = "409",
                    description = "The key is not bound to a signer (errorCode `KEY_UNBOUND`)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "The certificate's public key does not match the key "
                            + "(errorCode `PUBLIC_KEY_MISMATCH`), or the request body violates a field "
                            + "validation rule (errorCode `VALIDATION_FAILED`)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/certificate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateIssuedResponseV2Dto certificateIssued(@RequestBody @Valid CertificateIssuedRequestV2Dto request);
}
