package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.connector.cryptography.v2.operations.data.IdentifiedDataV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "KeyResolutionV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class KeyResolutionV2Dto implements IdentifiedDataV2Dto {

    @Schema(description = "Identifier of the handle this result answers", examples = {"0"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "identifier is required")
    private String identifier;

    @Schema(description = "Outcome of the resolution", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "outcome is required")
    private KeyResolutionOutcome outcome;

    @Schema(description = "Key the token holds under the handle, in the listing's shape. Present only when "
            + "`outcome` is `found`.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    private ListedKeyV2Dto key;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported key resolution property: " + property);
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "key must be present exactly when outcome is found")
    public boolean isKeyConsistentWithOutcome() {
        if (outcome == null) {
            return true;
        }
        return (outcome == KeyResolutionOutcome.FOUND) == (key != null);
    }
}
