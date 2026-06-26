package com.otilm.api.model.core.raprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.otilm.util.builders.DataAttributeV3Builder.aDataAttribute;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RaProfileCertificateRequestAttributesDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsMergeModeAndRequestAttributes() throws Exception {
        // given
        var attributeName = "server_fqdn";
        var dto = new RaProfileCertificateRequestAttributesDto();
        dto.setMergeMode(AttributeSetMergeMode.MERGE);
        dto.setRequestAttributes(List.of(aDataAttribute().withUuid("u1").withName(attributeName).build()));

        // when
        var json = mapper.writeValueAsString(dto);
        RaProfileCertificateRequestAttributesDto back =
                mapper.readValue(json, RaProfileCertificateRequestAttributesDto.class);

        // then
        assertEquals(AttributeSetMergeMode.MERGE, back.getMergeMode());
        assertEquals(1, back.getRequestAttributes().size());
        assertEquals(attributeName, back.getRequestAttributes().get(0).getName());
    }
}
