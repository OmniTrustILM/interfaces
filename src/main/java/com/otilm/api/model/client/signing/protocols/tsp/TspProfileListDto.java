package com.otilm.api.model.client.signing.protocols.tsp;

import com.otilm.api.model.client.signing.profile.SimplifiedSigningProfileDto;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TspProfileListDto", description = "TSP (Timestamping Protocol) profile details for listing")
@ToString(callSuper = true)
public class TspProfileListDto extends NameAndUuidDto {

    @Schema(description = "Description of the TSP Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "TSP profile for production timestamping")
    private String description;

    @Schema(description = "Default Signing Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SimplifiedSigningProfileDto defaultSigningProfile;

    @Schema(
            description = "TSP URL for signing",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "https://ilm.otilm.com/api/v1/protocols/tsp/tsp-profile-1/sign"
    )
    private String signingUrl;

    @Schema(description = "Enabled flag of the TSP Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private boolean enabled;
}
