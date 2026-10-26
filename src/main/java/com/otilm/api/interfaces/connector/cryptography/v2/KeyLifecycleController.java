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
        description = "Hooks into the platform's key lifecycle for keys under a signature activation module")
@ApiResponses(@ApiResponse(responseCode = "400",
        description = "Request body cannot be read (`BAD_REQUEST`), such as malformed JSON, an unknown property or "
                + "a value outside the published enum values",
        content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                schema = @Schema(implementation = ProblemDetailExtended.class))))
public interface KeyLifecycleController extends AuthProtectedConnectorController {

    @Operation(summary = "List signer attributes", description = "Returns the signer attribute definitions. "
            + "The platform renders the attributes like token attributes and validates the values against them. "
            + "The attribute values are sent on the certificate request hook. "
            + "Every attribute must be a v3 data attribute. The connector must pre-fill a value the token fixes. "
            + "Definitions must not contain resolved credentials or secret values.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schema retrieved"),
            @ApiResponse(responseCode = "422", description = "The body breaks a field rule (`VALIDATION_FAILED`)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/signer/attributes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listSignerAttributes(@RequestBody @Valid TokenProfileScopedRequestV2Dto request);

    @Operation(summary = "List certificate request attributes",
            description = "Returns the signature parameter schema of a certificate request for the key. "
                    + "The schema must include exactly one v3 string data attribute named signatureAlgorithm with UUID "
                    + "9180267f-c82f-4b7b-8160-d2363d813869, as a required, single-select, non-extensible list. "
                    + "The list must offer only the algorithms the connector proves possession with for this key. "
                    + "The platform offers these algorithms on its certificate request form and accepts no other. "
                    + "These algorithms may differ from the signing algorithms that are listed by "
                    + "`POST /v2/cryptographyProvider/operations/sign/attributes`.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schema retrieved"),
            @ApiResponse(responseCode = "422", description = "The body breaks a field rule (`VALIDATION_FAILED`)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/csr/attributes", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listCertificateRequestAttributes(@RequestBody @Valid KeyScopedRequestV2Dto request);

    @Operation(summary = "Create certificate request",
            description = "Create a PKCS#10 certificate request for the key. "
                    + "The platform sends the request info as the template. "
                    + "The connector resolves the signer from the signer attributes of an unbound key, "
                    + "or from the signer of a bound one. "
                    + "A rekeyed key arrives bound to the signer of the key it replaces. "
                    + "The connector binds the key where the technology binds at this point. "
                    + "The connector must refuse request info that carries another public key. "
                    + "The connector must prove possession with exactly the requested signature algorithm. "
                    + "The PKCS#10 must carry the requested subject and the key's public key. "
                    + "Requested extensions are advisory. The platform verifies the self-signature. "
                    + "The response carries the complete PKCS#10 and the canonical signer. "
                    + "The platform persists that signer as the key's binding. The hook is idempotent. "
                    + "A request that fails after the technology bound the key leaves the key bound.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Certificate request created"),
            @ApiResponse(responseCode = "409",
                    description = "The key is bound to another signer (`SIGNER_MISMATCH`). "
                            + "The problem names that signer. "
                            + "The key is already certified, and the technology proves possession only before "
                            + "certification (`KEY_CERTIFIED`).",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "The signer attributes do not identify a signer (`SIGNER_INVALID`). "
                            + "The problem names the attribute. "
                            + "The request info carries another public key (`PUBLIC_KEY_MISMATCH`). "
                            + "The signature algorithm is outside the certificate request listing "
                            + "(`PARAMETER_UNSUPPORTED`). "
                            + "The body breaks a field rule, such as request info that does not parse "
                            + "(`VALIDATION_FAILED`).",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/csr", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateRequestCreationResponseV2Dto createCertificateRequest(
            @RequestBody @Valid CreateCertificateRequestV2Dto request);

    @Operation(summary = "Record issued certificate",
            description = "Record a certificate issued for the key. "
                    + "The platform calls the hook for every certificate of the key, a renewal included. "
                    + "The first certificate certifies the key. "
                    + "The connector must check that the certificate's public key matches the key. "
                    + "It hands the certificate to a technology that stores certificates. "
                    + "It marks the key certified where the technology records that. "
                    + "The hook is idempotent on `keyMeta` and `certificateUuid`.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Certificate recorded"),
            @ApiResponse(responseCode = "409", description = "The key is not bound to a signer (`KEY_UNBOUND`)",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class))),
            @ApiResponse(responseCode = "422",
                    description = "The certificate's public key does not match the key (`PUBLIC_KEY_MISMATCH`). "
                            + "The body breaks a field rule (`VALIDATION_FAILED`).",
                    content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)))})
    @PostMapping(path = "/certificate", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    CertificateIssuedResponseV2Dto certificateIssued(@RequestBody @Valid CertificateIssuedRequestV2Dto request);
}
