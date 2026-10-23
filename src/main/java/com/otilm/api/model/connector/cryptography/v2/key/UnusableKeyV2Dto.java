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
@Schema(name = "UnusableKeyV2Dto",
        description = "A key the token holds that the platform cannot onboard, with the reason",
        additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class UnusableKeyV2Dto {

    @Schema(description = "Label the token keeps for the key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String label;

    @Schema(description = "Reason the platform cannot onboard the key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "reason is required")
    private UnusableKeyReason reason;

    @Schema(description = "Descriptor of the key: a private key for a key pair listing, a secret key for a secret "
            + "key listing", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    private KeyDataV2Dto keyData;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported unusable key property: " + property);
    }
}
