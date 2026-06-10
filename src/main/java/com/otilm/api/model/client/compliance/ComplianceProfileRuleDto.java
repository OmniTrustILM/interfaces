package com.otilm.api.model.client.compliance;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.certificate.CertificateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class ComplianceProfileRuleDto extends NameAndUuidDto {

    @Schema(description = "Description of the rule", examples = {"Sample rule description"})
    private String description;

    @Schema(description = "Connector Name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorName;

    @Schema(description = "Connector UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorUuid;

    @Schema(description = "Connector Kind", requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Schema(description = "Group UUID")
    private String groupUuid;

    @Schema(description = "Certificate type for the rule", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"X509"})
    private CertificateType certificateType;

    @Schema(description = "List of attributes for the rule", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> attributes = new ArrayList<>();

    @Schema(description = "UUID of the Compliance Profile", requiredMode = Schema.RequiredMode.REQUIRED)
    private String complianceProfileUuid;

    @Schema(description = "Name of the Compliance Profile", requiredMode = Schema.RequiredMode.REQUIRED)
    private String complianceProfileName;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("description", description)
                .append("connectorName", connectorName)
                .append("connectorUuid", connectorUuid)
                .append("kind", kind)
                .append("groupUuid", groupUuid)
                .append("certificateType", certificateType)
                .append("attributes", attributes)
                .append("complianceProfileUuid", complianceProfileUuid)
                .append("complianceProfileName", complianceProfileName)
                .append("uuid", uuid)
                .append("name", name)
                .toString();
    }
}
