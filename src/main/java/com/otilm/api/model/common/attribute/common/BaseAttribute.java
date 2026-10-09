package com.otilm.api.model.common.attribute.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.otilm.api.config.serializer.BaseAttributeDeserializer;
import com.otilm.api.config.serializer.BaseAttributeSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.ALWAYS)
@Schema(implementation = BaseAttributeDto.class)
@JsonDeserialize(using = BaseAttributeDeserializer.class)
@JsonSerialize(using = BaseAttributeSerializer.class)
// Keeps the JSON in Jackson's default order. The order BaseAttributeDto declares is for its schema.
@JsonPropertyOrder
public abstract class BaseAttribute implements BaseAttributeDto {
    public abstract <T> T getContent();

    public abstract int getVersion();

}
