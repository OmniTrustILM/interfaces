package com.otilm.api.model.core.search;

import io.swagger.v3.oas.annotations.media.Schema;

public class DynamicSearchInternalResponse {
    @Schema(description = "Number of items matching the filter", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long totalItems;
    @Schema(description = "Number of pages", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalPages;
    @Schema(description = "Search result", requiredMode = Schema.RequiredMode.REQUIRED)
    private Object result;

    public Long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
