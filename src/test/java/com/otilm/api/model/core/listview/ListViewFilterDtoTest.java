package com.otilm.api.model.core.listview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.certificate.SearchFilterRequestDto;
import com.otilm.api.model.core.search.FilterConditionOperator;
import com.otilm.api.model.core.search.FilterFieldSource;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.Validator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListViewFilterDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void isAListingFilterTermSoAClientCanApplyItAsOne() {
        assertInstanceOf(SearchFilterRequestDto.class,
                new ListViewFilterDto(FilterFieldSource.PROPERTY, "commonName", FilterConditionOperator.EQUALS, "a"));
    }

    @Test
    void readsAFilterWrittenBeforeBindingExistedAsUnbound() throws Exception {
        // when
        var dto = mapper
                .readValue("""
                        {"fieldSource":"custom","fieldIdentifier":"team|STRING","condition":"EQUALS","value":"pki"}""",
                        ListViewFilterDto.class);

        // then
        assertEquals(FilterFieldSource.CUSTOM, dto.getFieldSource());
        assertEquals("team|STRING", dto.getFieldIdentifier());
        assertEquals(FilterConditionOperator.EQUALS, dto.getCondition());
        assertEquals("pki", dto.getValue());
        assertNull(dto.getAttributeDefinitionUuids());
        assertNull(dto.getStatus());
        assertNull(dto.getRebind());
    }

    @Test
    void readsTheRebindFlagFromAClient() throws Exception {
        // when
        var dto = mapper
                .readValue(
                        """
                                {"fieldSource":"custom","fieldIdentifier":"team|STRING","condition":"EQUALS","value":"pki","rebind":true}""",
                        ListViewFilterDto.class);

        // then
        assertEquals(Boolean.TRUE, dto.getRebind());
    }

    @Test
    void neverWritesTheRebindFlagBackOut() throws Exception {
        // given
        var dto = new ListViewFilterDto(FilterFieldSource.CUSTOM, "team|STRING", FilterConditionOperator.EQUALS, "pki");
        dto.setRebind(true);

        // when
        var json = mapper.writeValueAsString(dto);

        // then
        assertFalse(json.contains("rebind"), json);
    }

    @Test
    void omitsTheBindingTheStatusAndTheRebindFlagWhenNoneIsSet() throws Exception {
        // when
        var json = mapper
                .writeValueAsString(new ListViewFilterDto(FilterFieldSource.PROPERTY, "commonName",
                        FilterConditionOperator.EMPTY, null));

        // then
        assertFalse(json.contains("attributeDefinitionUuids"));
        assertFalse(json.contains("status"));
        assertFalse(json.contains("rebind"));
    }

    @Test
    void roundTripsTheBindingAndTheStatusOfAnAttributeFilter() throws Exception {
        // given
        var definition = UUID.fromString("0b6c1f3e-2d4a-4b5c-8e9f-1a2b3c4d5e6f");
        var dto = new ListViewFilterDto(FilterFieldSource.META, "owner|STRING", FilterConditionOperator.EQUALS, "x");
        dto.setAttributeDefinitionUuids(List.of(definition));
        dto.setStatus(ListViewFieldStatus.UNAVAILABLE);

        // when
        var json = mapper.writeValueAsString(dto);
        var back = mapper.readValue(json, ListViewFilterDto.class);

        // then
        assertTrue(json.contains("\"status\":\"unavailable\""));
        assertEquals(List.of(definition), back.getAttributeDefinitionUuids());
        assertEquals(ListViewFieldStatus.UNAVAILABLE, back.getStatus());
    }

    @Test
    void comparesTheBindingAsPartOfTheFilter() {
        // given
        var unbound = new ListViewFilterDto(FilterFieldSource.CUSTOM, "team|STRING", FilterConditionOperator.EQUALS,
                "pki");
        var bound = new ListViewFilterDto(FilterFieldSource.CUSTOM, "team|STRING", FilterConditionOperator.EQUALS,
                "pki");
        bound.setAttributeDefinitionUuids(List.of(UUID.randomUUID()));

        // then
        assertNotEquals(unbound, bound);
    }

    @Test
    void keepsTheConstraintsOfTheFilterTermItExtends() {
        // when
        var violations = VALIDATOR.validate(new ListViewFilterDto());

        // then
        assertEquals(3, violations.size());
    }

    @Test
    void tellsAnEmptyBindingApartFromAnAbsentOne() throws Exception {
        // given — bound while the definition was already gone, which never resolves again
        var dto = new ListViewFilterDto(FilterFieldSource.CUSTOM, "team|STRING", FilterConditionOperator.EMPTY, null);
        dto.setAttributeDefinitionUuids(List.of());

        // when
        var json = mapper.writeValueAsString(dto);
        var back = mapper.readValue(json, ListViewFilterDto.class);

        // then
        assertTrue(json.contains("\"attributeDefinitionUuids\":[]"));
        assertEquals(List.of(), back.getAttributeDefinitionUuids());
    }
}
