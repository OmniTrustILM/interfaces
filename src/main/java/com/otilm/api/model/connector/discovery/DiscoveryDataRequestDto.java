package com.otilm.api.model.connector.discovery;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DiscoveryDataRequestDto {

    @Schema(description = "Name of the Discovery",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Discovery Kind",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Schema(description = "Page number for the retrieved certificates",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageNumber;

    @Schema(description = "Number of certificates per page",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer itemsPerPage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("kind", kind)
                .append("pageNumber", pageNumber)
                .append("itemsPerPage", itemsPerPage)
                .toString();
    }
}
