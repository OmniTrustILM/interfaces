package com.otilm.api.model.core.settings;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.io.Serializable;

@Data
public class CertificateSettingsUpdateDto implements Serializable {

    @Valid
    @Schema(description = "Settings of validation of certificates")
    private CertificateValidationSettingsUpdateDto validation;
}
