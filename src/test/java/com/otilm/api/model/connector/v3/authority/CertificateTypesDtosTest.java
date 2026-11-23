package com.otilm.api.model.connector.v3.authority;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.certificate.CertificateType;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CertificateTypesDtosTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void requestForAnAuthorityRoundTripsWithoutRaProfileAttributes() throws Exception {
        // given
        CertificateTypesRequestDtoV3 request = new CertificateTypesRequestDtoV3();
        request.setAuthorityAttributes(List.of());

        // when
        CertificateTypesRequestDtoV3 back = mapper
                .readValue(mapper.writeValueAsString(request), CertificateTypesRequestDtoV3.class);

        // then
        assertEquals(0, back.getAuthorityAttributes().size());
        assertNull(back.getRaProfileAttributes());
    }

    @Test
    void requestForAnRaProfileRoundTripsWithItsAttributes() throws Exception {
        // given
        CertificateTypesRequestDtoV3 request = new CertificateTypesRequestDtoV3();
        request.setAuthorityAttributes(List.of());
        request.setRaProfileAttributes(List.of());

        // when
        CertificateTypesRequestDtoV3 back = mapper
                .readValue(mapper.writeValueAsString(request), CertificateTypesRequestDtoV3.class);

        // then
        assertEquals(0, back.getRaProfileAttributes().size());
    }

    @Test
    void supportedTypeIsWrittenAndReadByItsCode() throws Exception {
        // given
        SupportedCertificateTypeDtoV3 supported = SupportedCertificateTypeDtoV3.of(CertificateType.SSH);

        // when
        String json = mapper.writeValueAsString(supported);
        SupportedCertificateTypeDtoV3 back = mapper.readValue(json, SupportedCertificateTypeDtoV3.class);

        // then
        assertEquals("{\"certificateType\":\"" + CertificateType.SSH.getCode() + "\"}", json);
        assertEquals(supported, back);
    }
}
