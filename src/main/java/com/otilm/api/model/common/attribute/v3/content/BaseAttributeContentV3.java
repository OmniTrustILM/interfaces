package com.otilm.api.model.common.attribute.v3.content;

import com.otilm.api.model.client.attribute.BaseAttributeContentDtoV3;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "contentType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BooleanAttributeContentV3.class, name = AttributeContentType.Codes.BOOLEAN),
        @JsonSubTypes.Type(value = CodeBlockAttributeContentV3.class, name = AttributeContentType.Codes.CODEBLOCK),
        @JsonSubTypes.Type(value = DateAttributeContentV3.class, name = AttributeContentType.Codes.DATE),
        @JsonSubTypes.Type(value = DateTimeAttributeContentV3.class, name = AttributeContentType.Codes.DATETIME),
        @JsonSubTypes.Type(value = FileAttributeContentV3.class, name = AttributeContentType.Codes.FILE),
        @JsonSubTypes.Type(value = FloatAttributeContentV3.class, name = AttributeContentType.Codes.FLOAT),
        @JsonSubTypes.Type(value = IntegerAttributeContentV3.class, name = AttributeContentType.Codes.INTEGER),
        @JsonSubTypes.Type(value = ObjectAttributeContentV3.class, name = AttributeContentType.Codes.OBJECT),
        @JsonSubTypes.Type(value = StringAttributeContentV3.class, name = AttributeContentType.Codes.STRING),
        @JsonSubTypes.Type(value = TextAttributeContentV3.class, name = AttributeContentType.Codes.TEXT),
        @JsonSubTypes.Type(value = TimeAttributeContentV3.class, name = AttributeContentType.Codes.TIME),
        @JsonSubTypes.Type(value = ResourceObjectContent.class, name = AttributeContentType.Codes.RESOURCE)
})
@JsonInclude(JsonInclude.Include.ALWAYS)
@Schema(implementation = BaseAttributeContentDtoV3.class)
@JsonDeserialize
public class BaseAttributeContentV3<T extends Serializable> extends AttributeContent implements BaseAttributeContentDtoV3 {

    private String reference;

    @Hidden
    @Schema(description = "Content Data", requiredMode = Schema.RequiredMode.REQUIRED)
    private T data;

    @Schema(description = "Content Type of the attribute", requiredMode = Schema.RequiredMode.REQUIRED)
    private AttributeContentType contentType;

    public BaseAttributeContentV3() {
    }

    public BaseAttributeContentV3(String reference) {
        this.reference = reference;
    }

    public BaseAttributeContentV3(String reference, T data) {
        this.reference = reference;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof BaseAttributeContentV3<?> that)) return false;

        // content is considered equal when reference and data are equal
        return Objects.equals(this.reference, that.getReference()) && Objects.equals(this.getData(), that.getData()) && Objects.equals(this.contentType, that.getContentType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference, data);
    }

}
