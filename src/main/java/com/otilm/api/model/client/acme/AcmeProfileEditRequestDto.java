package com.otilm.api.model.client.acme;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.acme.AcmeIdentifierAuthorizationMode;
import com.otilm.api.model.core.acme.AcmePreauthorizedIdentifierDto;
import com.otilm.api.model.core.protocol.ProtocolCertificateAssociationsRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
public class AcmeProfileEditRequestDto {

    @Schema(description = "Description of the ACME Profile", examples = {"Sample description"})
    private String description;
    @Schema(description = "Terms of Service URL", examples = {"https://sample-url.com/termsOfService"})
    private String termsOfServiceUrl;
    @Schema(description = "Website URL", examples = {"https://sample-url.com"})
    private String websiteUrl;
    @Schema(description = "DNS Resolver IP address", defaultValue = "System Default", examples = {"8.8.8.8"})
    private String dnsResolverIp;
    @Schema(description = "DNS Resolver port number", defaultValue = "53", examples = {"53"})
    private String dnsResolverPort;
    @Schema(description = "RA Profile UUID", examples = {"6b55de1c-844f-11ec-a8a3-0242ac120002"})
    private String raProfileUuid;
    @Schema(description = "Retry interval for the Orders", defaultValue = "30", example = "60")
    private Integer retryInterval;
    @Schema(description = "Disable new Orders due to change in Terms of Service", defaultValue = "false",
            example = "false")
    private Boolean termsOfServiceChangeDisable;

    @Schema(description = "Changes of Terms of Service URL",
            examples = {"https://sample-url.com/termsOfService/change"})
    private String termsOfServiceChangeUrl;
    @Schema(description = "Order Validity", defaultValue = "36000", example = "3000")
    private Integer validity;
    @Schema(description = "List of Attributes to issue Certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> issueCertificateAttributes;
    @Schema(description = "List of Attributes to revoke Certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> revokeCertificateAttributes;
    @Schema(description = "Require contact information for new Account", defaultValue = "false", example = "true")
    private Boolean requireContact;
    @Schema(description = "Require new Account to agree on Terms of Service", defaultValue = "false", example = "false")
    private Boolean requireTermsOfService;
    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;

    @Valid
    @Schema(description = "Associations to set for certificates issued by the protocol",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ProtocolCertificateAssociationsRequestDto certificateAssociations;

    @Valid
    @Schema(description = "Identifiers an account may obtain from this profile without proving control of them. "
            + "An order whose identifiers are all covered is ready at creation and carries no challenges.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<AcmePreauthorizedIdentifierDto> preauthorizedIdentifiers = new ArrayList<>();

    @Schema(description = "What happens to an ordered identifier the pre-authorization policy does not cover. "
            + "'preauthorizedOrChallenge', the default, sends it through the usual http-01 and dns-01 flow. "
            + "'preauthorizedOnly' refuses the order, and needs at least one pre-authorized identifier - to stop "
            + "a profile accepting orders at all, disable new orders instead.",
            defaultValue = "preauthorizedOrChallenge", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private AcmeIdentifierAuthorizationMode identifierAuthorizationMode;

    public Boolean isRequireTermsOfService() {
        return requireTermsOfService;
    }

    public Boolean isRequireContact() {
        return requireContact;
    }

    public Boolean isTermsOfServiceChangeDisable() {
        return termsOfServiceChangeDisable;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("description", description)
                .append("termsOfServiceUrl", termsOfServiceUrl)
                .append("websiteUrl", websiteUrl)
                .append("dnsResolverIp", dnsResolverIp)
                .append("dnsResolverPort", dnsResolverPort)
                .append("raProfileUuid", raProfileUuid)
                .append("retryInterval", retryInterval)
                .append("termsOfServiceChangeDisable", termsOfServiceChangeDisable)
                .append("termsOfServiceChangeUrl", termsOfServiceChangeUrl)
                .append("validity", validity)
                .append("issueCertificateAttributes", issueCertificateAttributes)
                .append("revokeCertificateAttributes", revokeCertificateAttributes)
                .append("requireContact", requireContact)
                .append("requireTermsOfService", requireTermsOfService)
                .append("customAttributes", customAttributes)
                .append("preauthorizedIdentifiers", preauthorizedIdentifiers)
                .append("identifierAuthorizationMode", identifierAuthorizationMode)
                .toString();
    }
}
