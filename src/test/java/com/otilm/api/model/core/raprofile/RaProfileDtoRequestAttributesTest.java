package com.otilm.api.model.core.raprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RaProfileDtoRequestAttributesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsCertificateRequestAttributes() throws Exception {
        // given
        var requestAttributes = new RaProfileCertificateRequestAttributesDto();
        requestAttributes.setMergeMode(AttributeSetMergeMode.MERGE);
        var dto = new RaProfileDto();
        dto.setCertificateRequestAttributes(requestAttributes);

        // when
        var json = mapper.writeValueAsString(dto);
        RaProfileDto back = mapper.readValue(json, RaProfileDto.class);

        // then
        assertNotNull(back.getCertificateRequestAttributes());
        assertEquals(AttributeSetMergeMode.MERGE, back.getCertificateRequestAttributes().getMergeMode());
    }
}
