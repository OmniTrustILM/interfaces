package com.otilm.api.model.core.settings;

import lombok.Data;

import java.io.Serializable;

@Data
public class CertificateSettingsDto implements Serializable {


    private CertificateValidationSettingsDto validation;

}
