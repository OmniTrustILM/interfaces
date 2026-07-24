package com.otilm.api.model.common.attribute.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v2.content.BaseAttributeContentV2;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        description = "Info attribute contains content that is for metadata. Its content can not be edited and is not send in requests to store.",
        type = "object"
)
@JsonDeserialize
@JsonSerialize
public class MetadataAttributeV2 extends MetadataAttribute {

    private String uuid;

    @NotBlank(message = "metadata attribute name is required")
    private String name;

    private String description;

    @Schema(description = "Version of the attribute", requiredMode = Schema.RequiredMode.REQUIRED)
    private int version = 2;

    private AttributeType type;

    /**
     * Content of the Attribute
     **/
    @Schema(
            description = "Content of the Attribute"
    )
    @NotEmpty(message = "metadata attribute content is required")
    private List<@NotNull @Valid BaseAttributeContentV2<?>> content;

    /**
     * Type of the Attribute content
     */
    @Schema(
            description = "Type of the Content",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "metadata attribute contentType is required")
    private AttributeContentType contentType;

    /**
     * Properties of the Attributes
     */
    @Schema(
            description = "Properties of the Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "metadata attribute properties are required")
    private MetadataAttributeProperties properties;

    public MetadataAttributeV2() {
        type = AttributeType.META;
    }

    public MetadataAttributeV2(MetadataAttributeV2 other) {
        this.uuid = other.uuid;
        this.name = other.name;
        this.description = other.description;
        this.version = other.version;
        this.type = other.type;
        this.content = other.content == null ? null : new ArrayList<>(other.content);
        this.contentType = other.contentType;
        this.properties = other.properties;
    }

    @Override
    public MetadataAttributeV2 copy() {
        return new MetadataAttributeV2(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("content", content)
                .append("contentType", contentType)
                .append("properties", properties)
                .toString();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MetadataAttributeV2 that))
            return false;
        return Objects.equals(content, that.content) && contentType == that.contentType && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), content, contentType, properties);
    }

    @Override
    public void setContent(List<? extends AttributeContent> content) {
        this.content = (List<BaseAttributeContentV2<?>>) content;
    }
}
