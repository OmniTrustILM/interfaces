package com.otilm.api.model.client.acme;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.protocol.ProtocolCertificateAssociationsRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
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

    @Schema(description = "UUIDs of the secrets holding the External Account Binding HMAC keys accepted by this "
            + "ACME Profile. Each secret's UUID is the kid an ACME client binds a new Account with, and its "
            + "content is the base64url-encoded key. A non-empty list makes External Account Binding mandatory - "
            + "the directory meta then advertises externalAccountRequired. Omitting the property keeps the "
            + "profile's current secrets; send an empty array to clear them and leave Account registration open.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<@NotNull UUID> eabSecretUuids;

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
                .append("eabSecretUuids", eabSecretUuids)
                .toString();
    }
}
