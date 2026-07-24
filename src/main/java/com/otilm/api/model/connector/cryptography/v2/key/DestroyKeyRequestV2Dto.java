package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body for {@code POST /v2/cryptographyProvider/keys/destroy}. The key is identified by the inherited
 * {@code keyMeta}.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DestroyKeyRequestV2Dto extends KeyScopedRequestV2Dto {

    @Schema(description = "Caller-selected execution mode. The connector must not switch modes implicitly.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "executionMode is required")
    private OperationExecutionMode executionMode;
}
