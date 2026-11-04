package com.otilm.api.model.connector.cryptography.v2.operations.data;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * One item of a V2 signing or verification request.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SignatureDataV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class SignatureDataV2Dto implements IdentifiedDataV2Dto {

    @Schema(description = "Data to be signed or verified, or the signature to verify, depending on the request "
            + "field.", format = "byte", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "data is required and must not be empty")
    @ToString.Exclude
    private byte[] data;

    @Schema(description = "Identifier used to correlate the item across request and response", examples = {"customId"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "identifier is required and must be unique within the batch")
    private String identifier;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported v2 signature data property: " + property);
    }
}
