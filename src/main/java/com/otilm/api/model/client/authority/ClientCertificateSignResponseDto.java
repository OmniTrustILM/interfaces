package com.otilm.api.model.client.authority;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Response to signing request
 */
public class ClientCertificateSignResponseDto {

    @Schema(description = "Date of signed certificate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String certificateData;

    public String getCertificateData() {
        return certificateData;
    }

    public void setCertificateData(String certificateData) {
        this.certificateData = certificateData;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("certificateData", certificateData)
                .toString();
    }
}

