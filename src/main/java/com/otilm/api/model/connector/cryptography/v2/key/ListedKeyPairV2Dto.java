package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.validation.ValidMetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "ListedKeyPairV2Dto", description = """
        A key pair the token holds, in the shape of a key-pair creation response. Any later request accepts its
        metadata exactly like metadata returned at creation.
        """, additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class ListedKeyPairV2Dto extends ListedKeyV2Dto {

    @Override
    @Schema(description = "Type of the listed key", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = KeyRequestType.Codes.KEY_PAIR)
    public KeyRequestType getKeyRequestType() {
        return KeyRequestType.KEY_PAIR;
    }

    @Schema(description = "Connector-defined metadata for the pair as a whole. It must identify the pair durably. "
            + "The key resolution takes it back as the key's handle. Each metadata name appears once, and "
            + "every content item carries data. A key that cannot meet this is reported in `unusableKeys`.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "keyPairMeta is required and must not be empty")
    private List<@NotNull(
            message = "keyPairMeta must not contain null items") @ValidMetadataAttribute MetadataAttribute> keyPairMeta;

    @Schema(description = "Data of the private key", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    @NotNull(message = "privateKeyData is required")
    private PrivateKeyDataResponseV2Dto privateKeyData;

    @Schema(description = "Data of the public key, read from the public key object on the token. Absent when the "
            + "token lacks a public key object for the pair.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    private PublicKeyDataResponseV2Dto publicKeyData;

    @Override
    public List<MetadataAttribute> handle() {
        return keyPairMeta;
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "public and private key algorithms must match")
    public boolean isKeyAlgorithmsMatching() {
        return isMissingAnyKeyDescriptor() || Objects
                .equals(publicKeyData.getKeyData().getAlgorithm(), privateKeyData.getKeyData().getAlgorithm());
    }

    private boolean isMissingAnyKeyDescriptor() {
        return publicKeyData == null || privateKeyData == null || publicKeyData.getKeyData() == null
                || privateKeyData.getKeyData() == null;
    }

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported listed key pair property: " + property);
    }
}
