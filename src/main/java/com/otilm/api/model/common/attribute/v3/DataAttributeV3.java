package com.otilm.api.model.common.attribute.v3;

import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.DataAttribute;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.callback.AttributeCallback;
import com.otilm.api.model.common.attribute.common.constraint.BaseAttributeConstraint;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.Objects;

/**
 * This class contains set of properties to represent
 * an Attribute definition including its value for the attributes
 * of type Data.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        description = "Data attribute allows to store and transfer dynamic data. Its content can be edited and send in requests to store.",
        type = "object"
)
@JsonDeserialize
@JsonSerialize
public class DataAttributeV3 extends DataAttribute {

    private String uuid;

    private String name;

    private String description;

    @Schema(description = "Version of the attribute", requiredMode = Schema.RequiredMode.REQUIRED)
    private int version = 3;

    private AttributeType type;


    /**
     * Content of the Attribute
     **/
    @ArraySchema(
            schema = @Schema(
                    ref = "BaseAttributeContentDtoV3"
            ),
            arraySchema = @Schema(
                    description = "Content of the Attribute"
            )
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
    private DataAttributeProperties properties;

    /**
     * List of constraints for the Attributes
     **/
    @Schema(
            description = "Optional constraints used for validating the Attribute content"
    )
    private List<BaseAttributeConstraint<?>> constraints;

    /**
     * Optional definition of callback for getting the content of the Attribute based on the action
     **/
    @Schema(
            description = "Optional definition of callback for getting the content of the Attribute based on the action"
    )
    private AttributeCallback attributeCallback;

    @Schema(
            description = "Schema version of the Attribute",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private AttributeVersion schemaVersion = AttributeVersion.V3;

    public DataAttributeV3() {
        this.type = AttributeType.DATA;
    }


    public DataAttributeV3(DataAttributeV3 original) {
        this.type = AttributeType.DATA;
        setUuid(original.getUuid());
        setName(original.getName());
        this.content = original.content;
        this.properties = original.properties;
        this.constraints = original.constraints;
        this.attributeCallback = original.attributeCallback;
        this.contentType = original.contentType;
        this.schemaVersion = original.schemaVersion;
        setDescription(original.getDescription());
        setType(original.getType());
        setContentType(original.contentType);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("content", content)
                .append("contentType", contentType)
                .append("properties", properties)
                .append("constraints", constraints)
                .append("attributeCallback", attributeCallback)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataAttributeV3 that)) return false;

        return Objects.equals(content, that.content)
                && contentType == that.contentType
                && Objects.equals(properties, that.properties)
                && Objects.equals(constraints, that.constraints)
                && Objects.equals(attributeCallback, that.attributeCallback);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                content,
                contentType,
                properties,
                constraints,
                attributeCallback
        );
    }

    @Override
    public void setContent(List<? extends AttributeContent> content) {
        this.content = (List<BaseAttributeContentV3<?>>) content;
    }
}
