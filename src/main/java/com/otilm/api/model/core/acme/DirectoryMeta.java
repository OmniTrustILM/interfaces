package com.otilm.api.model.core.acme;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Response for meta field in the Directory list API
 */
public class DirectoryMeta {

    /**
     * URL for the Terms and Service to be accepted by the clients
     */
    @Schema(description = "Terms of Service URL", examples = {"https://sample-url.com/termsOfService"})
    private String termsOfService;

    /**
     * URL of the website for the ACME configuration
     */
    @Schema(description = "Website URL", examples = {"https://sample-company.com"})
    private String website;

    /**
     * Boolean value of the need of external account requirement
     */
    @Schema(description = "External Account Binding flag",
            defaultValue = "false")
    private Boolean externalAccountRequired;

    /**
     * CAA record validation list of hostnames
     */
    @Schema(description = "Array of CAA record validation servers",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "[\"example1.com\", \"example2.com\"]")
    private String[] caaIdentities;

    public String getTermsOfService() {
        return termsOfService;
    }

    public void setTermsOfService(String termsOfService) {
        this.termsOfService = termsOfService;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Boolean getExternalAccountRequired() {
        return externalAccountRequired;
    }

    public void setExternalAccountRequired(Boolean externalAccountRequired) {
        this.externalAccountRequired = externalAccountRequired;
    }

    public String[] getCaaIdentities() { return caaIdentities; }

    public void setCaaIdentities(String[] caaIdentities) { this.caaIdentities = caaIdentities; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("termsOfService", termsOfService)
                .append("website", website)
                .append("externalAccountRequired", externalAccountRequired)
                .append("caaIdentities", caaIdentities)
                .toString();
    }
}
