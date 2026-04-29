package com.otilm.api.model.common.attribute.v3.content.data;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.core.auth.AttributeResource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(
        title = "ResourceSimpleContentData",
        description = "Content data for resource object defined by its attributes"
)
public class ResourceSimpleContentData extends ResourceObjectContentData {

    @Schema(description = "Attributes of the resource object", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> attributes;

    public ResourceSimpleContentData(AttributeResource resource) {
        super(resource);
    }

    @JsonCreator
    public ResourceSimpleContentData(
            @JsonProperty("resource") AttributeResource resource,
            @JsonProperty("uuid") String uuid,
            @JsonProperty("name") String name,
            @JsonProperty("attributes") List<ResponseAttribute> attributes) {
        this(resource);
        this.uuid = uuid;
        this.name = name;
        this.attributes = attributes;
    }

}
