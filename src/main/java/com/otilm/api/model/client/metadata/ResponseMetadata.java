package com.otilm.api.model.client.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "version",
        defaultImpl = ResponseMetadataV3.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ResponseMetadataV3.class, name = AttributeVersion.Codes.V3),
        @JsonSubTypes.Type(value = ResponseMetadataV2.class, name = AttributeVersion.Codes.V2)})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(implementation = ResponseMetadataDto.class)
public abstract class ResponseMetadata implements ResponseMetadataDto {

    public abstract <T extends AttributeContent> List<T> getContent();

}
