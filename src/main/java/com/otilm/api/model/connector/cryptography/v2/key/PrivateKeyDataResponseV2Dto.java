package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
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

/**
 * Completed private-key response envelope.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "PrivateKeyDataResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class PrivateKeyDataResponseV2Dto {

    @Schema(description = "Connector-defined opaque handle of the private key, which must identify the key uniquely "
            + "on the token and durably across connector restarts, sessions and token profiles. Each metadata name "
            + "appears once, and every content item carries data. A private key listed without its public key has "
            + "no fingerprint, so the platform recognises a duplicate by this metadata alone. Every "
            + "response that returns the key, from creation or import to listing and resolution, must carry the "
            + "same metadata names and content data, with each name's content items in the same order.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "private key metadata is required and must not be empty")
    private List<@NotNull @ValidMetadataAttribute MetadataAttribute> keyMeta;

    @Valid
    @NotNull(message = "private key data is required")
    @Schema(description = "Private-key descriptor without key material", requiredMode = Schema.RequiredMode.REQUIRED)
    private PrivateKeyDataV2Dto keyData;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported private-key response property: " + property);
    }
}
