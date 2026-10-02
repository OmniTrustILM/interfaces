package com.otilm.api.model.core.listview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.search.FilterFieldSource;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.Validator;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListViewColumnDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void omitsTheLabelWhenTheColumnFollowsTheCatalogue() throws Exception {
        // given — no override, so a field that is relabelled later follows along
        var dto = new ListViewColumnDto(FilterFieldSource.PROPERTY, "commonName", null);

        // when
        var json = mapper.writeValueAsString(dto);

        // then
        assertFalse(json.contains("label"));
        assertNull(mapper.readValue(json, ListViewColumnDto.class).getLabel());
    }

    @Test
    void roundTripsAPinnedHeading() throws Exception {
        // given — the operator renamed the heading for this view only
        var dto = new ListViewColumnDto(FilterFieldSource.CUSTOM, "department", "Owning team");

        // when
        var back = mapper.readValue(mapper.writeValueAsString(dto), ListViewColumnDto.class);

        // then
        assertEquals("Owning team", back.getLabel());
        assertEquals(FilterFieldSource.CUSTOM, back.getFieldSource());
        assertEquals("department", back.getFieldIdentifier());
    }

    @Test
    void acceptsAHeadingAtTheStoredColumnLength() {
        assertTrue(VALIDATOR
                .validate(new ListViewColumnDto(FilterFieldSource.CUSTOM, "department", "x".repeat(255)))
                .isEmpty());
    }

    @Test
    void rejectsAHeadingLongerThanTheStoredColumn() {
        // given
        var violations = VALIDATOR
                .validate(new ListViewColumnDto(FilterFieldSource.CUSTOM, "department", "x".repeat(256)));

        // then
        assertEquals(1, violations.size());
        assertEquals("label", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void acceptsAnEmptyHeading() {
        // given — a column carrying only an icon is headed by nothing, which is a choice and not an absent override
        var dto = new ListViewColumnDto(FilterFieldSource.CUSTOM, "department", "");

        // when
        var violations = VALIDATOR.validate(dto);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void roundTripsAnEmptyHeading() throws Exception {
        // given
        var dto = new ListViewColumnDto(FilterFieldSource.CUSTOM, "department", "");

        // when
        var json = mapper.writeValueAsString(dto);

        // then
        assertTrue(json.contains("\"label\":\"\""));
        assertEquals("", mapper.readValue(json, ListViewColumnDto.class).getLabel());
    }

    @Test
    void preservesAWhitespaceOnlyHeading() throws Exception {
        // given
        var dto = new ListViewColumnDto(FilterFieldSource.CUSTOM, "department", "   ");

        // when
        var violations = VALIDATOR.validate(dto);
        var back = mapper.readValue(mapper.writeValueAsString(dto), ListViewColumnDto.class);

        // then
        assertTrue(violations.isEmpty());
        assertEquals("   ", back.getLabel());
    }

    @Test
    void readsAnEmptyLabelFromAClientAsAPinnedBlankHeading() throws Exception {
        // when
        var dto = mapper.readValue("""
                {"fieldSource":"custom","fieldIdentifier":"department","label":""}""", ListViewColumnDto.class);

        // then
        assertEquals("", dto.getLabel());
    }

    @Test
    void readsAnOmittedLabelFromAClientAsFollowingTheCatalogue() throws Exception {
        // when
        var dto = mapper.readValue("""
                {"fieldSource":"custom","fieldIdentifier":"department"}""", ListViewColumnDto.class);

        // then
        assertNull(dto.getLabel());
    }

    @Test
    void readsAnExplicitNullLabelFromAClientAsFollowingTheCatalogue() throws Exception {
        // when
        var dto = mapper.readValue("""
                {"fieldSource":"custom","fieldIdentifier":"department","label":null}""", ListViewColumnDto.class);

        // then
        assertNull(dto.getLabel());
    }

    @Test
    void keepsTheHeadingOutOfColumnIdentity() {
        // given — a view stores identifiers; two entries for the same field are the same column however it is headed
        var plain = new ListViewColumnDto(FilterFieldSource.CUSTOM, "department", null);
        var renamed = new ListViewColumnDto(FilterFieldSource.CUSTOM, "department", "Owning team");

        // then — equality intentionally includes the label, so an edit that only renames is still a change to persist
        assertFalse(plain.equals(renamed));
        assertEquals(plain.getFieldIdentifier(), renamed.getFieldIdentifier());
        assertEquals(plain.getFieldSource(), renamed.getFieldSource());
    }

    @Test
    void omitsTheBindingTheStatusAndTheRebindFlagWhenNoneIsSet() throws Exception {
        // given — a property column binds to no attribute definition and a request carries no status
        var dto = new ListViewColumnDto(FilterFieldSource.PROPERTY, "commonName", null);

        // when
        var json = mapper.writeValueAsString(dto);

        // then
        assertFalse(json.contains("attributeDefinitionUuids"));
        assertFalse(json.contains("status"));
        assertFalse(json.contains("rebind"));
    }

    @Test
    void roundTripsTheBindingAndTheStatusOfAnAttributeColumn() throws Exception {
        // given
        var definition = UUID.fromString("6f1d2c1e-6c3a-4c5e-9f0a-2b7d8e9f0a1b");
        var dto = new ListViewColumnDto(FilterFieldSource.CUSTOM, "department|STRING", null);
        dto.setAttributeDefinitionUuids(List.of(definition));
        dto.setStatus(ListViewFieldStatus.REPLACED);

        // when
        var json = mapper.writeValueAsString(dto);
        var back = mapper.readValue(json, ListViewColumnDto.class);

        // then
        assertTrue(json.contains("\"status\":\"replaced\""));
        assertEquals(List.of(definition), back.getAttributeDefinitionUuids());
        assertEquals(ListViewFieldStatus.REPLACED, back.getStatus());
    }

    @Test
    void readsTheRebindFlagFromAClient() throws Exception {
        // when
        var dto = mapper
                .readValue("""
                        {"fieldSource":"custom","fieldIdentifier":"department|STRING","rebind":true}""",
                        ListViewColumnDto.class);

        // then
        assertEquals(Boolean.TRUE, dto.getRebind());
    }

    @Test
    void neverWritesTheRebindFlagBackOut() throws Exception {
        // given
        var dto = new ListViewColumnDto(FilterFieldSource.CUSTOM, "department|STRING", null);
        dto.setRebind(true);

        // when
        var json = mapper.writeValueAsString(dto);

        // then
        assertFalse(json.contains("rebind"), json);
    }

    @Test
    void readsAColumnWrittenBeforeBindingExistedAsUnbound() throws Exception {
        // when
        var dto = mapper.readValue("""
                {"fieldSource":"custom","fieldIdentifier":"department|STRING"}""", ListViewColumnDto.class);

        // then
        assertNull(dto.getAttributeDefinitionUuids());
        assertNull(dto.getStatus());
        assertNull(dto.getRebind());
    }

    @Test
    void tellsAnEmptyBindingApartFromAnAbsentOne() throws Exception {
        // given — bound while the definition was already gone, which never resolves again
        var dto = new ListViewColumnDto(FilterFieldSource.CUSTOM, "department|STRING", null);
        dto.setAttributeDefinitionUuids(List.of());

        // when
        var json = mapper.writeValueAsString(dto);
        var back = mapper.readValue(json, ListViewColumnDto.class);

        // then
        assertTrue(json.contains("\"attributeDefinitionUuids\":[]"));
        assertEquals(List.of(), back.getAttributeDefinitionUuids());
    }
}
