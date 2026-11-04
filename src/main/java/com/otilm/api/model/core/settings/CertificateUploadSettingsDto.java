package com.otilm.api.model.core.settings;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Data
public class CertificateUploadSettingsDto implements Serializable {

    @Schema(description = "Indicator whether the user who uploads a certificate, manually or by file import, becomes "
            + "its owner. An owner set by an upload trigger takes precedence. The uploader gets full owner rights over "
            + "the certificate, such as updating, archiving and deleting it",
            requiredMode = Schema.RequiredMode.REQUIRED, defaultValue = "false")
    private boolean automaticOwnership = false;

}
