package com.otilm.api.model.core.cryptography.key;

import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Schema(name = "TokenKeyDto", description = "A key pair found in the key storage, and whether it can be onboarded")
public class TokenKeyDto {

    @Schema(description = "Opaque handle of the key pair in the key storage. Send it back unchanged to onboard the key "
            + "pair. Absent for a key without a unique ID.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keyPairHandle;

    @Schema(description = "Label the key storage keeps for the key pair",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String label;

    @Schema(description = "Algorithm of the key pair", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyAlgorithm keyAlgorithm;

    @Schema(description = "Key length in bits. Required for RSA, ECDSA and AES; optional otherwise.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true, minimum = "1")
    private Integer length;

    @Schema(description = "SHA-256 fingerprint of the public key. Absent when the key storage lacks a public key object.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fingerprint;

    @Schema(description = "Onboarding status of the key pair", requiredMode = Schema.RequiredMode.REQUIRED)
    private TokenKeyStatus status;

    @Schema(description = "UUID of the inventory key that already holds this key pair or its public key",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String inventoryKeyUuid;

    @Schema(description = "Name of the inventory key that already holds this key pair or its public key",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String inventoryKeyName;

    @Schema(description = "Default name of the onboarded key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String suggestedName;

    @Schema(description = "Why the key pair cannot be onboarded", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reason;
}
