package com.otilm.api.model.common.attribute.common;

import com.otilm.api.model.common.attribute.common.callback.AttributeCallback;
import com.otilm.api.model.common.attribute.common.constraint.BaseAttributeConstraint;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(implementation = DataAttributeDto.class)
public abstract class DataAttribute extends BaseAttribute implements DataAttributeDto {

    public abstract void setContent(List<? extends AttributeContent> content);
    public abstract AttributeContentType getContentType();
    public abstract DataAttributeProperties getProperties();

    public abstract List<BaseAttributeConstraint<?>> getConstraints();

    public abstract AttributeCallback getAttributeCallback();

}
