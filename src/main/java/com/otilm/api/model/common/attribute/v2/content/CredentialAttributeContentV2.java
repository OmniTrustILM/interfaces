package com.otilm.api.model.common.attribute.v2.content;

import com.otilm.api.model.common.attribute.common.content.data.CredentialAttributeContentData;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(
        description = "Credential attribute content carrying information about credential to use",
        type = "object")
public class CredentialAttributeContentV2 extends BaseAttributeContentV2<CredentialAttributeContentData> {

    @Schema(description = "Credential attribute content data", requiredMode = Schema.RequiredMode.REQUIRED)
    private CredentialAttributeContentData data;

    public CredentialAttributeContentV2() {
    }

    public CredentialAttributeContentV2(CredentialAttributeContentData data) {
        this.data = data;
    }

    public CredentialAttributeContentV2(String reference, CredentialAttributeContentData data) {
        super(reference);
        this.data = data;
    }

    @Override
    public CredentialAttributeContentData getData() {
        return data;
    }

    @Override
    public void setData(CredentialAttributeContentData data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CredentialAttributeContentV2 that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode()
        );
    }


}
