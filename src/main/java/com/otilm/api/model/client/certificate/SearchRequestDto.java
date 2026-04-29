package com.otilm.api.model.client.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class SearchRequestDto {

    @Schema(description = "Certificate filter input")
    private List<SearchFilterRequestDto> filters;

    @Schema(description = "Number of entries per page", defaultValue = "10", maximum = "1000")
    private Integer itemsPerPage = 10;

    @Schema(description = "Page number for the request", defaultValue = "1")
    private Integer pageNumber = 1;
}
