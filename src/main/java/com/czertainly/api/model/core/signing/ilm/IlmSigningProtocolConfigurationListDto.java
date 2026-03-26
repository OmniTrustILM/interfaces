package com.czertainly.api.model.core.signing.ilm;

import com.czertainly.api.model.core.signing.signingprofile.SimplifiedSigningProfileDto;
import com.czertainly.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "IlmSigningProtocolConfigurationListDto", description = "ILM Signing Protocol configuration details for listing")
@ToString(callSuper = true)
public class IlmSigningProtocolConfigurationListDto extends NameAndUuidDto {

    @Schema(description = "Description of the ILM Signing Protocol Configuration", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "ILM signing protocol configuration for production")
    private String description;

    @Schema(description = "Default Signing Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "6b55de1c-844f-11ec-a8a3-0242ac120002")
    private SimplifiedSigningProfileDto signingProfile;
}
