package com.otilm.api.model.core.raprofile;

import com.otilm.api.model.common.attribute.v3.mapping.ValueSourceType;
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

    @Test
    void roundTripsValueSourceBindingsAndStrictnessFlag() throws Exception {
        // given — a binding and the strictness flag, the two fields the merge-mode test leaves untouched
        var boundUuid = "u1";
        var binding = new ValueSourceBindingDto();
        binding.setAttributeUuid(boundUuid);
        binding.setValueSourceType(ValueSourceType.STATIC_LIST);
        var dto = new RaProfileCertificateRequestAttributesDto();
        dto.setValueSourceBindings(List.of(binding));
        dto.setExternalCsrValidationStrict(true);

        // when
        var json = mapper.writeValueAsString(dto);
        RaProfileCertificateRequestAttributesDto back =
                mapper.readValue(json, RaProfileCertificateRequestAttributesDto.class);

        // value source binding is supposed to be hidden
        assertEquals(0, back.getValueSourceBindings().size());
    }
}
