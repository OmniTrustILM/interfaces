package com.otilm.api.model.connector.v2;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Class representing a request to identify certificate
 */
@Data
public class CertificateIdentificationRequestDto {
    @Schema(description = "Base64 Certificate content. (certificate to be identified)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String certificate;

    @Schema(description = "List of RA Profiles attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute>raProfileAttributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("certificate", certificate)
                .append("raProfileAttributes", raProfileAttributes)
                .toString();
    }
}
