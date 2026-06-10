package com.otilm.api.model.common.attribute.common;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.CustomAttributeProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@Schema(implementation = CustomAttributeDto.class)
public abstract class CustomAttribute extends BaseAttribute implements CustomAttributeDto {

    public abstract void setContent(List<? extends AttributeContent> content);
    public abstract AttributeContentType getContentType();
    public abstract CustomAttributeProperties getProperties();
}
