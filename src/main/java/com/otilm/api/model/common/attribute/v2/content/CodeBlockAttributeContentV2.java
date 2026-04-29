package com.otilm.api.model.common.attribute.v2.content;

import com.otilm.api.model.common.attribute.common.content.data.CodeBlockAttributeContentData;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(
        description = "Codeblock attribute content to store encoded snippets of programming language code",
        type = "object")
public class CodeBlockAttributeContentV2 extends BaseAttributeContentV2<CodeBlockAttributeContentData> {

    @Schema(description = "CodeBlock attribute content data", requiredMode = Schema.RequiredMode.REQUIRED)
    private CodeBlockAttributeContentData data;

    public CodeBlockAttributeContentV2() {
    }

    public CodeBlockAttributeContentV2(String reference, CodeBlockAttributeContentData data) {
        super(reference, data);
    }

    @Override
    public CodeBlockAttributeContentData getData() {
        return data;
    }

    @Override
    public void setData(CodeBlockAttributeContentData data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodeBlockAttributeContentV2 that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                data
        );
    }
}
