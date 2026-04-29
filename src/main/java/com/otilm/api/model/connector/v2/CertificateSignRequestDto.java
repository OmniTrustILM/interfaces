package com.otilm.api.model.connector.v2;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.enums.CertificateRequestFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Class representing a request to sign CSR
 */
@Setter
@Getter
public class CertificateSignRequestDto {

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
            description = "List of Attributes to issue Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> attributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("request", request)
                .append("format", format)
                .append("raProfileAttributes", raProfileAttributes)
                .append("attributes", attributes)
                .toString();
    }
}
