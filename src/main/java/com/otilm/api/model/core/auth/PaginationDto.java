package com.otilm.api.model.core.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class PaginationDto {

    @Schema(description = "Current page number")
    private Integer currentPage;

    @Schema(description = "Number of records in the page")
    private Integer pageSize;

    @Schema(description = "Total number of pages")
    private Integer totalPages;

    @Schema(description = "Total number of entries")
    private Integer totalCount;

    @Schema(description = "Is previous page available")
    private Integer hasPrevious;

    @Schema(description = "Is next page available")
    private Integer hasNext;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(Integer hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public Integer getHasNext() {
        return hasNext;
    }

    public void setHasNext(Integer hasNext) {
        this.hasNext = hasNext;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("currentPage", currentPage)
                .append("pageSize", pageSize)
                .append("totalCount", totalCount)
                .append("totalPages", totalPages)
                .append("hasPrevious", hasPrevious)
                .append("hasNext", hasNext)
                .toString();
    }
}
