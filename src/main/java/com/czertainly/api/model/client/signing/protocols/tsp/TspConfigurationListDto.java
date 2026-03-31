package com.czertainly.api.model.client.signing.protocols.tsp;

import com.czertainly.api.model.client.signing.profile.SimplifiedSigningProfileDto;
import com.czertainly.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TspConfigurationListDto", description = "TSP (Timestamping Protocol) configuration details for listing")
@ToString(callSuper = true)
public class TspConfigurationListDto extends NameAndUuidDto {

    @Schema(description = "Description of the TSP Configuration", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "TSP configuration for production timestamping")
    private String description;

    @Schema(description = "Default Signing Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "6b55de1c-844f-11ec-a8a3-0242ac120002")
    private SimplifiedSigningProfileDto defaultSigningProfile;

    @Schema(description = "Enabled flag of the TSP Configuration", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private boolean enabled;
}
