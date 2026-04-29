package com.otilm.api.model.common.attribute.v2;

import com.otilm.api.model.client.attribute.BaseAttributeDtoV2;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", defaultImpl = DataAttributeV2.class, visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DataAttributeV2.class, name = AttributeType.Codes.DATA),
        @JsonSubTypes.Type(value = GroupAttributeV2.class, name = AttributeType.Codes.GROUP),
        @JsonSubTypes.Type(value = InfoAttributeV2.class, name = AttributeType.Codes.INFO),
        @JsonSubTypes.Type(value = MetadataAttributeV2.class, name = AttributeType.Codes.META),
        @JsonSubTypes.Type(value = CustomAttributeV2.class, name = AttributeType.Codes.CUSTOM)
})
@JsonInclude(JsonInclude.Include.ALWAYS)
@Schema(name = "BaseAttributeV2", implementation = BaseAttributeDtoV2.class)
public class BaseAttributeV2<T> extends BaseAttribute implements BaseAttributeDtoV2 {

    private String uuid;

    private String name;

    private String description;

    @Schema(description = "Version of the attribute", requiredMode = Schema.RequiredMode.REQUIRED)
    private int version = 2;

    @Hidden
    @Schema(
        description = "Content of the Attribute"
    )
    private T content;

    private AttributeType type = AttributeType.DATA;

    public BaseAttributeV2() {
    }

    public BaseAttributeV2(AttributeType type) {
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
        if (!(object instanceof BaseAttributeV2<?> that)) return false;
        return version == (that.version) && Objects.equals(uuid, that.uuid) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(getContent(), that.getContent()) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, uuid, name, description, content, type);
    }


}
