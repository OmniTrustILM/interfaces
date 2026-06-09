package com.otilm.api.model.client.compliance.v2;

import com.otilm.api.model.connector.compliance.v2.ComplianceRuleRequestDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter
@Setter
@ToString
public class ProviderComplianceRulesRequestDto {

    @NotNull
    @Schema(description = "UUID of the Compliance Provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID connectorUuid;

    @NotNull
    @Schema(description = "Kind of the Compliance Provider", requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Schema(description = "Provider rules associated", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<ComplianceRuleRequestDto> rules = new HashSet<>();

    @Schema(description = "Provider groups UUIDS to associate profile with", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<UUID> groups = new HashSet<>();

    @JsonIgnore
    @AssertTrue(message = "At least one provider group or rule needs to be specified")
    public boolean isValid() {
        return (rules != null && !rules.isEmpty()) || (groups != null && !groups.isEmpty());
    }

}
