package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.validation.ValidMetadataAttribute;
import com.otilm.api.model.connector.cryptography.v2.operations.data.IdentifiedDataV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Schema(name = "KeyHandleV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class KeyHandleV2Dto implements IdentifiedDataV2Dto {

    @Schema(description = "Identifier of the handle. Its result echoes it.", examples = {"0"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "identifier is required")
    private String identifier;

    @Schema(description = "Handle exactly as a listing returned it: `keyPairMeta` of a key pair, `keyMeta` of a "
            + "secret key.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "keyMeta must not be empty")
    private List<@NotNull(
            message = "keyMeta must not contain null items") @ValidMetadataAttribute MetadataAttribute> keyMeta;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported key handle property: " + property);
    }
}
