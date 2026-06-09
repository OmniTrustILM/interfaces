package com.otilm.api.model.core.compliance.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class ProviderComplianceRulesDto {
    @Schema(description = "UUID of the Compliance Provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID connectorUuid;

    @Schema(description = "Name of the Compliance Provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorName;

    @Schema(description = "Kind of the Compliance Provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Schema(description = "Provider rules associated", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ComplianceRuleDto> rules = new ArrayList<>();

    @Schema(description = "Provider groups associated", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ComplianceGroupDto> groups = new ArrayList<>();

}
