package com.otilm.api.model.client.attribute;

import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.content.BaseAttributeContentV2;
import lombok.*;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAttributeV2 extends RequestAttribute {


    private UUID uuid;

    private String name;

    private AttributeContentType contentType;

    private List<BaseAttributeContentV2<?>> content;

    private AttributeVersion version = AttributeVersion.V2;

    public RequestAttributeV2(UUID uuid, String name, AttributeContentType contentType, List<BaseAttributeContentV2<?>> content) {
        this(uuid, name, contentType, content, AttributeVersion.V2);
    }
}
