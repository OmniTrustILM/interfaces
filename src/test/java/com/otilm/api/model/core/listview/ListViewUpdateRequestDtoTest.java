package com.otilm.api.model.core.listview;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.search.FilterConditionOperator;
import com.otilm.api.model.core.search.FilterFieldSource;
import com.otilm.api.model.core.search.SortDirection;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListViewUpdateRequestDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void cannotMoveAViewToAnotherResource() throws Exception {
        // given — the update shape has no resource, so an edit cannot repoint a view at a different catalogue
        var dto = new ListViewUpdateRequestDto();
        dto.setName("Expiry watch");
        dto.setColumns(List.of(new ListViewColumnDto(FilterFieldSource.PROPERTY, "commonName", null)));

        // when
        Map<String, Object> properties = mapper.readValue(mapper.writeValueAsString(dto), new TypeReference<>() {
        });

        // then
        assertFalse(properties.containsKey("resource"));
        assertEquals(Set.of("name", "columns", "defaultView", "filters", "sort"), properties.keySet());
    }

    @Test
    void renamesAndReordersColumnsInOneEdit() throws Exception {
        // given — rename and re-selection are the same operation on the same shape
        var dto = new ListViewUpdateRequestDto();
        dto.setName("Expiry watch v2");
        dto
                .setColumns(List
                        .of(new ListViewColumnDto(FilterFieldSource.CUSTOM, "department", null),
                                new ListViewColumnDto(FilterFieldSource.PROPERTY, "commonName", null)));
        dto.setSort(new ListViewSortRequestDto(FilterFieldSource.PROPERTY, "notAfter", SortDirection.ASC));

        // when
        var back = mapper.readValue(mapper.writeValueAsString(dto), ListViewUpdateRequestDto.class);

        // then
        assertEquals("Expiry watch v2", back.getName());
        assertEquals(List.of("department", "commonName"),
                back.getColumns().stream().map(ListViewColumnDto::getFieldIdentifier).toList());
        assertEquals(SortDirection.ASC, back.getSort().getDirection());
        assertTrue(VALIDATOR.validate(back).isEmpty());
    }

    @Test
    void rejectsANullColumnEntry() {
        // given — @NotEmpty rejects an empty list but permits [null], and cascaded validation skips null elements
        var dto = new ListViewUpdateRequestDto();
        dto.setName("Expiry watch");
        dto.setColumns(Collections.singletonList(null));

        // when
        var violations = VALIDATOR.validate(dto);

        // then
        assertEquals(1, violations.size());
        assertEquals("columns[0].<list element>", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void rejectsANullFilterEntry() {
        // given
        var dto = new ListViewUpdateRequestDto();
        dto.setName("Expiry watch");
        dto.setColumns(List.of(new ListViewColumnDto(FilterFieldSource.PROPERTY, "commonName", null)));
        dto.setFilters(Collections.singletonList(null));

        // when
        var violations = VALIDATOR.validate(dto);

        // then
        assertEquals(1, violations.size());
        assertEquals("filters[0].<list element>", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void rejectsAFilterThatAddressesNoField() {
        // given — a filter with no source, identifier or condition cannot be applied to a listing
        var dto = new ListViewUpdateRequestDto();
        dto.setName("Expiry watch");
        dto.setColumns(List.of(new ListViewColumnDto(FilterFieldSource.PROPERTY, "commonName", null)));
        dto.setFilters(List.of(new ListViewFilterDto()));

        // when
        var violations = VALIDATOR.validate(dto);

        // then
        assertEquals(Set.of("filters[0].fieldSource", "filters[0].fieldIdentifier", "filters[0].condition"),
                violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet()));
    }

    @Test
    void acceptsAFilterWithoutAValue() {
        // given — operators such as EMPTY address a field but carry no value
        var dto = new ListViewUpdateRequestDto();
        dto.setName("Expiry watch");
        dto.setColumns(List.of(new ListViewColumnDto(FilterFieldSource.PROPERTY, "commonName", null)));
        dto
                .setFilters(List
                        .of(new ListViewFilterDto(FilterFieldSource.PROPERTY, "commonName",
                                FilterConditionOperator.EMPTY, null)));

        // then
        assertTrue(VALIDATOR.validate(dto).isEmpty());
    }

    @Test
    void rejectsAnEditThatClearsEveryColumn() {
        // given
        var dto = new ListViewUpdateRequestDto();
        dto.setName("Expiry watch");
        dto.setColumns(List.of());

        // when
        var violations = VALIDATOR.validate(dto);

        // then
        assertEquals(1, violations.size());
        assertEquals("columns", violations.iterator().next().getPropertyPath().toString());
    }
}
