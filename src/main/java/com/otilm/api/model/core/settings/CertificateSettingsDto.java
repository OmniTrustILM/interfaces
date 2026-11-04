package com.otilm.api.model.core.settings;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

@Data
public class CertificateSettingsDto implements Serializable {

    private CertificateValidationSettingsDto validation;

    private CertificateRequestAttributesSettingsDto requestAttributes;

    private CertificateRegistrationSettingsDto registration;

    @Schema(description = "Certificate upload settings, which govern ownership of certificates added by manual upload "
            + "and by file import")
    private CertificateUploadSettingsDto upload;

}
