package com.otilm.api.model.connector.entity;

import com.otilm.api.model.client.attribute.RequestAttribute;
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
public class GenerateCsrResponseDto {

    @Schema(description = "Base64-encoded certificate signing request",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String csr;

    @Schema(description = "CSR Metadata")
    private List<MetadataAttribute> metadata;

    @Schema(description = "Type of the certificate expected to be issued",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateType certificateType;

    @Schema(
            description = "List of Attributes to push Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute>pushAttributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("csr", csr)
                .append("certificateType", certificateType)
                .append("pushAttributes", pushAttributes)
                .toString();
    }

}
