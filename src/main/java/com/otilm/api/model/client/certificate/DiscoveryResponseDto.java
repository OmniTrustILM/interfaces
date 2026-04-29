package com.otilm.api.model.client.certificate;

import com.otilm.api.model.client.discovery.DiscoveryHistoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class DiscoveryResponseDto {
    @Schema(description = "Discoveries", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<DiscoveryHistoryDto> discoveries;

    @Schema(description = "Number of entries per page", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer itemsPerPage;

    @Schema(description = "Page number for the request", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageNumber;

    @Schema(description = "Number of pages available", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalPages;

    @Schema(description = "Number of items available", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long totalItems;
}
