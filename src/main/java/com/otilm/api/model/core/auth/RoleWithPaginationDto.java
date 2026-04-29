package com.otilm.api.model.core.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class RoleWithPaginationDto extends PaginationDto {

    @Schema(description = "Role data")
    private List<RoleDto> data;

    public List<RoleDto> getData() {
        return data;
    }

    public void setData(List<RoleDto> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("data", data)
                .append("currentPage", getCurrentPage())
                .append("pageSize", getPageSize())
                .append("totalCount", getTotalCount())
                .append("totalPages", getTotalPages())
                .append("hasPrevious", getHasPrevious())
                .append("hasNext", getHasNext())
                .toString();
    }
}
