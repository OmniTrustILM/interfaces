package com.otilm.api.model.connector.compliance.v2;

import com.otilm.api.model.core.compliance.ComplianceRuleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.UUID;

/*
Contains the description for rules and its validation results. This object is part of
the ComplianceResponseDto and describes in detail the list of rules applied and their
individual status.
 */
@Getter
@Setter
@Schema(name = "ComplianceResponseRuleDtoV2", description = "Response of Compliance Rule Check V2")
public class ComplianceResponseRuleDto {
    @Schema(description = "UUID of the rule", requiredMode = Schema.RequiredMode.REQUIRED, example = "b11c9be1-b619-4ef5-be1b-a1cd9ef265b7")
    private UUID uuid;

    @Schema(description = "Name of the rule", requiredMode = Schema.RequiredMode.REQUIRED, example = "Rule1")
    private String name;

    @Schema(description = "Rule status", requiredMode = Schema.RequiredMode.REQUIRED, example = ComplianceRuleStatus.Codes.OK)
    private ComplianceRuleStatus status;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("status", status)
                .toString();
    }
}
