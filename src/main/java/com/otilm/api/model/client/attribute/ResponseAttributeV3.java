package com.otilm.api.model.client.attribute;

import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAttributeV3 extends ResponseAttribute {

    private List<BaseAttributeContentV3<?>> content;

    private UUID uuid;

    private String name;

    private String label;

    private AttributeType type;

    private AttributeContentType contentType;

    private AttributeVersion version = AttributeVersion.V3;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResponseAttributeV3 that)) return false;
        return Objects.equals(uuid, that.uuid) && Objects.equals(content, that.content) && Objects.equals(name, that.name) && Objects.equals(label, that.label) && type == that.type && contentType == that.contentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, content, name, label, type, contentType);
    }
    
}
