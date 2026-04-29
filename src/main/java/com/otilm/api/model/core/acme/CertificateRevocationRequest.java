package com.otilm.api.model.core.acme;


import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Fields to be used by the client for revoking a certificate issued to the
 * same Account
 */
public class CertificateRevocationRequest {
    /**
     * The Certificate to be revoked, in the base64url-encoded version of the DER format.  (Note: Because this field
     * uses base64url, and does not include headers, it is different from PEM.).
     */
    @Schema(description = "Certificate in base64url-encoded version of DER format",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"<base64url-encoded version of the DER format>"})
    private String certificate;

    /**
     * Reason for the Certificate revocation. The reason is as specified by the <a href="https://www.rfc-editor.org/rfc/rfc5280.html#section-5.3.1">RFC5280 section 5.3.1</a>
     * The value is optional. If none of the reason is given, then the server can leave the reason blank for OCSP
     * meaning the server can set the code for revocation is UNSPECIFIED.
     *
     */
    @Schema(description = "Revocation reason code",
            externalDocs = @ExternalDocumentation(description = "RFC 5280, section 5.3.1", url = "https://datatracker.ietf.org/doc/html/rfc5280#section-5.3.1"),
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "1")
    private Integer reason;

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("certificate", certificate)
                .append("reason", reason)
                .toString();
    }
}
