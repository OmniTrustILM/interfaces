package com.otilm.api.model.connector.compliance.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
public class ComplianceRulesBatchRequestDto {

    @Schema(description = "UUIDs of the rules to retrieve", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"b11c9be1-b619-4ef5-be1b-a1cd9ef265b7\"]")
    private Set<UUID> ruleUuids = new HashSet<>();

    @Schema(description = "UUIDs of the groups to retrieve", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"b11c9be1-b619-4ef5-be1b-a1cd9ef265b7\"]")
    private Set<UUID> groupUuids = new HashSet<>();

    @Schema(description = "Flag to determine whether to include group rules in the response", requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "false")
    private boolean withGroupRules;

    @JsonIgnore
    @AssertTrue(message = "At least one group or rule needs to be retrieved in batch")
    public boolean isValid() {
        return (ruleUuids != null && !ruleUuids.isEmpty()) || (groupUuids != null && !groupUuids.isEmpty());
    }

}
