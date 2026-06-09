package com.otilm.api.model.core.certificate;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.core.compliance.ComplianceRuleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/*
Contains the list of parameters returned by the Compliance Provider after
checking the certificate for compliance. This response will be received
by the Core from the Connector once the compliance check is completed.
 */
@Setter
@Getter
public class CertificateComplianceResultDto {

    @Schema(description = "Name of the Compliance Provider", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"Provider1"})
    private String connectorName;

    @Schema(description = "Name of the rule",requiredMode = Schema.RequiredMode.REQUIRED, examples = {"RuleName"})
    private String ruleName;

    @Schema(description = "Description of the rule",requiredMode = Schema.RequiredMode.REQUIRED, examples = {"Description sample"})
    private String ruleDescription;

    @Schema(description = "Status of the rule",requiredMode = Schema.RequiredMode.REQUIRED, examples = {"nok"})
    private ComplianceRuleStatus status;

    @Schema(description = "Attributes of the rule")
    private List<ResponseAttribute> attributes;

    //Default getters and setters


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("ruleName", ruleName)
                .append("connectorName", connectorName)
                .append("status", status)
                .append("ruleDescription", ruleDescription)
                .append("attributes", attributes)
                .toString();
    }
}
