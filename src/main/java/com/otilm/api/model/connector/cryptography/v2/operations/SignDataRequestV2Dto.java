package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureRequestDataV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Body for {@code POST /v2/cryptographyProvider/operations/sign}.
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignDataRequestV2Dto extends KeyScopedRequestV2Dto {

    @Schema(description = "Caller-selected execution mode. The connector must not switch modes implicitly.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "executionMode is required")
    private OperationExecutionMode executionMode;

    @Schema(description = "Signature attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "signatureAttributes is required (may be empty list, but must be present)")
    private List<RequestAttribute> signatureAttributes;

    @Schema(description = "Data to be signed",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "data must contain at least one item")
    private List<@NotNull(message = "data must not contain null items") @Valid SignatureRequestDataV2Dto> data;
}
