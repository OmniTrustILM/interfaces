package com.czertainly.api.model.core.signing.ilm;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "IlmSigningProtocolConfigurationUpdateRequestDto", description = "Request to update an Internal ILM Signing Protocol Configuration")
public class IlmSigningProtocolConfigurationUpdateRequestDto {

    @NotBlank
    @Schema(description = "Name of the ILM Signing Protocol Configuration", requiredMode = Schema.RequiredMode.REQUIRED, example = "ILM-Signing-1")
    private String name;

    @Schema(description = "Description of the ILM Signing Protocol Configuration", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "ILM signing protocol configuration for production")
    private String description;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();
}
