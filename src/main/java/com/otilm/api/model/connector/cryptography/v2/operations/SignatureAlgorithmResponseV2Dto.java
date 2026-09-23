package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Response envelope for {@code POST /v2/cryptographyProvider/operations/sign/algorithm}.
 */
@Getter
@Setter
@ToString
@Schema(name = "SignatureAlgorithmResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class SignatureAlgorithmResponseV2Dto {

    @Schema(description = "The signature algorithm for the supplied key and signature attributes.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "signatureAlgorithm is required")
    private SignatureAlgorithm signatureAlgorithm;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported v2 signature algorithm response property: " + property);
    }
}
