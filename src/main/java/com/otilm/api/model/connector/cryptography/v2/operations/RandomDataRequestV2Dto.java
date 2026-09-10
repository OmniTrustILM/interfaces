package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Token-scoped body for {@code POST /v2/cryptographyProvider/operations/random}.
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "RandomDataRequestV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class RandomDataRequestV2Dto extends TokenProfileScopedRequestV2Dto {

    public static final int MAX_LENGTH = 1_048_576;

    @Schema(description = "Number of random bytes to generate, capped at 1 MiB", minimum = "1",
            maximum = "" + MAX_LENGTH, requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "length must be greater than zero")
    @Max(value = MAX_LENGTH, message = "length must not exceed 1 MiB")
    private int length;

    @Schema(description = "Random generator attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "operationAttributes is required (may be empty list, but must be present)")
    private List<@NotNull(
            message = "operationAttributes must not contain null entries") RequestAttribute> operationAttributes;
}
