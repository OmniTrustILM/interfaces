package com.otilm.api.model.common.attribute.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestAttributeCallback {

    @Schema(description = "UUID of the Attribute")
    private String uuid;

    @Schema(description = "Name of the Attribute",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(
            description = "Map of path variables supported by the callback method"
    )
    private Map<String, Serializable> pathVariables;

    @Schema(
            description = "Map of the query parameters supported by the callback method"
    )
    private Map<String, Serializable> queryParameters;

    @Schema(
            description = "Request body for the callback method"
    )
    private Map<String, Serializable> requestBody;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, Serializable> getPathVariables() {
        return pathVariables;
    }

    public void setPathVariables(Map<String, Serializable> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public Map<String, Serializable> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, Serializable> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public Map<String, Serializable> getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Map<String, Serializable> requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("uuid", uuid)
                .append("pathVariables", pathVariables)
                .append("queryParameters", queryParameters)
                .append("requestBody", requestBody)
                .toString();
    }
}
