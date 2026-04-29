package com.otilm.api.model.connector.compliance;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.core.certificate.CertificateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/*
List of rules information from the Compliance Provider. These rules will
have name, uuid and the attributes. The attributes of the rules is used
to request for additional information for the rule.
 */
@Getter
@Setter
public class ComplianceRulesResponseDto {
    @Schema(description = "UUID of the rule", requiredMode = Schema.RequiredMode.REQUIRED, example = "b11c9be1-b619-4ef5-be1b-a1cd9ef265b7")
    private String uuid;

    @Schema(description = "UUID of the group to which the rule belongs to", example = "b11c9be1-b619-4ef5-be1b-a1cd9ef265b7")
    private String groupUuid;

    @Schema(description = "Name of the rule", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"Rule1"})
    private String name;

    @Schema(description = "Type of the certificate to which this rule can be applied", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"X509"})
    private CertificateType certificateType;

    @Schema(description = "Rule attributes")
    private List<BaseAttribute> attributes;

    @Schema(description = "Description of the rule", examples = {"Sample rule description"})
    private String description;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("attributes", attributes)
                .append("description", description)
                .append("groupUuid", groupUuid)
                .append("certificateType", certificateType)
                .toString();
    }
}
