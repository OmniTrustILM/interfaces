package com.otilm.api.model.core.settings;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Data
public class CertificateUploadSettingsUpdateDto implements Serializable {

    @Schema(description = "Indicator whether the user who uploads a certificate, manually or by file import, becomes "
            + "its owner. An owner set by an upload trigger takes precedence",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "false")
    private boolean automaticOwnership = false;

}
