package com.otilm.api.model.common.attribute.v3.content;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.content.data.ResourceObjectContentData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;

@Schema(
        description = "Resource object attribute content carrying resource object data",
        type = "object")
@EqualsAndHashCode(callSuper = true)
public class ResourceObjectContent extends BaseAttributeContentV3<ResourceObjectContentData> {

    @Schema(description = "Resource Object content data", requiredMode = Schema.RequiredMode.REQUIRED)
    private ResourceObjectContentData data;

    @Override
    public ResourceObjectContentData getData() {
        return data;
    }

    public ResourceObjectContent() {
    }

    @Override
    public void setData(ResourceObjectContentData data) {
        this.data = data;
    }

    public ResourceObjectContent(String reference, ResourceObjectContentData data) {
        setContentType(AttributeContentType.RESOURCE);
        this.data = data;
        this.setReference(reference);
    }
}
