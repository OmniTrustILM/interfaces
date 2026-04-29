package com.otilm.api.model.core.compliance;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.certificate.CertificateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Setter
@Getter
public class ComplianceRulesDto extends NameAndUuidDto {
    @Schema(description = "Description of the rule", examples = {"Sample rule description"})
    private String description;

    @Schema(description = "Certificate type for the rule", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"X509"})
    private CertificateType certificateType;

    //Default getters and setters
    @Schema(description = "Attributes of the rule")
    private List<ResponseAttribute> attributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("attributes", attributes)
                .append("description", description)
                .append("certificateType", certificateType)
                .toString();
    }
}
