package com.czertainly.api.model.connector.v2;

import com.czertainly.api.model.common.attribute.common.AttributeContent;
import com.czertainly.api.model.common.attribute.common.MetadataAttribute;
import com.czertainly.api.model.core.certificate.CertificateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Response containing signed certificate data
 */
@Data
public class CertificateDataResponseDto {

    @Schema(description = "Base64 encoded Certificate content. Required for synchronous "
                    + "(HTTP 200) responses; absent for asynchronous (HTTP 202) responses, "
                    + "where the operation is still in flight and only optional metadata "
                    + "may be carried in the body.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String certificateData;

    @Schema(description = "UUID of Certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String uuid;

    @Schema(
            description = "Metadata for the Certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<MetadataAttribute> meta;

    @Schema(description = "Type of the Certificate",
            defaultValue = "X509",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateType certificateType;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("certificateData", certificateData)
                .append("certificateUuid", uuid)
                .toString();
    }
}

