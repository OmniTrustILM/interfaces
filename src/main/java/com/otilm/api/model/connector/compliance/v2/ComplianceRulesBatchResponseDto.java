package com.otilm.api.model.connector.compliance.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ComplianceRulesBatchResponseDto {
    @Schema(description = "List of the batch rules to retrieve",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ComplianceRuleResponseDto> rules = new ArrayList<>();

    @Schema(description = "UUIDs of the groups to retrieve",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ComplianceGroupBatchResponseDto> groups = new ArrayList<>();

}
