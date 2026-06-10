package com.otilm.api.model.core.acme;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.raprofile.SimplifiedRaProfileDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.protocol.ProtocolCertificateAssociationsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AcmeProfileDto extends NameAndUuidDto {
    @Schema(description = "Enabled flag - true = enabled; false = disabled",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;

    @Schema(description = "ACME Profile description", examples = {"Sample description"})
    private String description;
    @Schema(description = "Terms of Service URL", examples = {"https://sample-url.com/termsOfService"})
    private String termsOfServiceUrl;
    @Schema(description = "Website URL", examples = {"https://sample-company.com"})
    private String websiteUrl;
    @Schema(description = "DNS Resolver IP address", examples = {"8.8.8.8"})
    private String dnsResolverIp;
    @Schema(description = "DNS Resolver port number", examples = {"53"})
    private String dnsResolverPort;
    @Schema(description = "RA Profile")
    private SimplifiedRaProfileDto raProfile;
    @Schema(description = "Retry interval for ACME client requests", example = "30")
    private Integer retryInterval;
    @Schema(description = "Disable new Orders (change in Terms of Service)", example = "false")
    private Boolean termsOfServiceChangeDisable;
    @Schema(description = "Order validity", example = "36000")
    private Integer validity;
    @Schema(description = "ACME Directory URL", examples = {"https://some-server.com/api/v1/protocols/acme/profile1/directory"})
    private String directoryUrl;
    @Schema(description = "Changes of Terms of Service URL", examples = {"https://some-company.com/termsOfService/change"})
    private String termsOfServiceChangeUrl;
    @Schema(description = "Require Contact information for new Account", example = "true")
    private Boolean requireContact;
    @Schema(description = "Require new Account to agree on Terms of Service", example = "true")
    private Boolean requireTermsOfService;
    @Schema(description = "List of Attributes to issue a Certificate")
    private List<ResponseAttribute> issueCertificateAttributes;
    @Schema(description = "List of Attributes to revoke a Certificate")
    private List<ResponseAttribute> revokeCertificateAttributes;
    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes;

    @Schema(description = "Associations to set for certificates issued by the protocol", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ProtocolCertificateAssociationsDto certificateAssociations;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("isEnabled", enabled)
                .append("description", description)
                .append("termsOfServiceUrl", termsOfServiceUrl)
                .append("websiteUrl", websiteUrl)
                .append("dnsResolverIp", dnsResolverIp)
                .append("dnsResolverPort", dnsResolverPort)
                .append("raProfile", raProfile)
                .append("retryInterval", retryInterval)
                .append("termsOfServiceChangeDisable", termsOfServiceChangeDisable)
                .append("validity", validity)
                .append("directoryUrl", directoryUrl)
                .append("termsOfServiceChangeUrl", termsOfServiceChangeUrl)
                .append("requireContact", requireContact)
                .append("requireTermsOfService", requireTermsOfService)
                .append("issueCertificateAttributes", issueCertificateAttributes)
                .append("revokeCertificateAttributes", revokeCertificateAttributes)
                .append("customAttributes", customAttributes)
                .toString();
    }
}
