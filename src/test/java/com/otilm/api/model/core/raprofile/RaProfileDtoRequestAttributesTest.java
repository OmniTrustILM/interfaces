package com.otilm.api.model.core.raprofile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RaProfileDtoRequestAttributesTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void carriesCertificateRequestAttributes() throws Exception {
        RaProfileCertificateRequestAttributesDto ra = new RaProfileCertificateRequestAttributesDto();
        ra.setMergeMode(AttributeSetMergeMode.MERGE);

        RaProfileDto dto = new RaProfileDto();
        dto.setCertificateRequestAttributes(ra);

        String json = mapper.writeValueAsString(dto);
        RaProfileDto back = mapper.readValue(json, RaProfileDto.class);

        assertNotNull(back.getCertificateRequestAttributes());
        assertEquals(AttributeSetMergeMode.MERGE, back.getCertificateRequestAttributes().getMergeMode());
    }
}
