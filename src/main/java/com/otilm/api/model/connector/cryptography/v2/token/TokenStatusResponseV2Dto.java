package com.otilm.api.model.connector.cryptography.v2.token;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Response from {@code POST /v2/cryptographyProvider/tokens/status}.
 */
@Getter
@Setter
@ToString
@Schema(name = "TokenStatusResponseV2Dto")
public class TokenStatusResponseV2Dto {

    @Schema(description = "Token status",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private TokenStatusV2 status;

    @Schema(description = "Activation attempts remaining before the token locks out. Reported without authenticating "
            + "where the technology exposes it; null when unknown or not applicable.",
            examples = {"2"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer remainingActivationAttempts;
}
