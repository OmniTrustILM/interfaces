package com.otilm.api.model.core.raprofile;

import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.otilm.util.builders.DataAttributeV3Builder.aDataAttribute;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class RaProfileCertificateRequestAttributesUpdateDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsDefinitionsAndMergeMode() throws Exception {
        // given
        var firstAttributeName = "server_fqdn";
        var secondAttributeName = "organization";
        var dto = new RaProfileCertificateRequestAttributesUpdateDto();
        dto.setMergeMode(AttributeSetMergeMode.STATIC_ONLY);
        dto.setRequestAttributes(List.of(
                aDataAttribute().withUuid("u1").withName(firstAttributeName).build(),
                aDataAttribute().withUuid("u2").withName(secondAttributeName).build()));

        // when
        var json = mapper.writeValueAsString(dto);
        RaProfileCertificateRequestAttributesUpdateDto back =
                mapper.readValue(json, RaProfileCertificateRequestAttributesUpdateDto.class);

        // then
        assertEquals(AttributeSetMergeMode.STATIC_ONLY, back.getMergeMode());
        assertEquals(2, back.getRequestAttributes().size());
        assertInstanceOf(DataAttributeV3.class, back.getRequestAttributes().get(0));
        assertEquals(firstAttributeName, back.getRequestAttributes().get(0).getName());
        assertEquals(secondAttributeName, back.getRequestAttributes().get(1).getName());
    }

    @Test
    void omitsMergeMode_whenNull() throws Exception {
        // given — a DTO whose mergeMode was never set
        var dto = new RaProfileCertificateRequestAttributesUpdateDto();
        dto.setRequestAttributes(List.of());

        // when
        var json = mapper.writeValueAsString(dto);

        // then
        assertFalse(json.contains("mergeMode"));
    }
}
