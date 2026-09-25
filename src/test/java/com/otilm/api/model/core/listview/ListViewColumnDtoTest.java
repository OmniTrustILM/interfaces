package com.otilm.api.model.core.listview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.search.FilterFieldSource;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.Validator;
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
}
