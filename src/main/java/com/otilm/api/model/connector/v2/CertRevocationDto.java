package com.otilm.api.model.connector.v2;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.authority.CertificateRevocationReason;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Class with parameter to revoke any certificate.
 */
public class CertRevocationDto {

    @Schema(description = "Revocation reason",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private CertificateRevocationReason reason;

    @Schema(description = "List of RA Profiles attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute>raProfileAttributes;

    @Schema(description = "List of Attributes to revoke Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute>attributes;

    @Schema(description = "Base64 Certificate content. (Certificate to be revoked)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String certificate;

    public CertificateRevocationReason getReason() {
        return reason;
    }

    public void setReason(CertificateRevocationReason reason) {
        this.reason = reason;
    }

    public List<RequestAttribute>getRaProfileAttributes() {
        return raProfileAttributes;
    }

    public void setRaProfileAttributes(List<RequestAttribute>raProfileAttributes) {
        this.raProfileAttributes = raProfileAttributes;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public List<RequestAttribute>getAttributes() {
        return attributes;
    }

    public void setAttributes(List<RequestAttribute>attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("reason", reason)
                .append("raProfile", raProfileAttributes)
                .append("attributes", attributes)
                .toString();
    }
}
