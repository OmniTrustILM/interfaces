package com.otilm.api.model.core.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UserCertificateDto {

    @Schema(description = "UUID of the certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    @Schema(description = "Fingerprint of the certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fingerprint;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("fingerprint", fingerprint)
                .toString();
    }
}
