package com.otilm.api.model.connector.cryptography.v2.operations.data;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.OperationEventV2Dto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A signature from a synchronous V2 sign response, with the activation steps that created it. The platform keeps one
 * signing record per signed item.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "SignatureResponseItemV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class SignatureResponseItemV2Dto implements IdentifiedDataV2Dto {

    @Schema(description = "Signature over the data of the corresponding request item", format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "data is required and must not be empty")
    @ToString.Exclude
    private byte[] data;

    @Schema(description = "Identifier of the corresponding item in the sign request", examples = {"customId"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "identifier is required")
    private String identifier;

    @ArraySchema(arraySchema = @Schema(description = "Steps the connector took to create this signature, in order; "
            + "absent when it took none. Steps shared by the whole batch, such as its authorization, must appear on "
            + "every signature."))
    private List<@NotNull(message = "events must not contain null items") @Valid OperationEventV2Dto> events;

    public SignatureResponseItemV2Dto(byte[] data, String identifier) {
        this(data, identifier, null);
    }

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported v2 signature response item property: " + property);
    }
}
