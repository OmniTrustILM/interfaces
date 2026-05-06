package com.czertainly.api.model.client.signing.protocols.tsp;

import com.czertainly.api.model.common.NameAndUuidDto;
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

    @Schema(description = "Enabled flag of the TSP Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private boolean enabled;
}
