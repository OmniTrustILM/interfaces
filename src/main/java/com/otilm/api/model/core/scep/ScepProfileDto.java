package com.otilm.api.model.core.scep;

import com.otilm.api.model.client.raprofile.SimplifiedRaProfileDto;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ScepProfileDto extends NameAndUuidDto {
    @Schema(description = "Enabled flag - true = enabled; false = disabled",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;
    @Schema(description = "SCEP Profile description", examples = {"Sample description"})
    private String description;
    @Schema(description = "RA Profile")
    private SimplifiedRaProfileDto raProfile;
    @Schema(description = "Include CA certificate in the SCEP response", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean includeCaCertificate;
    @Schema(description = "Include CA certificate chain in the SCEP response", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean includeCaCertificateChain;
    @Schema(description = "Renewal time threshold in days", example = "30")
    private Integer renewThreshold;
    @Schema(description = "SCEP URL", examples = {"https://some-server.com/api/v1/protocols/scep/profile/pkiclient.exe"})
    private String scepUrl;

    @Schema(description = "Status of Intune")
    private boolean enableIntune;

    @Schema(description = "Whether a challenge password is currently set for the SCEP Profile. The password value "
            + "itself is write-only and never returned; this flag lets clients reflect the current state.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enableChallengePassword;
}
