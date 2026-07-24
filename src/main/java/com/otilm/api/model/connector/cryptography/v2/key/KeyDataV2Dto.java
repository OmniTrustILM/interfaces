package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.*;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Common non-sensitive description of a key held by a cryptographic provider.
 *
 * <p>Secret and private key material must remain inside the provider. Only
 * {@link PublicKeyDataV2Dto} can carry a public representation, and that representation is fixed to SPKI.</p>
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SecretKeyDataV2Dto.class, name = "Secret"),
        @JsonSubTypes.Type(value = PublicKeyDataV2Dto.class, name = "Public"),
        @JsonSubTypes.Type(value = PrivateKeyDataV2Dto.class, name = "Private")
})
@Schema(implementation = KeyDataV2Dto.OpenApiView.class)
public abstract sealed class KeyDataV2Dto
        permits SecretKeyDataV2Dto, PublicKeyDataV2Dto, PrivateKeyDataV2Dto {

    @Schema(description = "Role of the key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "key type is required")
    private KeyRoleV2 type;

    @Schema(description = "Cryptographic algorithm of the key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "key algorithm is required")
    private KeyAlgorithm algorithm;

    @Schema(description = "Bit length of the key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "key length is required")
    @Positive(message = "key length must be positive")
    private Integer length;

    @Schema(description = "Non-sensitive, provider-specific descriptive metadata",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttributeV2> metadata;

    protected KeyDataV2Dto(KeyRoleV2 type) {
        this.type = type;
    }

    /**
     * Sets the key role and requires it to match the concrete key-data subtype.
     */
    @JsonSetter("type")
    public final void setType(KeyRoleV2 type) {
        if (type == null || type != expectedType()) {
            throw new IllegalArgumentException("Key type does not match the key-data subtype");
        }
        this.type = type;
    }

    protected abstract KeyRoleV2 expectedType();

    /**
     * Validate a completed connector response after deserialization.
     */
    public final void validate() {
        if (type != expectedType()) {
            throw new IllegalArgumentException("Key type does not match the key-data subtype");
        }
        if (algorithm == null) {
            throw new IllegalArgumentException("Key algorithm is required");
        }
        if (length == null || length <= 0) {
            throw new IllegalArgumentException("Key length must be positive");
        }
        if (metadata != null && metadata.stream().anyMatch(entry -> entry == null
                || entry.getName() == null || entry.getName().isBlank()
                || entry.getContentType() == null
                || entry.getProperties() == null
                || entry.getContent() == null || entry.getContent().isEmpty()
                || entry.getContent().stream().anyMatch(java.util.Objects::isNull))) {
            throw new IllegalArgumentException("Key metadata entries must have a name, type, properties and content");
        }
        validateRoleSpecificData();
    }

    protected void validateRoleSpecificData() {
        // Non-public key roles deliberately have no role-specific data.
    }

    /**
     * Reject unknown key-data properties during deserialization.
     */
    @JsonAnySetter
    @Schema(hidden = true)
    public final void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported v2 key-data property: " + property);
    }

    /**
     * OpenAPI schema for role-specific key descriptors.
     */
    @Schema(
            name = "KeyDataV2",
            description = "Role-specific key descriptor. Secret and private keys never contain key material.",
            type = "object",
            discriminatorProperty = "type",
            discriminatorMapping = {
                    @DiscriminatorMapping(value = "Secret", schema = SecretKeyDataV2Dto.class),
                    @DiscriminatorMapping(value = "Public", schema = PublicKeyDataV2Dto.class),
                    @DiscriminatorMapping(value = "Private", schema = PrivateKeyDataV2Dto.class)
            },
            oneOf = {
                    SecretKeyDataV2Dto.class,
                    PublicKeyDataV2Dto.class,
                    PrivateKeyDataV2Dto.class
            })
    interface OpenApiView {

        KeyRoleV2 getType();

        KeyAlgorithm getAlgorithm();

        Integer getLength();

        List<MetadataAttributeV2> getMetadata();
    }
}
