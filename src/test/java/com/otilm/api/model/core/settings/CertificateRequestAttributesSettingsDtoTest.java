package com.otilm.api.model.core.settings;

import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.otilm.util.builders.DataAttributeV3Builder.aDataAttribute;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class CertificateRequestAttributesSettingsDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsReadView() throws Exception {
        // given
        var attributeName = "common_name";
        var dto = new CertificateRequestAttributesSettingsDto();
        dto.setRequestAttributes(List.of(aDataAttribute().withUuid("u1").withName(attributeName).build()));

        // when
        var json = mapper.writeValueAsString(dto);
        CertificateRequestAttributesSettingsDto back =
                mapper.readValue(json, CertificateRequestAttributesSettingsDto.class);

        // then
        assertEquals(1, back.getRequestAttributes().size());
        assertInstanceOf(DataAttributeV3.class, back.getRequestAttributes().get(0));
        assertEquals(attributeName, back.getRequestAttributes().get(0).getName());
    }

    @Test
    void roundTripsUpdateView() throws Exception {
        // given
        var secondAttributeName = "country";
        var dto = new CertificateRequestAttributesSettingsUpdateDto();
        dto.setRequestAttributes(List.of(
                aDataAttribute().withUuid("u1").withName("common_name").build(),
                aDataAttribute().withUuid("u2").withName(secondAttributeName).build()));

        // when
        var json = mapper.writeValueAsString(dto);
        CertificateRequestAttributesSettingsUpdateDto back =
                mapper.readValue(json, CertificateRequestAttributesSettingsUpdateDto.class);

        // then
        assertEquals(2, back.getRequestAttributes().size());
        assertEquals(secondAttributeName, back.getRequestAttributes().get(1).getName());
    }

    @Test
    void foldsIntoCertificateSettings() throws Exception {
        // given
        var requestAttributes = new CertificateRequestAttributesSettingsDto();
        requestAttributes.setRequestAttributes(List.of(aDataAttribute().withUuid("u1").withName("common_name").build()));
        var settings = new CertificateSettingsDto();
        settings.setRequestAttributes(requestAttributes);

        // when
        var json = mapper.writeValueAsString(settings);
        CertificateSettingsDto back = mapper.readValue(json, CertificateSettingsDto.class);

        // then
        assertEquals(1, back.getRequestAttributes().getRequestAttributes().size());
    }
}
