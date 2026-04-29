package com.otilm.api.model.common.attribute.common;


import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(implementation = MetadataAttributeDto.class)
public abstract class MetadataAttribute extends BaseAttribute implements MetadataAttributeDto {

    public abstract void setContent(List<? extends AttributeContent> content);

    public abstract AttributeContentType getContentType();

    public abstract MetadataAttributeProperties getProperties();
}
