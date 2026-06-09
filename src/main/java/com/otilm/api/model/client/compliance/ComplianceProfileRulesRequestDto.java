package com.otilm.api.model.client.compliance;

import com.otilm.api.model.connector.compliance.ComplianceRequestRulesDto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ComplianceProfileRulesRequestDto {
    @Schema(description = "UUID of the Compliance Provider",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"c35bc88c-d0ef-11ec-9d64-0242ac120005"})
    private String connectorUuid;

    @Schema(description = "Kind of the Compliance Provider",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"x509"})
    private String kind;

    @Schema(description = "Rules for new Compliance Profiles")
    private List<ComplianceRequestRulesDto> rules;

    @Schema(description = "Groups for Compliance Profile")
    private List<String> groups;

    public String getConnectorUuid() {
        return connectorUuid;
    }

    public void setConnectorUuid(String connectorUuid) {
        this.connectorUuid = connectorUuid;
    }

    public List<ComplianceRequestRulesDto> getRules() {
        return rules;
    }

    public void setRules(List<ComplianceRequestRulesDto> rules) {
        this.rules = rules;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
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
                .append("connectorUuid", connectorUuid)
                .append("rules", rules)
                .append("groups", groups)
                .append("kind", kind)
                .toString();
    }
}
