package com.otilm.api.model.client.compliance;

import com.otilm.api.model.connector.compliance.ComplianceRequestRulesDto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ComplianceProfileRulesAdditionRequestDto {
    @Schema(description = "UUID of the Compliance Provider",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"c35bc88c-d0ef-11ec-9d64-0242ac120005"})
    private String connectorUuid;

    @Schema(description = "Kind of the Compliance Provider",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"x509"})
    private String kind;

    @Schema(description = "Rule to be added")
    private ComplianceRequestRulesDto rule;


    public String getConnectorUuid() {
        return connectorUuid;
    }

    public void setConnectorUuid(String connectorUuid) {
        this.connectorUuid = connectorUuid;
    }

    public ComplianceRequestRulesDto getRule() {
        return rule;
    }

    public void setRules(ComplianceRequestRulesDto rule) {
        this.rule = rule;
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
                .append("rule", rule)
                .append("kind", kind)
                .toString();
    }
}
