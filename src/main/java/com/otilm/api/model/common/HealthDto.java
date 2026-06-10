package com.otilm.api.model.common;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;

public class HealthDto {

    @Schema(description = "Current connector operational status",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private HealthStatus status;

    @Schema(description = "Detailed status description")
    private String description;

    @Schema(description = "Nested status of services")
    private Map<String, HealthDto> parts;

    public HealthStatus getStatus() {
        return status;
    }

    public void setStatus(HealthStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, HealthDto> getParts() {
        return parts;
    }

    public void setParts(Map<String, HealthDto> parts) {
        this.parts = parts;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("status", status)
                .append("description", description)
                .append("parts", parts)
                .toString();
    }
}
