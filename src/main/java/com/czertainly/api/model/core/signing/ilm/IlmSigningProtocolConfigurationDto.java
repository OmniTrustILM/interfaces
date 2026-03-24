package com.czertainly.api.model.core.signing.ilm;

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
@Schema(name = "IlmSigningProtocolConfigurationDto", description = "ILM Signing Protocol configuration details")
@ToString(callSuper = true)
public class IlmSigningProtocolConfigurationDto extends NameAndUuidDto {

    @Schema(description = "Description of the ILM Signing Protocol Configuration", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "ILM signing protocol configuration for production")
    private String description;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes = new ArrayList<>();
}
