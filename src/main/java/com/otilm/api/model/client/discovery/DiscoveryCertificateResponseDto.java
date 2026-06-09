package com.otilm.api.model.client.discovery;

import com.otilm.api.model.core.discovery.DiscoveryCertificateDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class DiscoveryCertificateResponseDto {
    @Schema(description = "Certificates", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<DiscoveryCertificateDto> certificates;

    @Schema(description = "Number of entries per page", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer itemsPerPage;

    @Schema(description = "Page number for the request", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageNumber;

    @Schema(description = "Number of pages available", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalPages;

    @Schema(description = "Number of items available", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long totalItems;
}
