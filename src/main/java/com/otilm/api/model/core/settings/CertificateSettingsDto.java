package com.otilm.api.model.core.settings;

import java.io.Serializable;
import lombok.Data;

@Data
public class CertificateSettingsDto implements Serializable {

    private CertificateValidationSettingsDto validation;

    private CertificateRequestAttributesSettingsDto requestAttributes;

    private CertificateRegistrationSettingsDto registration;

    private CertificateUploadSettingsDto upload;

}
