package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Token-scoped body for {@code POST /v2/cryptographyProvider/operations/random}.
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RandomDataRequestV2Dto extends TokenProfileScopedRequestV2Dto {

    @Schema(description = "Number of random bytes to generate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Positive(message = "length must be greater than zero")
    private int length;

    @Schema(description = "Random generator attributes",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> attributes;
}
