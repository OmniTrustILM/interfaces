package com.otilm.api.model.core.raprofile;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class RaProfileCertificateRequestAttributesUpdateDtoTest {

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
    void roundTripsDefinitionsAndMergeMode() throws Exception {
        RaProfileCertificateRequestAttributesUpdateDto dto = new RaProfileCertificateRequestAttributesUpdateDto();
        dto.setMergeMode(AttributeSetMergeMode.STATIC_ONLY);
        dto.setRequestAttributes(List.of(def("u1", "server_fqdn"), def("u2", "organization")));

        String json = mapper.writeValueAsString(dto);
        RaProfileCertificateRequestAttributesUpdateDto back =
                mapper.readValue(json, RaProfileCertificateRequestAttributesUpdateDto.class);

        assertEquals(AttributeSetMergeMode.STATIC_ONLY, back.getMergeMode());
        assertEquals(2, back.getRequestAttributes().size());
        assertInstanceOf(DataAttributeV3.class, back.getRequestAttributes().get(0));
        assertEquals("server_fqdn", back.getRequestAttributes().get(0).getName());
        assertEquals("organization", back.getRequestAttributes().get(1).getName());
    }

    @Test
    void mergeModeIsOmittedWhenNull() throws Exception {
        RaProfileCertificateRequestAttributesUpdateDto dto = new RaProfileCertificateRequestAttributesUpdateDto();
        dto.setRequestAttributes(List.of());
        String json = mapper.writeValueAsString(dto);
        assertFalse(json.contains("mergeMode"));
    }
}
