package com.czertainly.api.model.client.signing.protocols.tsp;

import com.czertainly.api.model.client.attribute.ResponseAttribute;
import com.czertainly.api.model.client.signing.profile.SimplifiedSigningProfileDto;
import com.czertainly.api.model.common.NameAndUuidDto;
import com.czertainly.api.model.core.signing.TspAuthenticationMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TspProfileDto", description = "TSP (Timestamping Protocol) profile details")
@ToString(callSuper = true)
public class TspProfileDto extends NameAndUuidDto {

    @Schema(description = "Description of the TSP Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "TSP profile for production timestamping")
    private String description;

    @Schema(description = "Enabled flag of the TSP Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private boolean enabled;

    @Schema(description = "Default Signing Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SimplifiedSigningProfileDto defaultSigningProfile;

    @Schema(
            description = "TSP URL for signing",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "https://ilm.otilm.com/api/v1/protocols/tsp/tsp-profile-1/sign"
    )
    private String signingUrl;

    @Schema(description = "Authentication methods this TSP Profile accepts", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<TspAuthenticationMethod> allowedAuthenticationMethods = new ArrayList<>();

    @Schema(description = "Read-only Basic credentials configured on this profile in case BASIC_PASSWORD authentication method " +
            "is configured. Secret is never returned to the client.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<TspBasicCredentialDto> basicCredentials = new ArrayList<>();

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes = new ArrayList<>();
}
