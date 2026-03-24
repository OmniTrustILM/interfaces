package com.czertainly.api.model.core.signing.tsp;

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
}
