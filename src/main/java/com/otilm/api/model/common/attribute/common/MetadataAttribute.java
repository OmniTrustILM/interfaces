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

    /**
     * A distinct instance with the same field values. Only the {@code content} list container is
     * copied: replacing it wholesale (via {@link #setContent}) or structurally mutating the copy's
     * list (e.g. {@code copy.getContent().clear()}) never affects the original's list. This is a
     * shallow copy, not a deep one — the {@code AttributeContent} elements inside the list, and
     * other fields such as {@code properties}, are still shared by reference with the original.
     * Mutating an element in place, or mutating {@code properties}, corrupts the original too.
     * Callers must treat those as immutable after construction.
     */
    public abstract MetadataAttribute copy();
}
