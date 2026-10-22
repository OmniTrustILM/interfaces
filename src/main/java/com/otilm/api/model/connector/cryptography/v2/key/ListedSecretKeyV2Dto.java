package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.validation.ValidMetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "ListedSecretKeyV2Dto", description = """
        A secret key the token holds, in the shape of a secret-key creation response. Any later request accepts its
        metadata exactly like metadata returned at creation.
        """, additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class ListedSecretKeyV2Dto extends ListedKeyV2Dto {

    @Override
    @Schema(description = "Type of the listed key", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = KeyRequestType.Codes.SECRET)
    public KeyRequestType getKeyRequestType() {
        return KeyRequestType.SECRET;
    }

    @Schema(description = "Connector-defined metadata of the key. It must identify the key durably, and for a key "
            + "the platform created it equals the metadata creation returned: a secret key has no public key, so the "
            + "platform recognises one it already holds by this metadata alone. `keyMetaFilter` matches against it. "
            + "Each metadata name appears once, and every content item carries data. A key that cannot meet this "
            + "is reported in `unaddressableKeys` or left out.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "keyMeta is required and must not be empty")
    private List<@NotNull(
            message = "keyMeta must not contain null items") @ValidMetadataAttribute MetadataAttribute> keyMeta;

    @Schema(description = "Descriptor of the secret key", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    @NotNull(message = "keyData is required")
    private SecretKeyDataV2Dto keyData;

    @Override
    public List<MetadataAttribute> handle() {
        return keyMeta;
    }

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported listed secret key property: " + property);
    }
}
