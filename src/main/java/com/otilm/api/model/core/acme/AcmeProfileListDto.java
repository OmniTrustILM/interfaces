package com.otilm.api.model.core.acme;

import com.otilm.api.model.client.raprofile.SimplifiedRaProfileDto;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AcmeProfileListDto extends NameAndUuidDto {

    @Schema(description = "Enabled flag - true = enabled; false = disabled",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "false")
    private boolean enabled;
    @Schema(description = "ACME Profile description", examples = {"Some description"})
    private String description;
    @Schema(description = "RA Profile")
    private SimplifiedRaProfileDto raProfile;

    @Schema(description = "URL of the ACME Directory", examples = {"https://some-server.com/api/v1/protocols/acme/profile1/directory"})
    private String directoryUrl;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SimplifiedRaProfileDto getRaProfile() { return raProfile; }

    public void setRaProfile(SimplifiedRaProfileDto raProfile) {
        this.raProfile = raProfile;
    }

    public String getDirectoryUrl() {
        return directoryUrl;
    }

    public void setDirectoryUrl(String directoryUrl) {
        this.directoryUrl = directoryUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AcmeProfileListDto() {
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("isEnabled", enabled)
                .append("description", description)
                .append("raProfileName", raProfile == null ? "N/A" : raProfile.getName())
                .append("raProfileUuid", raProfile == null ? "N/A" : raProfile.getUuid())
                .append("directoryUrl", directoryUrl)
                .toString();
    }
}
