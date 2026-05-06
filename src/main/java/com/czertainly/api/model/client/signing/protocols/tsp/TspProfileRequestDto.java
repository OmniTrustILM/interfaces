package com.czertainly.api.model.client.signing.protocols.tsp;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.validation.ValidName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "TspProfileRequestDto", description = "Request to create or update a TSP (Timestamping Protocol) Profile")
public class TspProfileRequestDto {

    @NotBlank
    @ValidName
    @Schema(description = "Name of the TSP Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "TSP-Profile-1")
    private String name;

    @Schema(description = "Description of the TSP Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "TSP profile for production timestamping")
    private String description;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();
}
