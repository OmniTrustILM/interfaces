package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.connector.cryptography.v2.validation.ValidMetadataAttribute;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Common non-sensitive description of a key held by a cryptographic provider.
 *
 * <p>
 * Secret and private key material must remain inside the provider. Only {@link PublicKeyDataV2Dto} can carry a public
 * representation, and that representation is fixed to SPKI.
 * </p>
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SecretKeyDataV2Dto.class, name = "Secret"),
        @JsonSubTypes.Type(value = PublicKeyDataV2Dto.class, name = "Public"),
        @JsonSubTypes.Type(value = PrivateKeyDataV2Dto.class, name = "Private")})
@Schema(implementation = KeyDataV2Dto.OpenApiView.class)
public abstract sealed class KeyDataV2Dto permits SecretKeyDataV2Dto, PublicKeyDataV2Dto, PrivateKeyDataV2Dto {

    @Schema(description = "Type of the key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "key type is required")
    @Setter(AccessLevel.NONE)
    private KeyTypeV2 type;

    @Schema(description = "Cryptographic algorithm of the key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "key algorithm is required")
    private KeyAlgorithm algorithm;

    @Schema(description = "Bit length of the key", requiredMode = Schema.RequiredMode.REQUIRED, minimum = "1")
    @NotNull(message = "key length is required")
    @Min(value = 1, message = "key length must be positive")
    private Integer length;

    @Schema(description = "Non-sensitive, provider-specific descriptive metadata",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<@NotNull(
            message = "key metadata must not contain null items") @ValidMetadataAttribute MetadataAttribute> metadata;

    protected KeyDataV2Dto(KeyTypeV2 type) {
        this.type = type;
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
     * OpenAPI schema for type-specific key descriptors.
     */
    @Schema(name = "KeyDataV2",
            description = "Type-specific key descriptor. Secret and private keys never contain key material.",
            type = "object", discriminatorProperty = "type",
            discriminatorMapping = {
                    @DiscriminatorMapping(value = "Secret", schema = SecretKeyDataV2Dto.class),
                    @DiscriminatorMapping(value = "Public", schema = PublicKeyDataV2Dto.class),
                    @DiscriminatorMapping(value = "Private", schema = PrivateKeyDataV2Dto.class)},
            oneOf = {SecretKeyDataV2Dto.class, PublicKeyDataV2Dto.class, PrivateKeyDataV2Dto.class})
    interface OpenApiView {

        KeyTypeV2 getType();

        KeyAlgorithm getAlgorithm();

        Integer getLength();

        List<MetadataAttribute> getMetadata();
    }
}
