package com.otilm.api.model.client.metadata;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Response metadata attribute instance with content")
@AllArgsConstructor
public class ResponseMetadataV3 extends ResponseMetadata {

    private List<NameAndUuidDto> sourceObjects;
    private UUID uuid;
    private String name;
    private String label;
    private AttributeType type;
    private AttributeContentType contentType;
    private List<BaseAttributeContentV3<?>> content;


    @Override
    public AttributeVersion getVersion() {
        return AttributeVersion.V3;
    }
}
