package com.otilm.api.model.core.raprofile;

import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.mapping.ValueSourceType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.otilm.util.builders.DataAttributeV3Builder.aDataAttribute;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void roundTripsValueSourceBindingsAndStrictnessFlag() throws Exception {
        // given — a binding and the strictness flag, the two fields the merge-mode test leaves untouched
        var boundName = "server";
        var binding = new ValueSourceBindingDto();
        binding.setAttributeName(boundName);
        binding.setValueSourceType(ValueSourceType.CONNECTOR_CALLBACK);
        var dto = new RaProfileCertificateRequestAttributesUpdateDto();
        dto.setValueSourceBindings(List.of(binding));
        dto.setExternalCsrValidationStrict(true);

        // when
        var json = mapper.writeValueAsString(dto);
        RaProfileCertificateRequestAttributesUpdateDto back =
                mapper.readValue(json, RaProfileCertificateRequestAttributesUpdateDto.class);

        // since it is hidden, it should not return it
        assertEquals(0, back.getValueSourceBindings().size());
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

    @Test
    void acceptsDistinctBindingTargets() {
        // given — two bindings targeting different attributes
        var dto = new RaProfileCertificateRequestAttributesUpdateDto();
        dto.setValueSourceBindings(List.of(
                bindingByUuid("u1"),
                bindingByName("organization")));

        // when / then
        assertTrue(dto.isValueSourceBindingsUnique());
    }

    @Test
    void rejectsDuplicateBindingTargetByUuid() {
        // given — two bindings targeting the same attribute UUID
        var dto = new RaProfileCertificateRequestAttributesUpdateDto();
        dto.setValueSourceBindings(List.of(
                bindingByUuid("u1"),
                bindingByUuid("u1")));

        // when / then
        assertFalse(dto.isValueSourceBindingsUnique());
    }

    @Test
    void rejectsDuplicateBindingTargetByName() {
        // given — two bindings targeting the same attribute name
        var dto = new RaProfileCertificateRequestAttributesUpdateDto();
        dto.setValueSourceBindings(List.of(
                bindingByName("server"),
                bindingByName("server")));

        // when / then
        assertFalse(dto.isValueSourceBindingsUnique());
    }

    private static ValueSourceBindingDto bindingByUuid(String uuid) {
        var binding = new ValueSourceBindingDto();
        binding.setAttributeUuid(uuid);
        binding.setValueSourceType(ValueSourceType.STATIC_LIST);
        return binding;
    }

    private static ValueSourceBindingDto bindingByName(String name) {
        var binding = new ValueSourceBindingDto();
        binding.setAttributeName(name);
        binding.setValueSourceType(ValueSourceType.CONNECTOR_CALLBACK);
        return binding;
    }
}
