package com.otilm.api.model.core.listview;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.certificate.SearchSortRequestDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.search.FilterConditionOperator;
import com.otilm.api.model.core.search.FilterFieldSource;
import com.otilm.api.model.core.search.SortDirection;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListViewDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTripsAViewThatStoresColumnsOnly() throws Exception {
        // given — a persisted view carries a name, a resource and an ordered column set
        var dto = new ListViewDto();
        dto.setUuid("f0f0d6a2-2a3e-11ee-be56-0242ac120002");
        dto.setName("Expiry watch");
        dto.setResource(Resource.CERTIFICATE);
        dto
                .setColumns(List
                        .of(new ListViewColumnDto(FilterFieldSource.PROPERTY, "commonName", null),
                                new ListViewColumnDto(FilterFieldSource.PROPERTY, "notAfter", null),
                                new ListViewColumnDto(FilterFieldSource.CUSTOM, "department", null)));
        dto.setDefaultView(true);

        // when
        var back = mapper.readValue(mapper.writeValueAsString(dto), ListViewDto.class);

        // then
        assertEquals("Expiry watch", back.getName());
        assertEquals(Resource.CERTIFICATE, back.getResource());
        assertTrue(back.isDefaultView());
        assertEquals(List.of("commonName", "notAfter", "department"),
                back.getColumns().stream().map(ListViewColumnDto::getFieldIdentifier).toList());
        assertNull(back.getFilters());
        assertNull(back.getSort());
    }

    @Test
    void roundTripsTheFiltersAndOrderingAViewApplies() throws Exception {
        // given — a view restores its slice of the inventory, so its filters and ordering travel with its columns
        var dto = new ListViewDto();
        dto.setName("Revoked, newest first");
        dto.setResource(Resource.CRYPTOGRAPHIC_KEY);
        dto.setColumns(List.of(new ListViewColumnDto(FilterFieldSource.PROPERTY, "name", null)));
        dto
                .setFilters(List
                        .of(new ListViewFilterDto(FilterFieldSource.PROPERTY, "state", FilterConditionOperator.EQUALS,
                                "active")));
        dto.setSort(new SearchSortRequestDto(FilterFieldSource.PROPERTY, "creationTime", SortDirection.DESC));

        // when
        var back = mapper.readValue(mapper.writeValueAsString(dto), ListViewDto.class);

        // then
        assertEquals("state", back.getFilters().get(0).getFieldIdentifier());
        assertEquals(FilterConditionOperator.EQUALS, back.getFilters().get(0).getCondition());
        assertEquals("creationTime", back.getSort().getFieldIdentifier());
        assertEquals(SortDirection.DESC, back.getSort().getDirection());
    }

    @Test
    void defaultsToNotBeingTheDefaultView() {
        assertFalse(new ListViewDto().isDefaultView());
    }

    @Test
    void namesTheResourceByItsWireCode() throws Exception {
        // given
        var dto = new ListViewDto();
        dto.setResource(Resource.CERTIFICATE);

        // when
        var json = mapper.writeValueAsString(dto);

        // then
        assertTrue(json.contains("\"resource\":\"certificates\""));
    }
}
