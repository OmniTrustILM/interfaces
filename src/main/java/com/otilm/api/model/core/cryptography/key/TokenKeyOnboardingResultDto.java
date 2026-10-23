package com.otilm.api.model.core.cryptography.key;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(name = "TokenKeyOnboardingResultDto", description = "What became of one selected key")
public class TokenKeyOnboardingResultDto {

    @Schema(description = "Metadata of the key pair, as the request sent it",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MetadataAttribute> keyPairMeta;

    @Schema(description = "Whether the key was onboarded", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean onboarded;

    @Schema(description = "Whether the private key joined the inventory key that held the public key",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean intoExistingKey;

    @Schema(description = "UUID of the onboarded key, or of the inventory key that already holds the key. "
            + "For an existing key, present only when the caller may see that key in detail.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID keyUuid;

    @Schema(description = "Name of the onboarded key, or of the inventory key that already holds the key. "
            + "For an existing key, present only when the caller may see that key in detail.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keyName;

    @Schema(description = "Why the key was skipped", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;
}
