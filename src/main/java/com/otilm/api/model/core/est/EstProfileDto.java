package com.otilm.api.model.core.est;

import com.otilm.api.model.client.raprofile.SimplifiedRaProfileDto;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EstProfileDto extends NameAndUuidDto {

    @Schema(description = "Enabled flag - true = enabled; false = disabled",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;

    @Schema(description = "EST Profile description", examples = {"Devices enrolling with vendor certificates"})
    private String description;

    @Schema(description = "Whether the EST Profile serves requests sent without a label",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean systemDefault;

    @Schema(description = "How clients authenticate to enroll or to receive a server-generated key",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private EstAuthenticationPolicy authenticationPolicy;

    @Schema(description = "RA Profile that serves requests sent to the EST Profile's own label")
    private SimplifiedRaProfileDto raProfile;

    @Schema(description = "Whether /serverkeygen is allowed. Without keyRecovery the generated key is returned "
            + "and not kept", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean serverKeyGeneration;

    @Schema(description = "Whether server-generated keys are created on the key recovery token profile and kept "
            + "there for recovery", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean keyRecovery;

    @Schema(description = "Label EST clients use to reach the EST Profile", examples = {"est-devices"})
    private String label;

    @Schema(description = "EST URL of the EST Profile. Empty until the EST Profile has an RA Profile",
            examples = {"https://your-domain.com/.well-known/est/est-devices"})
    private String estUrl;
}
