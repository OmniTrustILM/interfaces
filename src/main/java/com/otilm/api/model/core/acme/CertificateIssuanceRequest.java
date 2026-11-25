package com.otilm.api.model.core.acme;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Parameters for the client to be sent for the new Order.
 */
public class CertificateIssuanceRequest {
    /**
     * List of the Identifiers that the client wishes to submit an Order for.
     */
    @Schema(description = "List of Identifiers for the Order", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Identifier> identifiers;

    /**
     * The requested value of notBefore field in the certificate. The date format is as defined in the RFC3339.
     */
    @Schema(description = "Requested value of notBefore field in the certificate", format = "date-time",
            type = "string")
    private String notBefore;

    /**
     * The requested value of notAfter field in the certificate. The format of the certificate is as defined in the
     * RFC3339.
     */
    @Schema(description = "Requested value of notAfter field in the certificate", format = "date-time", type = "string")
    private String notAfter;

    /**
     * Certificate identifier of the certificate the new Order replaces (RFC 9773).
     */
    @Schema(description = "Certificate identifier of the certificate this Order replaces",
            examples = {"aYhba4dGQEHhs3uEe6CuLN4ByNQ.AIdlQyE"})
    private String replaces;

    public List<Identifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

    public String getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(String notBefore) {
        this.notBefore = notBefore;
    }

    public String getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(String notAfter) {
        this.notAfter = notAfter;
    }

    public String getReplaces() {
        return replaces;
    }

    public void setReplaces(String replaces) {
        this.replaces = replaces;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("identifiers", identifiers)
                .append("notBefore", notBefore)
                .append("notAfter", notAfter)
                .append("replaces", replaces)
                .toString();
    }
}
