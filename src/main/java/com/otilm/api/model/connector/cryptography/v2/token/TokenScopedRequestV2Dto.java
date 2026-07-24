package com.otilm.api.model.connector.cryptography.v2.token;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Token context used by token and token-profile operations.
 */
@Getter
@Setter
@ToString
@Schema(description = "Token context for token and token-profile operations.")
public class TokenScopedRequestV2Dto {

    @Schema(description = "Token attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "tokenAttributes is required (may be empty list, but must be present)")
    private List<RequestAttribute> tokenAttributes;
}
