package com.otilm.api.model.client.raprofile;

import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.UUID;

/**
 * Class representing RA profile
 */
@Getter
@Setter
@NoArgsConstructor
public class SimplifiedRaProfileDto extends NameAndUuidDto {

    @Schema(description = "Enabled flag - true = enabled; false = disabled",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enabled;

    @Schema(description = "Authority Instance UUID")
    private String authorityInstanceUuid;

    public SimplifiedRaProfileDto(UUID uuid, String name, Boolean enabled, UUID authorityInstanceUuid) {
        super(uuid != null ? uuid.toString() : null, name);
        this.enabled = enabled;
        this.authorityInstanceUuid = authorityInstanceUuid != null ? authorityInstanceUuid.toString() : null;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("enabled", enabled)
                .append("authorityInstanceUuid", authorityInstanceUuid)
                .toString();
    }
}
