package com.otilm.api.model.core.acme;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Data;

@Data
@Schema(description = "An identifier an ACME account may obtain from this profile without proving control of it. "
        + "An order whose identifiers are all covered gets its authorizations created valid, so no http-01 or "
        + "dns-01 challenge is issued for them.")
public class AcmePreauthorizedIdentifierDto implements Serializable {

    @NotNull
    @Schema(description = "Which kind of ordered identifier this entry can cover. An entry covers its own type and "
            + "no other, so a dotted value listed as 'ip' never pre-authorizes the DNS name that reads the same.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private AcmeIdentifierType type;

    @NotBlank
    @Schema(description = "The identifier value: a DNS name, or an IP address for an RFC 8738 ip identifier.",
            example = "apps.example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String value;

    @NotNull
    @Schema(description = "How the value is compared with an ordered identifier. 'exact' covers that identifier "
            + "only. 'subdomain' covers descendants of the name at any depth but not the name itself, so covering "
            + "both takes two entries; it applies to DNS names, an IP address always being matched exactly. DNS "
            + "comparison is case-insensitive, IP comparison is by octets.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private AcmeIdentifierMatchType matchType;

    @Schema(description = "Whether the wildcard identifier of this entry may also be pre-authorized. It sits on the "
            + "entry rather than the profile, so allowing '*.apps.example.com' is a decision about that pattern "
            + "rather than a switch over every listed name. A wildcard is covered only when everything it stands "
            + "for is already inside this entry's scope.", defaultValue = "false", example = "false")
    private boolean allowWildcard;

    /**
     * An address has no hierarchy to descend and no wildcard form, so 'subdomain' and a wildcard allowance would say
     * nothing an ip entry could act on. Refused rather than ignored, so an entry never reads as covering more than it
     * does. A null type is left to its own constraint.
     */
    @JsonIgnore
    @AssertTrue(
            message = "an ip entry is matched exactly: it can use neither the subdomain match type nor a wildcard allowance")
    @Schema(hidden = true)
    public boolean isAddressEntryMatchedExactly() {
        return type != AcmeIdentifierType.IP || (matchType != AcmeIdentifierMatchType.SUBDOMAIN && !allowWildcard);
    }
}
