package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Completed public-key response envelope.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "PublicKeyDataResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class PublicKeyDataResponseV2Dto {

    @Schema(description = "Connector-defined opaque public-key handle",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "public key metadata is required and must not be empty")
    private List<@NotNull @Valid MetadataAttributeV2> keyMeta;

    @Valid
    @NotNull(message = "public key data is required")
    @Schema(description = "Public-key descriptor, optionally carrying the public key as SPKI",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private PublicKeyDataV2Dto keyData;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported public-key response property: " + property);
    }
}
