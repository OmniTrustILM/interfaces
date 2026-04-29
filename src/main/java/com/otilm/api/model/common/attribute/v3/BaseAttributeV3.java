package com.otilm.api.model.common.attribute.v3;

import com.otilm.api.model.client.attribute.BaseAttributeDtoV3;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

@Setter
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", defaultImpl = DataAttributeV3.class, visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DataAttributeV3.class, name = AttributeType.Codes.DATA),
        @JsonSubTypes.Type(value = GroupAttributeV3.class, name = AttributeType.Codes.GROUP),
        @JsonSubTypes.Type(value = InfoAttributeV3.class, name = AttributeType.Codes.INFO),
        @JsonSubTypes.Type(value = MetadataAttributeV3.class, name = AttributeType.Codes.META),
        @JsonSubTypes.Type(value = CustomAttributeV3.class, name = AttributeType.Codes.CUSTOM)
})
@JsonInclude(JsonInclude.Include.ALWAYS)
@Schema(implementation = BaseAttributeDtoV3.class)
public class BaseAttributeV3<T> extends BaseAttribute implements BaseAttributeDtoV3 {

    @Schema(
            description = "Schema version of the Attribute",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private AttributeVersion schemaVersion = AttributeVersion.V3;

    private String uuid;

    private String name;

    private String description;

    @Hidden
    @Schema(
        description = "Content of the Attribute"
    )
    private T content;

    private int version = 3;

    private AttributeType type = AttributeType.DATA;

    public BaseAttributeV3() {
    }

    public BaseAttributeV3(AttributeType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("description", description)
                .append("content", content)
                .append("type", type)
                .toString();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BaseAttributeV3<?> that)) return false;
        return schemaVersion.equals(that.schemaVersion) && Objects.equals(uuid, that.uuid) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(getContent(), that.getContent()) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemaVersion, uuid, name, description, content, type);
    }
}
