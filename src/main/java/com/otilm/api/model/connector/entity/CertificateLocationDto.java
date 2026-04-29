package com.otilm.api.model.connector.entity;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.core.certificate.CertificateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Getter
@Setter
public class CertificateLocationDto {

    @Schema(description = "Base64-encoded Certificate content",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String certificateData;

    @Schema(
            description = "Metadata of the Certificate related to the Location"
    )
    private List<MetadataAttribute> metadata;

    @Schema(description = "Type of the Certificate",
            defaultValue = "X509",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateType certificateType;

    @Schema(description = "If the Certificate in Location has associated private key",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean withKey;

    @Schema(
            description = "List of Attributes to replace Certificate"
    )
    private List<BaseAttribute> pushAttributes;

    @Schema(
            description = "List of Attributes to renew Certificate"
    )
    private List<BaseAttribute> csrAttributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("certificateData", certificateData)
                .append("metadata", metadata)
                .append("certificateType", certificateType)
                .append("pushAttributes", pushAttributes)
                .append("csrAttributes", csrAttributes)
                .append("withKey", withKey)
                .toString();
    }
}
