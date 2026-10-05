package com.otilm.api.model.core.listview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.certificate.SearchSortRequestDto;
import com.otilm.api.model.core.search.FilterFieldSource;
import com.otilm.api.model.core.search.SortDirection;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

class ListViewSortRequestDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void isAListingOrderingSoAClientCanSendTheOneItApplies() {
        assertInstanceOf(SearchSortRequestDto.class,
                new ListViewSortRequestDto(FilterFieldSource.CUSTOM, "team|STRING", SortDirection.ASC));
    }

    @Test
    void readsAnOrderingWithoutTheFlagAsCarried() throws Exception {
        // when
        var dto = mapper
                .readValue("""
                        {"fieldSource":"custom","fieldIdentifier":"team|STRING","direction":"asc"}""",
                        ListViewSortRequestDto.class);

        // then
        assertEquals(FilterFieldSource.CUSTOM, dto.getFieldSource());
        assertEquals("team|STRING", dto.getFieldIdentifier());
        assertEquals(SortDirection.ASC, dto.getDirection());
        assertNull(dto.getRebind());
    }

    @Test
    void readsTheRebindFlagFromAClient() throws Exception {
        // when
        var dto = mapper
                .readValue("""
                        {"fieldSource":"custom","fieldIdentifier":"team|STRING","direction":"asc","rebind":true}""",
                        ListViewSortRequestDto.class);

        // then
        assertEquals(Boolean.TRUE, dto.getRebind());
    }

    @Test
    void neverWritesTheRebindFlagBackOut() throws Exception {
        // given
        var dto = new ListViewSortRequestDto(FilterFieldSource.CUSTOM, "team|STRING", SortDirection.ASC);
        dto.setRebind(true);

        // when
        var json = mapper.writeValueAsString(dto);

        // then
        assertFalse(json.contains("rebind"), json);
    }

    @Test
    void keepsTheConstraintsOfTheOrderingItExtends() {
        // when
        var violations = VALIDATOR.validate(new ListViewSortRequestDto());

        // then
        assertEquals(3, violations.size());
    }
}
