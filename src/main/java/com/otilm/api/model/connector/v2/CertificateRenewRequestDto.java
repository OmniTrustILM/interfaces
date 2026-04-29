package com.otilm.api.model.connector.v2;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.core.enums.CertificateRequestFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Class representing a request to renew certificate
 */
@Setter
@Getter
public class CertificateRenewRequestDto {

    @Schema(
            description = "Certificate signing request encoded as Base64 string",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String request;

    @Schema(
            description = "Certificate signing request format",
            defaultValue = "pkcs10"
    )
    private CertificateRequestFormat format;

    @Schema(
            description = "List of RA Profiles attributes",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> raProfileAttributes;

    @Schema(
            description = "Base64 Certificate content. (Certificate to be renewed)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String certificate;

    @Schema(
            description = "Metadata for the Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<MetadataAttribute> meta;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("request", request)
                .append("format", format)
                .append("certificate", certificate)
                .append("raProfileAttributes", raProfileAttributes)
                .append("meta", meta)
                .toString();
    }
}
