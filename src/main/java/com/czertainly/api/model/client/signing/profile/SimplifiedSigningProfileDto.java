package com.czertainly.api.model.client.signing.profile;

import com.czertainly.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SimplifiedSigningProfileDto extends NameAndUuidDto {

    @Schema(description = "Enabled flag - true = enabled; false = disabled", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;
}
