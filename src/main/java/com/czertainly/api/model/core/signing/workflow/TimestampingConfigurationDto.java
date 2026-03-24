package com.czertainly.api.model.core.signing.workflow;

import com.czertainly.api.model.common.enums.cryptography.DigestAlgorithm;
import com.czertainly.api.model.core.signing.timequality.TimeQualityConfigurationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "TimestampingConfigurationDto", description = "Timestamping workflow configuration details")
@ToString(callSuper = true)
public class TimestampingConfigurationDto extends SigningWorkflowConfigurationDto {

    @Schema(description = "Default TSA Policy ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1.2.3.4.5")
    private String defaultPolicyId;

    @Schema(description = "Allowed TSA Policy IDs", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "[\"1.2.3.4.5\", \"1.2.3.4.6\"]")
    private List<String> allowedPolicyIds = new ArrayList<>();

    @Schema(description = "Allowed message digest algorithms", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "[\"SHA-256\", \"SHA-384\", \"SHA-512\"]")
    private List<DigestAlgorithm> allowedDigestAlgorithms = new ArrayList<>();

    @Schema(description = "ETSI qualified electronic timestamp", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean qualifiedTimestamp;

    @Schema(description = "Time quality configuration reference", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TimeQualityConfigurationDto timeQualityConfiguration;

    public TimestampingConfigurationDto() {
        super(SigningWorkflowType.TIMESTAMPING);
    }
}
