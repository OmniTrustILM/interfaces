package com.otilm.api.model.core.compliance.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class ComplianceProfileListDto {
    @Schema(description = "Compliance profile UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = "Compliance profile name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Compliance Profile description")
    private String description;

    @Schema(description = "Provider (external) rules count", requiredMode = Schema.RequiredMode.REQUIRED)
    private int providerRulesCount;

    @Schema(description = "Provider (external) groups count", requiredMode = Schema.RequiredMode.REQUIRED)
    private int providerGroupsCount;

    @Schema(description = "Workflow (internal) rules count", requiredMode = Schema.RequiredMode.REQUIRED)
    private int internalRulesCount;

    @Schema(description = "Number of associated objects", requiredMode = Schema.RequiredMode.REQUIRED)
    private int associations;
}
