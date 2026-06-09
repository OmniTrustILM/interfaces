package com.otilm.api.model.core.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class SubjectPermissionsDto {

    @Schema(description = "Allow all resources, True = Yes, False = No", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean allowAllResources;

    @Schema(description = "Resources", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResourcePermissionsDto> resources;

    public Boolean getAllowAllResources() {
        return allowAllResources;
    }

    public void setAllowAllResources(Boolean allowAllResources) {
        this.allowAllResources = allowAllResources;
    }

    public List<ResourcePermissionsDto> getResources() {
        return resources;
    }

    public void setResources(List<ResourcePermissionsDto> resources) {
        this.resources = resources;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("allowAllResources", allowAllResources)
                .append("resources", resources)
                .toString();
    }
}
