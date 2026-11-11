package com.otilm.api.model.core.certificate;

import com.otilm.api.model.common.NameAndUuidDto;
import java.io.Serializable;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class CertificateDtoLogDataTest {

    @Test
    void namesTheCertificateBySubjectDn_whenItHasOne() {
        // given
        CertificateDto certificate = new CertificateDto();
        certificate.setUuid(UUID.randomUUID().toString());
        certificate.setSubjectDn("CN=holder");
        certificate.setDisplayName("holder");

        // when
        Serializable logData = certificate.toLogData();

        // then
        assertEquals("CN=holder", assertInstanceOf(NameAndUuidDto.class, logData).getName());
    }

    @Test
    void namesTheCertificateByDisplayName_whenItsTypeHasNoSubjectDn() {
        // given
        CertificateDto certificate = new CertificateDto();
        certificate.setUuid(UUID.randomUUID().toString());
        certificate.setDisplayName("ssh-key-id");

        // when
        Serializable logData = certificate.toLogData();

        // then
        assertEquals("ssh-key-id", assertInstanceOf(NameAndUuidDto.class, logData).getName());
    }
}
