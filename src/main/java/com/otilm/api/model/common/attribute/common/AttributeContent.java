package com.otilm.api.model.common.attribute.common;

import com.otilm.api.config.serializer.AttributeContentDeserializer;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.content.BaseAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(
        description = "Attribute Content",
        type = "object",
        oneOf = {
                BaseAttributeContentV2.class,
                BaseAttributeContentV3.class
        }

)
@JsonDeserialize(using = AttributeContentDeserializer.class)
public abstract class AttributeContent implements Serializable {

    public abstract <T> T getData();

    public abstract String getReference();

    public abstract AttributeContentType getContentType();

}
