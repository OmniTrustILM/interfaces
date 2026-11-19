package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.connector.cryptography.v2.operations.data.IdentifiedDataV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** The connector's report for one requested handle, answered under the handle's identifier. */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "KeyResolutionV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class KeyResolutionV2Dto extends KeyEntryV2Dto implements IdentifiedDataV2Dto {

    @Schema(description = "Identifier of the handle this result answers", examples = {"0"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "identifier is required")
    private String identifier;
}
