package com.otilm.api.model.client.attribute.custom;

import com.otilm.api.model.client.attribute.AttributeDefinitionDto;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class CustomAttributeDefinitionDto extends AttributeDefinitionDto {

    /**
     * Resources of the Custom Attribute
     */
    @Schema(description = "List of resources for custom attribute", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Resource> resources;

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
