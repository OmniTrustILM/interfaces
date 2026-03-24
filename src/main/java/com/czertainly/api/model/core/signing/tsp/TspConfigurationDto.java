package com.czertainly.api.model.core.signing.tsp;

import com.czertainly.api.model.client.attribute.ResponseAttribute;
import com.czertainly.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TspConfigurationDto", description = "TSP (Timestamping Protocol) configuration details")
@ToString(callSuper = true)
public class TspConfigurationDto extends NameAndUuidDto {

    @Schema(description = "Description of the TSP Configuration", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "TSP configuration for production timestamping")
    private String description;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes = new ArrayList<>();
}
