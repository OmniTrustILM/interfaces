package com.otilm.api.model.connector.compliance;

import com.otilm.api.model.core.compliance.ComplianceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/*
Contains the list of parameters returned by the Compliance Provider after
checking the certificate for compliance. This response will be received
by the Core from the Connector once the compliance check is completed.
 */
public class ComplianceResponseDto {

    @Schema(description = "Status of the compliance check", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"ok"})
    private ComplianceStatus status;

    @Schema(description = "List of rules applied and their status")
    private List<ComplianceResponseRulesDto> rules;

    //Default getters and setters
    public ComplianceStatus getStatus() {
        return status;
    }

    public void setStatus(ComplianceStatus status) {
        this.status = status;
    }

    public List<ComplianceResponseRulesDto> getRules() {
        return rules;
    }

    public void setRules(List<ComplianceResponseRulesDto> rules) {
        this.rules = rules;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("status", status)
                .append("rules", rules)
                .toString();
    }
}
