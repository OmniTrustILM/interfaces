package com.otilm.api.model.core.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ResourcePermissionsDto {

    @Schema(description = "Name of the Resource", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Allow all actions. True = Yes, False = No", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean allowAllActions;

    @Schema(description = "List of actions permitted", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> actions;

    @Schema(description = "Object permissions", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ObjectPermissionsDto> objects;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAllowAllActions() {
        return allowAllActions;
    }

    public void setAllowAllActions(Boolean allowAllActions) {
        this.allowAllActions = allowAllActions;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public List<ObjectPermissionsDto> getObjects() {
        return objects;
    }

    public void setObjects(List<ObjectPermissionsDto> objects) {
        this.objects = objects;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("allowAllActions", allowAllActions)
                .append("actions", actions)
                .append("objects", objects)
                .toString();
    }
}
