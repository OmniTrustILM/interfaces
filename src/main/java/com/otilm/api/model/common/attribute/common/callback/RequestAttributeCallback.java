package com.otilm.api.model.common.attribute.common.callback;

import com.otilm.api.model.core.scheduler.PaginationRequestDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class RequestAttributeCallback {

    @Schema(description = "UUID of the Attribute")
    private String uuid;

    @Schema(description = "Name of the Attribute",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(
            description = "Map of path variables supported by the callback method"
    )
    private Map<String, Serializable> pathVariable;

    @Schema(
            description = "Map of the query parameters supported by the callback method"
    )
    private Map<String, Serializable> requestParameter;

    @Schema(
            description = "Request body for the callback method"
    )
    private Map<String, Serializable> body;

    @Schema(
            description = "Filter for the resource callback method", requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Map<String, Serializable> filter;

    @Schema(description = "Pagination of the callback response", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PaginationRequestDto pagination;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("uuid", uuid)
                .append("pathVariable", pathVariable)
                .append("requestParameter", requestParameter)
                .append("body", body)
                .toString();
    }
}
