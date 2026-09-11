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

    @Schema(description = "Connector-defined opaque private-key handle. Metadata must identify the key durably—it must "
            + "remain valid across connector restarts and sessions; ephemeral handles must not be used.",
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
