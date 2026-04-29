package com.otilm.api.model.connector.compliance;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/*
Contains the list of parameters required for creating a new compliance check request.
This object should only be used by the core and connector and not by the client.
When a new compliance check is to be initiated, Core should use this class to frame the
request for the connector. And the connector should accept only this object as input for
any incoming compliance check request
 */
public class ComplianceRequestDto {

    @Schema(description = "Base64 encoded Certificate content", requiredMode = Schema.RequiredMode.REQUIRED)
    private String certificate;

    @Schema(description = "List of UUIDs of Compliance rules")
    private List<ComplianceRequestRulesDto> rules;

    //Default getters and setters

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public List<ComplianceRequestRulesDto> getRules() {
        return rules;
    }

    public void setRules(List<ComplianceRequestRulesDto> rules) {
        this.rules = rules;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("certificate", certificate)
                .append("rules", rules)
                .toString();
    }
}
