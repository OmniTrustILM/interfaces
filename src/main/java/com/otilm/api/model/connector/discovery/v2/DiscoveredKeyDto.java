package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.connector.discovery.v2.validation.NoPrivateKeyMaterial;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Payload for a discovered {@code keys} item. Location (where the key was found) is not a field here — it belongs on
 * the enclosing {@link DiscoveredItemDto}'s {@code meta}.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoPrivateKeyMaterial
@Schema(description = "A connector reports a key's existence, intrinsic metadata, and at most its public "
        + "part; private, secret and split key material must never be sent. A "
        + NoPrivateKeyMaterial.TYPES_WITHOUT_A_PUBLIC_PART_NAMES
        + " report MUST omit publicKey and publicKeyFormat entirely.")
public class DiscoveredKeyDto implements DiscoveredItemPayloadDto {

    /**
     * Fixed to this subtype's own constant and deliberately not settable: it is the discriminator that selects this
     * shape, so there is nothing to set independently of the shape itself, and a setter would invite the two to
     * disagree (the same reasoning {@link DiscoveredItemDto} applies to its derived accessor). Jackson resolves an
     * {@code EXISTING_PROPERTY} discriminator from the wire value before it constructs anything, so no mutator is
     * needed to deserialize; a wire {@code resource} that contradicts this constant cannot overwrite it. No
     * {@code @NotNull} here either: with no setter and no other constructor the field can never be null, so the
     * constraint could never fail — {@code requiredMode} alone publishes it as required.
     */
    @Setter(AccessLevel.NONE)
    // No description here on purpose. Resource is a platform-wide schema component, and OpenAPI 3.0
    // cannot carry a description beside a $ref, so swagger-core hoists a referencing field's
    // description onto the referenced component — which would give every other API that references
    // Resource this discovery-specific wording. The discriminator is explained on
    // DiscoveredItemPayloadInterface instead.
    // S1170 wants this static because it is final with a constant initializer. It cannot be:
    // Lombok generates a static getter for a static field, which then no longer implements
    // DiscoveredItemPayloadInterface.getResource(), so the class does not compile. Verified, not assumed.
    @SuppressWarnings("java:S1170")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private final Resource resource = Resource.CRYPTOGRAPHIC_KEY;

    @Schema(description = "Key type: whether this is a public, private, secret, or split key",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "type is required")
    private KeyType type;

    @Schema(description = "Cryptographic algorithm of the key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "algorithm is required")
    private KeyAlgorithm algorithm;

    @Schema(description = "Key length in bits", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer length;

    @Schema(description = "Intrinsic key fingerprint; correlates the same key across runs, connectors, "
            + "and certificates without exposing key material", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fingerprint;

    @Schema(description = "Format of publicKey when present (typically SubjectPublicKeyInfo); "
            + "absent when publicKey is absent", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private KeyFormat publicKeyFormat;

    @Schema(description = "Base64-encoded public key material. Absent for "
            + NoPrivateKeyMaterial.TYPES_WITHOUT_A_PUBLIC_PART_NAMES
            + " discoveries, where only existence and intrinsic metadata are ever reported",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String publicKey;
}
