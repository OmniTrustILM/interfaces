package com.otilm.api.model.core.cryptography.key;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(name = "TokenKeyOnboardingResultDto", description = "What became of one selected key pair")
public class TokenKeyOnboardingResultDto {

    @Schema(description = "Handle of the key pair, as the request sent it", requiredMode = Schema.RequiredMode.REQUIRED)
    private String keyPairHandle;

    @Schema(description = "Whether the key pair was onboarded", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean onboarded;

    @Schema(description = "Whether the private key joined the inventory key that held the public key",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean intoExistingKey;

    @Schema(description = "UUID of the onboarded key, or of the inventory key that already holds the key pair",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keyUuid;

    @Schema(description = "Name of the onboarded key, or of the inventory key that already holds the key pair",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keyName;

    @Schema(description = "Why the key pair was skipped", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;
}
