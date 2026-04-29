package com.otilm.api.model.core.acme;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Dto for the client to send for finalizing the Order.
 */
public class CertificateFinalizeRequest {
    /**
     * A CSR encoding the parameters for the certificate being requested [RFC2986].  The CSR is sent in the
     * base64url-encoded version of the DER format. (Note: Because this field uses base64url, and does not include
     * headers, it is different from PEM.).
     */
    @Schema(
            description = "CSR in Base64url-encoded version of the DER format",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"<base64url-encoded version of the DER format>"}
    )
    private String csr;

    public String getCsr() {
        return csr;
    }

    public void setCsr(String csr) {
        this.csr = csr;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("csr", csr)
                .toString();
    }

}
