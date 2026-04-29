package com.otilm.api.model.core.raprofile;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing RA profile
 */
@Getter
@Setter
public class RaProfileDto extends NameAndUuidDto {

    @Schema(description = "Description of RA Profile",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "UUID of Authority provider",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String authorityInstanceUuid;

    @Schema(description = "Name of Authority instance",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String authorityInstanceName;

    @Schema(description = "Has Authority of legacy authority provider",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean legacyAuthority;

    @Schema(description = "Enabled flag - true = enabled; false = disabled",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enabled;

    @Schema(description = "List of RA Profiles attributes",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of Custom Attributes",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes;

    @Schema(description = "List of protocols enabled",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> enabledProtocols;

    @Schema(description = "Settings for validation of certificates associated with the RA Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private RaProfileCertificateValidationSettingsDto certificateValidationSettings;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("description", description)
                .append("authorityInstanceUuid", authorityInstanceUuid)
                .append("attributes", attributes)
                .append("customAttributes", customAttributes)
                .append("enabled", enabled)
                .append("authorityInstanceName", authorityInstanceName)
                .append("legacyAuthority", legacyAuthority)
                .toString();
    }
}
