package com.czertainly.api.model.core.signing.tsp;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "TspConfigurationUpdateRequestDto", description = "Request to update an existing TSP (Timestamping Protocol) Configuration")
public class TspConfigurationUpdateRequestDto {

    @NotBlank
    @Schema(description = "Name of the TSP Configuration", requiredMode = Schema.RequiredMode.REQUIRED, example = "TSP-Config-1")
    private String name;

    @Schema(description = "Description of the TSP Configuration", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "TSP configuration for production timestamping")
    private String description;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();
}
