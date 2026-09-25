package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.validation.UniqueIdentifiers;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body for {@code POST /v2/cryptographyProvider/operations/sign}. Asynchronous signing has no caller-supplied
 * idempotency identifier: if the accepted response is lost, its tracking handle cannot be recovered and retrying starts
 * a new batch.
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SignDataRequestV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class SignDataRequestV2Dto extends KeyScopedRequestV2Dto {

    @Schema(description = "Caller-selected execution mode. The connector must not switch modes implicitly.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "executionMode is required")
    private OperationExecutionMode executionMode;

    @Schema(description = "Signature attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "signatureAttributes is required")
    private List<@NotNull(
            message = "signatureAttributes must not contain null items") RequestAttribute> signatureAttributes;

    @Schema(description = "Data to be signed", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "data must contain at least one item")
    @UniqueIdentifiers
    private List<@NotNull(message = "data must not contain null items") @Valid SignatureDataV2Dto> data;
}
