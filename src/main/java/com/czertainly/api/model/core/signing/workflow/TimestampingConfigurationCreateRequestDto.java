package com.czertainly.api.model.core.signing.workflow;

import com.czertainly.api.model.common.enums.cryptography.DigestAlgorithm;
import com.czertainly.api.model.core.signing.workflow.validation.QualifiedTimestampConstrained;
import com.czertainly.api.model.core.signing.workflow.validation.ValidOid;
import com.czertainly.api.model.core.signing.workflow.validation.ValidOidList;
import com.czertainly.api.model.core.signing.workflow.validation.ValidQualifiedTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TimestampingConfigurationCreateRequestDto", description = "Request DTO for creating Timestamping Signing Workflow Configuration")
@ToString(callSuper = true)
@ValidQualifiedTimestamp
public class TimestampingConfigurationCreateRequestDto extends SigningWorkflowConfigurationCreateRequestDto
        implements QualifiedTimestampConstrained {
    @ValidOid
    @Schema(description = "Default TSA Policy ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1.2.3.4.5")
    private String defaultPolicyId;

    @ValidOidList
    @Schema(description = "Allowed TSA Policy IDs", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "[\"1.2.3.4.5\", \"1.2.3.4.6\"]")
    private List<String> allowedPolicyIds = new ArrayList<>();

    @Schema(description = "Allowed message digest algorithms", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "[\"SHA-256\", \"SHA-384\", \"SHA-512\"]")
    private List<DigestAlgorithm> allowedDigestAlgorithms = new ArrayList<>();

    @NotNull
    @Schema(description = "ETSI qualified electronic timestamp", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean qualifiedTimestamp;

    @Schema(description = "Time quality configuration UUID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID timeQualityConfigurationUuid;

    public TimestampingConfigurationCreateRequestDto() {
        super(SigningWorkflowType.TIMESTAMPING);
    }
}
