package com.otilm.api.model.core.settings;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.io.Serializable;
import lombok.Data;

@Data
public class CertificateSettingsUpdateDto implements Serializable {

    @Valid
    @Schema(description = "Settings of validation of certificates")
    private CertificateValidationSettingsUpdateDto validation;

    @Valid
    @Schema(description = "Platform default request-attribute set")
    private CertificateRequestAttributesSettingsUpdateDto requestAttributes;

    @Valid
    @Schema(description = "Certificate pre-registration settings")
    private CertificateRegistrationSettingsUpdateDto registration;

    @Valid
    @Schema(description = "Certificate upload settings")
    private CertificateUploadSettingsUpdateDto upload;
}
