package com.otilm.api.model.core.cryptography.key;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
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
@Schema(name = "TokenKeyDto", description = "A key found in the key storage, and whether it can be onboarded")
public class TokenKeyDto {

    @Schema(description = "Metadata identifying the key pair in the key storage. Send it back unchanged to onboard the "
            + "key pair.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> keyPairMeta;

    @Schema(description = "Label the key storage keeps for the key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String label;

    @Schema(description = "Algorithm of the key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private KeyAlgorithm keyAlgorithm;

    @Schema(description = "Key length in bits", requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true,
            minimum = "1")
    private Integer length;

    @Schema(description = "SHA-256 fingerprint of the public key of a key pair",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fingerprint;

    @Schema(description = "Onboarding status of the key", requiredMode = Schema.RequiredMode.REQUIRED)
    private TokenKeyStatus status;

    @Schema(description = "UUID of the inventory key that already holds this key or its public key. "
            + "Present only when the caller may see that key in detail.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID inventoryKeyUuid;

    @Schema(description = "Name of the inventory key that already holds this key or its public key. "
            + "Present only when the caller may see that key in detail.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String inventoryKeyName;

    @Schema(description = "Default name of the onboarded key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String suggestedName;

    @Schema(description = "Why the key cannot be onboarded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;
}
