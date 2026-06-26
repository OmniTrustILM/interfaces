package com.otilm.api.model.core.raprofile;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RaProfileCertificateRequestAttributesDtoTest {

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
    void roundTripsReadView() throws Exception {
        RaProfileCertificateRequestAttributesDto dto = new RaProfileCertificateRequestAttributesDto();
        dto.setMergeMode(AttributeSetMergeMode.MERGE);
        dto.setRequestAttributes(List.of(def("u1", "server_fqdn")));

        String json = mapper.writeValueAsString(dto);
        RaProfileCertificateRequestAttributesDto back =
                mapper.readValue(json, RaProfileCertificateRequestAttributesDto.class);

        assertEquals(AttributeSetMergeMode.MERGE, back.getMergeMode());
        assertEquals(1, back.getRequestAttributes().size());
        assertEquals("server_fqdn", back.getRequestAttributes().get(0).getName());
    }
}
