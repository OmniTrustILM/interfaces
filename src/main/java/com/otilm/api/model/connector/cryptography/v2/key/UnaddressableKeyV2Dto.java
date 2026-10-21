package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "UnaddressableKeyV2Dto", description = """
        A key the token holds without an identifier unique to it. Onboarding needs such an identifier, so the platform
        only lists the key.
        """, additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class UnaddressableKeyV2Dto {

    @Schema(description = "Label the token keeps for the key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String label;

    @Schema(description = "Descriptor of the key: a private key for a key pair listing, a secret key for a secret "
            + "key listing", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    @NotNull(message = "keyData is required")
    private KeyDataV2Dto keyData;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported unaddressable key property: " + property);
    }
}
