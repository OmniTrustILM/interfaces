package com.otilm.api.model.common.attribute.v3;

import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
public class MetadataAttributeV3 extends MetadataAttribute {

    private String uuid;

    private String name;

    private String description;

    @Schema(description = "Version of the attribute", requiredMode = Schema.RequiredMode.REQUIRED)
    private int version = 3;

    private AttributeType type;

    /**
     * Content of the Attribute
     **/
    @Schema(
            description = "Content of the Attribute"
    )
    private List<BaseAttributeContentV3<?>> content;

    /**
     * Type of the Attribute content
     */
    @Schema(
            description = "Type of the Content",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private AttributeContentType contentType;

    /**
     * Properties of the Attributes
     */
    @Schema(
            description = "Properties of the Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private MetadataAttributeProperties properties;

    @Schema(
            description = "Schema version of the Attribute",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private AttributeVersion schemaVersion = AttributeVersion.V3;

    public MetadataAttributeV3() {
        type = AttributeType.META;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetadataAttributeV3 that)) return false;

        return Objects.equals(content, that.content)
                && contentType == that.contentType
                && Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                content,
                contentType,
                properties
        );
    }

    @Override
    public void setContent(List<? extends AttributeContent> content) {
        this.content = (List<BaseAttributeContentV3<?>>) content;
    }
}
