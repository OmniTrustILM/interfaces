package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
@Schema(name = "ResolveKeysResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class ResolveKeysResponseV2Dto {

    @Schema(description = "Results, one per requested handle", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "results is required")
    private List<@NotNull(message = "results must not contain null items") @Valid KeyResolutionV2Dto> results;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported key resolution response property: " + property);
    }
}
