package com.otilm.api.model.core.cryptography.key;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.connector.cryptography.v2.key.KeyEntryV2Dto;
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

    @Schema(description = "What became of the key", requiredMode = Schema.RequiredMode.REQUIRED)
    private TokenKeyOnboardingOutcome outcome;

    @Schema(description = "Status that blocked the key; present only when `outcome` is `blocked` and a status "
            + "applies", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TokenKeyStatus status;

    @Schema(description = "UUID of the onboarded key, or of the inventory key that already holds the key. "
            + "For an existing key, present only when the caller may see that key in detail.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID keyUuid;

    @Schema(description = "Name of the onboarded key, or of the inventory key that already holds the key. "
            + "For an existing key, present only when the caller may see that key in detail.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keyName;

    @Schema(description = "Platform's summary of why the key was not onboarded",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;

    @Schema(description = "Text of the token instance's connector naming the cause behind a key that was not "
            + "onboarded and how to fix it, alongside the platform's `reason`. Clients display it and never parse it.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = KeyEntryV2Dto.MAX_DIAGNOSTIC_LENGTH)
    @ToString.Exclude
    private String diagnostic;
}
