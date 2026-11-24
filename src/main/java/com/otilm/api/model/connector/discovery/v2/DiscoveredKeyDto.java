package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.connector.discovery.v2.validation.AlgorithmIdentifierFitsKey;
import com.otilm.api.model.connector.discovery.v2.validation.NoPrivateKeyMaterial;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Payload for a discovered {@code keys} item. Where the key was found belongs in the item's {@code meta}.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoPrivateKeyMaterial
@AlgorithmIdentifierFitsKey
@Schema(description = "A discovered key: its existence, intrinsic metadata and at most its public part. Private, "
        + "secret and split key material MUST NOT be sent, and a "
        + NoPrivateKeyMaterial.TYPES_WITHOUT_A_PUBLIC_PART_NAMES + " key MUST omit publicKey and publicKeyFormat.")
public class DiscoveredKeyDto implements DiscoveredItemPayloadDto {

    /**
     * The discriminator, fixed to this subtype. No setter, so a wire value cannot contradict the shape, and no
     * {@code @NotNull}, since the field cannot be null; {@code requiredMode} publishes it as required.
     */
    @Setter(AccessLevel.NONE)
    // No description: swagger-core would hoist it onto the shared Resource component. Not static despite S1170: Lombok
    // would generate a static getter, which cannot implement DiscoveredItemPayloadInterface.getResource().
    @SuppressWarnings("java:S1170")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private final Resource resource = Resource.CRYPTOGRAPHIC_KEY;

    @Schema(description = "Key type: public, private, secret or split", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "type is required")
    private KeyType type;

    @Schema(description = "Cryptographic algorithm of the key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "algorithm is required")
    private KeyAlgorithm algorithm;

    @Schema(description = "Key length in bits", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer length;

    @Schema(description = "Intrinsic fingerprint identifying the same key across runs, connectors and certificates, "
            + "without exposing key material", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fingerprint;

    @Schema(description = "Format of `publicKey`, one of `Raw`, `SubjectPublicKeyInfo` or `Custom`; present only with "
            + "`publicKey`, and taken as SubjectPublicKeyInfo when absent.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private KeyFormat publicKeyFormat;

    @Schema(description = "Base64-encoded public key. Absent for a "
            + NoPrivateKeyMaterial.TYPES_WITHOUT_A_PUBLIC_PART_NAMES + " key.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String publicKey;

    @Schema(description = "Base64-encoded DER AlgorithmIdentifier naming the key's parameters, as "
            + "SubjectPublicKeyInfo and PKCS#8 carry it. The provider must send it, when it can read the parameters, "
            + "for a private or split key and for a public key without a SubjectPublicKeyInfo `publicKey`, and must "
            + "omit it for a secret key and beside a SubjectPublicKeyInfo `publicKey`.", type = "string",
            format = "byte", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String algorithmIdentifier;

    /** An absent {@code publicKeyFormat} implies SubjectPublicKeyInfo. */
    public boolean hasPublicKeyInSpkiFormat() {
        return publicKey != null && (publicKeyFormat == null || publicKeyFormat == KeyFormat.SPKI);
    }
}
