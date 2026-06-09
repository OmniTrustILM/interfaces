package com.otilm.api.model.core.cmp;

import com.otilm.api.model.client.raprofile.SimplifiedRaProfileDto;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CmpProfileDto extends NameAndUuidDto {

    @Schema(
            description = "Enabled flag - true = enabled; false = disabled",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean enabled;

    @Schema(
            description = "Variant of the CMP Profile",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CmpProfileVariant variant;

    @Schema(
            description = "CMP Profile description",
            examples = {"Sample text description"}
    )
    private String description;

    @Schema(
            description = "RA Profile associated with the CMP Profile"
    )
    private SimplifiedRaProfileDto raProfile;

    @Schema(
            description = "CMP URL",
            examples = {"https://your-domain.com/api/v1/protocols/cmp/cmpProfile"}
    )
    private String cmpUrl;
}
