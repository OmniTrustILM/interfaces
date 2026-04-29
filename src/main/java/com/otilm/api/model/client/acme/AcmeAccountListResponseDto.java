package com.otilm.api.model.client.acme;

import com.otilm.api.model.client.raprofile.SimplifiedRaProfileDto;
import com.otilm.api.model.core.acme.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Set of properties to represent the Account object from ACME.
 */
public class AcmeAccountListResponseDto {

    @Schema(
            description = "ID of the Account",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"HJAT6gc7i6"}
    )
    private String accountId;
    @Schema(
            description = "UUID of the Account",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"6b55de1c-844f-11ec-a8a3-0242ac120002"}
    )
    private String uuid;
    @Schema(
            description = "Enabled flag. true = enabled, false=disabled",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "false"
    )
    private Boolean enabled;
    @Schema(
            description = "Total number of Orders",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "4"
    )
    private Integer totalOrders;
    @Schema(
            description = "Status of the Account",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"VALID"}
    )
    private AccountStatus status;
    @Schema(
            description = "RA Profile",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            examples = {"RAProfile1"}
    )
    private SimplifiedRaProfileDto raProfile;
    @Schema(
            description = "Name of the ACME Profile",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            examples = {"ACMEProfile1"}
    )
    private String acmeProfileName;

    @Schema(
            description = "UUID of the ACME Profile",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            examples = {"6b55de1c-844f-11ec-a8a3-0242ac120002"}
    )
    private String acmeProfileUuid;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }


    public SimplifiedRaProfileDto getRaProfile() {
        return raProfile;
    }

    public void setRaProfile(SimplifiedRaProfileDto raProfile) { this.raProfile = raProfile; }

    public String getAcmeProfileName() {
        return acmeProfileName;
    }

    public void setAcmeProfileName(String acmeProfileName) {
        this.acmeProfileName = acmeProfileName;
    }

    public String getAcmeProfileUuid() {
        return acmeProfileUuid;
    }

    public void setAcmeProfileUuid(String acmeProfileUuid) {
        this.acmeProfileUuid = acmeProfileUuid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("accountId", accountId)
                .append("uuid", uuid)
                .append("enabled", enabled)
                .append("totalOrders", totalOrders)
                .append("status", status)
                .append("raProfileName", raProfile.getName())
                .append("acmeProfileName", acmeProfileName)
                .append("acmeProfileUuid", acmeProfileUuid)
                .toString();
    }
}
