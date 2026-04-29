package com.otilm.api.model.client.compliance;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ComplianceProfileRulesDeletionRequestDto {
    @Schema(description = "UUID of the Compliance Provider",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"c35bc88c-d0ef-11ec-9d64-0242ac120005"})
    private String connectorUuid;

    @Schema(description = "Kind of the Compliance Provider",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"x509"})
    private String kind;

    @Schema(description = "UUID of the rule to be deleted",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"18324c94-e95c-11ec-8fea-0242ac120002"})
    private String ruleUuid;

    public String getConnectorUuid() {
        return connectorUuid;
    }

    public void setConnectorUuid(String connectorUuid) {
        this.connectorUuid = connectorUuid;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getRuleUuid() {
        return ruleUuid;
    }

    public void setRuleUuid(String ruleUuid) {
        this.ruleUuid = ruleUuid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("connectorUuid", connectorUuid)
                .append("ruleUuid", ruleUuid)
                .append("kind", kind)
                .toString();
    }
}
