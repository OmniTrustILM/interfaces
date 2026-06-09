package com.otilm.api.model.core.search;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Data
public class SearchFieldDataByGroupDto {

    @Schema(description = "Search group", requiredMode = Schema.RequiredMode.REQUIRED)
    private FilterFieldSource filterFieldSource;

    @Schema(description = "List of search fields for specified search group", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<SearchFieldDataDto> searchFieldData;

    public SearchFieldDataByGroupDto(List<SearchFieldDataDto> searchFieldData, FilterFieldSource filterFieldSource) {
        this.searchFieldData = searchFieldData;
        this.filterFieldSource = filterFieldSource;
    }
}
