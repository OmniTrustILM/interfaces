package com.otilm.api.model.core.cryptography.key;

import com.otilm.api.model.client.cryptography.key.KeyCompromiseReason;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import com.otilm.api.model.core.compliance.v2.ComplianceCheckResultDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class KeyItemDetailDto extends NameAndUuidDto {

    @Schema(
            description = "UUID of the key item in the Connector",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String keyReferenceUuid;

    @Schema(
            description = "Type of the Key",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private KeyType type;

    @Schema(
            description = "Key Algorithm",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private KeyAlgorithm keyAlgorithm;

    @Schema(
            description = "Key Format",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private KeyFormat format;

    @Schema(
            description = "Key Data",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String keyData;

    @Schema(
            description = "Key Length",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private int length;

    @Schema(
            description = "Metadata for the key",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<MetadataResponseDto> metadata;

    @Schema(
            description = "Key Usages",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<KeyUsage> usage;

    @Schema(
            description = "Boolean describing if the key is enabled or not",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean enabled;

    @Schema(
            description = "Key State",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private KeyState state;

    @Schema(
            description = "Reason for Compromise",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private KeyCompromiseReason reason;

    @Schema(
            description = "Key compliance status",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ComplianceStatus complianceStatus;

}
