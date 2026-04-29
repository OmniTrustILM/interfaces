package com.otilm.api.model.connector.compliance;

import com.otilm.api.model.core.compliance.ComplianceRuleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/*
Contains the description for rules and its validation results. This object is part of
the ComplianceResponseDto and describes in detail the list of rules applied and their
individual status.
 */
public class ComplianceResponseRulesDto {
    @Schema(description = "UUID of the rule", requiredMode = Schema.RequiredMode.REQUIRED, example = "b11c9be1-b619-4ef5-be1b-a1cd9ef265b7")
    private String uuid;

    @Schema(description = "Name of the rule", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"Rule1"})
    private String name;

    @Schema(description = "Rule status", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"ok"})
    private ComplianceRuleStatus status;

    //Default getters and setters

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ComplianceRuleStatus getStatus() {
        return status;
    }

    public void setStatus(ComplianceRuleStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("status", status)
                .toString();
    }
}
