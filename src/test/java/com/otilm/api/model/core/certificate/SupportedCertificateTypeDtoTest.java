package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SupportedCertificateTypeDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void supportedTypeIsWrittenAndReadByItsCode() throws Exception {
        // given
        SupportedCertificateTypeDto supported = SupportedCertificateTypeDto.of(CertificateType.SSH);

        // when
        String json = mapper.writeValueAsString(supported);
        SupportedCertificateTypeDto back = mapper.readValue(json, SupportedCertificateTypeDto.class);

        // then
        assertEquals("{\"certificateType\":\"" + CertificateType.SSH.getCode() + "\"}", json);
        assertEquals(supported, back);
    }
}
