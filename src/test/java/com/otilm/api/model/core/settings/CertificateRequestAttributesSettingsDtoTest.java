package com.otilm.api.model.core.settings;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class CertificateRequestAttributesSettingsDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private static DataAttributeV3 def(String uuid, String name) {
        DataAttributeV3 a = new DataAttributeV3();
        a.setUuid(uuid);
        a.setName(name);
        a.setContentType(AttributeContentType.STRING);
        DataAttributeProperties p = new DataAttributeProperties();
        p.setLabel(name);
        a.setProperties(p);
        return a;
    }

    @Test
    void readViewRoundTrips() throws Exception {
        CertificateRequestAttributesSettingsDto dto = new CertificateRequestAttributesSettingsDto();
        dto.setRequestAttributes(List.of(def("u1", "common_name")));

        String json = mapper.writeValueAsString(dto);
        CertificateRequestAttributesSettingsDto back =
                mapper.readValue(json, CertificateRequestAttributesSettingsDto.class);

        assertEquals(1, back.getRequestAttributes().size());
        assertInstanceOf(DataAttributeV3.class, back.getRequestAttributes().get(0));
        assertEquals("common_name", back.getRequestAttributes().get(0).getName());
    }

    @Test
    void updateViewRoundTrips() throws Exception {
        CertificateRequestAttributesSettingsUpdateDto dto = new CertificateRequestAttributesSettingsUpdateDto();
        dto.setRequestAttributes(List.of(def("u1", "common_name"), def("u2", "country")));

        String json = mapper.writeValueAsString(dto);
        CertificateRequestAttributesSettingsUpdateDto back =
                mapper.readValue(json, CertificateRequestAttributesSettingsUpdateDto.class);

        assertEquals(2, back.getRequestAttributes().size());
        assertEquals("country", back.getRequestAttributes().get(1).getName());
    }

    @Test
    void foldsIntoCertificateSettings() throws Exception {
        CertificateRequestAttributesSettingsDto ra = new CertificateRequestAttributesSettingsDto();
        ra.setRequestAttributes(List.of(def("u1", "common_name")));
        CertificateSettingsDto settings = new CertificateSettingsDto();
        settings.setRequestAttributes(ra);

        String json = mapper.writeValueAsString(settings);
        CertificateSettingsDto back = mapper.readValue(json, CertificateSettingsDto.class);

        assertEquals(1, back.getRequestAttributes().getRequestAttributes().size());
    }
}
