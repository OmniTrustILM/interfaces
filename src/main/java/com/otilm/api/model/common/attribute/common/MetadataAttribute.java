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
public abstract class MetadataAttribute extends BaseAttribute implements MetadataAttributeDto, Cloneable {

    public abstract void setContent(List<? extends AttributeContent> content);

    public abstract AttributeContentType getContentType();

    public abstract MetadataAttributeProperties getProperties();

    /**
     * Shallow copy: a distinct instance sharing all field references with this one.
     * Lets callers strip or replace {@code content} on the copy without mutating the original.
     */
    @Override
    public MetadataAttribute clone() {
        try {
            return (MetadataAttribute) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
