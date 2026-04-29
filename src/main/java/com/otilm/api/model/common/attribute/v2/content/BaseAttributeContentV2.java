package com.otilm.api.model.common.attribute.v2.content;

import com.otilm.api.model.client.attribute.BaseAttributeContentDtoV2;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Schema(implementation = BaseAttributeContentDtoV2.class)
@JsonDeserialize
public class BaseAttributeContentV2<T extends Serializable> extends AttributeContent implements BaseAttributeContentDtoV2 {

    private String reference;

    @Hidden
    @Schema(description = "Content Data", requiredMode = Schema.RequiredMode.REQUIRED)
    private T data;

    public BaseAttributeContentV2() {
    }

    public BaseAttributeContentV2(String reference) {
        this.reference = reference;
    }

    public BaseAttributeContentV2(String reference, T data) {
        this.reference = reference;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (!(o instanceof BaseAttributeContentV2<?> that)) return false;

        // content is considered equal when reference and data are equal
        return Objects.equals(this.reference, that.getReference()) && Objects.equals(this.getData(), that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference, data);
    }

    @Override
    @Hidden
    @JsonIgnore
    public AttributeContentType getContentType() {
        return null;
    }

}
