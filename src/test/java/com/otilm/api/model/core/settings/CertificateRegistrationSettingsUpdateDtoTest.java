package com.otilm.api.model.core.settings;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateRegistrationSettingsUpdateDtoTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void defaultsAreValid() {
        assertTrue(VALIDATOR.validate(new CertificateRegistrationSettingsUpdateDto()).isEmpty());
    }

    @Test
    void rejectsNonPositiveWindowAndAttempts() {
        CertificateRegistrationSettingsUpdateDto dto = new CertificateRegistrationSettingsUpdateDto();
        dto.setDefaultIssuanceWindowDays(-1);
        dto.setMaxFailedAttempts(0);
        assertFalse(VALIDATOR.validate(dto).isEmpty(), "@Positive must reject <= 0");
    }

    @Test
    void cascadesFromCertificateSettingsUpdate() {
        CertificateSettingsUpdateDto parent = new CertificateSettingsUpdateDto();
        CertificateRegistrationSettingsUpdateDto reg = new CertificateRegistrationSettingsUpdateDto();
        reg.setMaxFailedAttempts(0);
        parent.setRegistration(reg);
        assertFalse(VALIDATOR.validate(parent).isEmpty(), "@Valid must cascade into the registration sub-DTO");
    }
}
