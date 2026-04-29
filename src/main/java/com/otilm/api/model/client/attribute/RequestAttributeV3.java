package com.otilm.api.model.client.attribute;

import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAttributeV3 extends RequestAttribute {

    private UUID uuid;

    private String name;

    private AttributeContentType contentType;

    private List<BaseAttributeContentV3<?>> content;

    private AttributeVersion version = AttributeVersion.V3;

    public RequestAttributeV3(UUID uuid, String name, AttributeContentType contentType, List<BaseAttributeContentV3<?>> content) {
        this(uuid, name, contentType, content, AttributeVersion.V3);
    }


}
