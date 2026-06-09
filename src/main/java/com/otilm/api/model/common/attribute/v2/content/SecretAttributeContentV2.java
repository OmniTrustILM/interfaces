package com.otilm.api.model.common.attribute.v2.content;

import com.otilm.api.model.common.attribute.common.content.data.SecretAttributeContentData;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        description = "Secret attribute content carrying secrets with defined protection level",
        type = "object")
public class SecretAttributeContentV2 extends BaseAttributeContentV2<SecretAttributeContentData> {

    @Schema(description = "Secret attribute content data", requiredMode = Schema.RequiredMode.REQUIRED)
    private SecretAttributeContentData data;

    public SecretAttributeContentV2(String reference, SecretAttributeContentData data) {
        super(reference, data);
        this.data = data;
    }

    public SecretAttributeContentV2() {
    }

    @Override
    public SecretAttributeContentData getData() {
        return data;
    }

    @Override
    public void setData(SecretAttributeContentData data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecretAttributeContentV2)) return false;
        SecretAttributeContentV2 that = (SecretAttributeContentV2) o;
        return Objects.equals(this.data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data);
    }
}
