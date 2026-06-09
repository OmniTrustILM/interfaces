package com.otilm.api.model.core.compliance;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ComplianceConnectorAndRulesDto {
    @Schema(description = "Name of the Compliance Provider")
    private String connectorName;

    @Schema(description = "UUID of the Compliance Provider")
    private String connectorUuid;

    @Schema(description = "Kind of the Compliance Provider")
    private String kind;

    @Schema(description = "Rules associated")
    private List<ComplianceRulesDto> rules;

    //Default getters and setters

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public String getConnectorUuid() {
        return connectorUuid;
    }

    public void setConnectorUuid(String connectorUuid) {
        this.connectorUuid = connectorUuid;
    }

    public List<ComplianceRulesDto> getRules() {
        return rules;
    }

    public void setRules(List<ComplianceRulesDto> rules) {
        this.rules = rules;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", connectorUuid)
                .append("name", connectorName)
                .append("rules", rules)
                .append("kind", kind)
                .toString();
    }
}
