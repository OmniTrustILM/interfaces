package com.otilm.api.model.core.cryptography.key;

import com.otilm.api.model.client.cryptography.key.KeyCompromiseReason;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import com.otilm.api.model.core.logging.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Public key-item detail, preserving an absent provider length as null.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class KeyItemDetailDto extends NameAndUuidDto {

    @Schema(description = "UUID of the key item in the Connector", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keyReferenceUuid;

    @Schema(description = "Type of the Key", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyType type;

    @Schema(description = "Key Algorithm", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyAlgorithm keyAlgorithm;

    @Schema(description = "Key Format", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyFormat format;

    @ToString.Exclude
    @Schema(description = "Key Data", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Sensitive
    private String keyData;

    @Schema(description = "Key length in bits. Required for RSA, ECDSA and AES; optional otherwise.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true, minimum = "1")
    private Integer length;

    @Schema(description = "Metadata for the key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataResponseDto> metadata;

    @Schema(description = "Key Usages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<KeyUsage> usage;

    @Schema(description = "Boolean describing if the key is enabled or not",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;

    @Schema(description = "Key State", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyState state;

    @Schema(description = "Reason for Compromise", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private KeyCompromiseReason reason;

    @Schema(description = "Key compliance status", requiredMode = Schema.RequiredMode.REQUIRED)
    private ComplianceStatus complianceStatus;

    @Schema(description = "Whether this key may be exported. Set when the key is created or imported and never "
            + "raised afterwards; it can only be lowered to false.", requiredMode = Schema.RequiredMode.REQUIRED,
            defaultValue = "false")
    private boolean exportable;

    /**
     * Retains the constructor accepting a primitive length for existing callers.
     *
     * @param keyReferenceUuid connector key identity
     * @param type key kind
     * @param keyAlgorithm key algorithm
     * @param format material encoding
     * @param keyData public material
     * @param length key length in bits
     * @param metadata descriptive metadata
     * @param usage permitted operations
     * @param enabled whether operations are enabled
     * @param state lifecycle state
     * @param reason compromise reason
     * @param complianceStatus compliance outcome
     * @param exportable whether private material may be exported
     */
    public KeyItemDetailDto(String keyReferenceUuid, KeyType type, KeyAlgorithm keyAlgorithm, KeyFormat format,
            String keyData, int length, List<MetadataResponseDto> metadata, List<KeyUsage> usage, boolean enabled,
            KeyState state, KeyCompromiseReason reason, ComplianceStatus complianceStatus, boolean exportable) {
        this(keyReferenceUuid, type, keyAlgorithm, format, keyData, Integer.valueOf(length), metadata, usage, enabled,
                state, reason, complianceStatus, exportable);
    }

    /**
     * The signature this class carried before {@code exportable} was added, so a caller that constructs it positionally
     * still compiles. A key item is not exportable unless it is said to be.
     */
    public KeyItemDetailDto(String keyReferenceUuid, KeyType type, KeyAlgorithm keyAlgorithm, KeyFormat format,
            String keyData, int length, List<MetadataResponseDto> metadata, List<KeyUsage> usage, boolean enabled,
            KeyState state, KeyCompromiseReason reason, ComplianceStatus complianceStatus) {
        this(keyReferenceUuid, type, keyAlgorithm, format, keyData, length, metadata, usage, enabled, state, reason,
                complianceStatus, false);
    }
}
