package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.connector.common.v2.validation.AsynchronousResponse;
import com.otilm.api.model.connector.common.v2.validation.SynchronousResponse;
import com.otilm.api.model.connector.common.v2.validation.ValidMetadataAttribute;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Key-pair response envelope for {@code POST /v2/cryptographyProvider/keys}.
 *
 * <p>
 * On a sync 200 both key entries are populated, each carrying its own {@code keyMeta} handle, and {@code keyPairMeta}
 * describes their association. On an async 202 both key entries are null and {@code operationMeta} is the tracking
 * handle.
 * </p>
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "KeyPairDataResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class KeyPairDataResponseV2Dto extends KeyCreationResponseV2Dto {

    @Override
    @Schema(description = "Type of key requested and returned", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = KeyRequestType.Codes.KEY_PAIR)
    public KeyRequestType getKeyRequestType() {
        return KeyRequestType.KEY_PAIR;
    }

    @Schema(description = "Data of the public key. Populated on sync 200; absent on async 202.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    @Null(message = "publicKeyData must be absent for asynchronous execution", groups = AsynchronousResponse.class)
    @NotNull(message = "publicKeyData is required for synchronous execution", groups = SynchronousResponse.class)
    private PublicKeyDataResponseV2Dto publicKeyData;

    @Schema(description = "Data of the private key. Populated on sync 200; absent on async 202.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    @Null(message = "privateKeyData must be absent for asynchronous execution", groups = AsynchronousResponse.class)
    @NotNull(message = "privateKeyData is required for synchronous execution", groups = SynchronousResponse.class)
    private PrivateKeyDataResponseV2Dto privateKeyData;

    @ArraySchema(arraySchema = @Schema(description = "Connector-defined metadata for the pair as a whole. Required on "
            + "a synchronous 200 and in a completed asynchronous creation-status result; absent from the initial "
            + "asynchronous 202.", requiredMode = Schema.RequiredMode.NOT_REQUIRED), minItems = 1)
    @Null(message = "keyPairMeta must be absent for asynchronous execution", groups = AsynchronousResponse.class)
    @NotEmpty(message = "keyPairMeta must contain at least one item for synchronous execution",
            groups = SynchronousResponse.class)
    private List<@NotNull(
            message = "keyPairMeta must not contain null items") @ValidMetadataAttribute MetadataAttribute> keyPairMeta;

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "public and private key algorithms must match")
    public boolean isKeyAlgorithmsMatching() {
        return isMissingAnyKeyDescriptor() || Objects
                .equals(publicKeyData.getKeyData().getAlgorithm(), privateKeyData.getKeyData().getAlgorithm());
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "public and private key lengths must match")
    public boolean isKeyLengthsMatching() {
        return isMissingAnyKeyDescriptor()
                || Objects.equals(publicKeyData.getKeyData().getLength(), privateKeyData.getKeyData().getLength());
    }

    private boolean isMissingAnyKeyDescriptor() {
        return publicKeyData == null || privateKeyData == null || publicKeyData.getKeyData() == null
                || privateKeyData.getKeyData() == null;
    }

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported key-pair response property: " + property);
    }
}
