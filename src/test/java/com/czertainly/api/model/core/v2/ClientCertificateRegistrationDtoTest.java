package com.czertainly.api.model.core.v2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientCertificateRegistrationDtoTest {

    @Test
    void bothEmpty_failsRfc5280Constraint() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        assertFalse(dto.isSubjectIdentificationProvided(),
                "An empty subject AND empty SAN must fail RFC 5280 §4.1.2.6");
    }

    @Test
    void subjectDnOnly_passes() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setSubjectDn("CN=device-7,O=Acme");
        assertTrue(dto.isSubjectIdentificationProvided());
    }

    @Test
    void subjectAltNameOnly_passes_perRfc5280() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setSubjectAltName("DNS:device-7.example.com");
        assertTrue(dto.isSubjectIdentificationProvided(),
                "SAN-only registration is valid per RFC 5280 §4.1.2.6 (subjectAltName must be marked critical at issuance)");
    }

    @Test
    void bothPresent_passes() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setSubjectDn("CN=device-7");
        dto.setSubjectAltName("DNS:device-7.example.com");
        assertTrue(dto.isSubjectIdentificationProvided());
    }

    @Test
    void whitespaceOnlyValues_treatedAsEmpty() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setSubjectDn("   ");
        dto.setSubjectAltName("\t\n  ");
        assertFalse(dto.isSubjectIdentificationProvided(),
                "Whitespace-only strings must not satisfy the subject-identification constraint");
    }
}
