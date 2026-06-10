package com.otilm.api.model.common.attribute.v3.content;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.content.data.CodeBlockAttributeContentData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;

@Schema(
        description = "Codeblock attribute content to store encoded snippets of programming language code",
        type = "object")
@EqualsAndHashCode(callSuper = true)
public class CodeBlockAttributeContentV3 extends BaseAttributeContentV3<CodeBlockAttributeContentData> {

    @Schema(description = "CodeBlock attribute content data", requiredMode = Schema.RequiredMode.REQUIRED)
    private CodeBlockAttributeContentData data;

    public CodeBlockAttributeContentV3() {
    }

    public CodeBlockAttributeContentV3(String reference, CodeBlockAttributeContentData data) {
        super(reference, data);
        this.data = data;
        setContentType(AttributeContentType.CODEBLOCK);
    }

    @Override
    public CodeBlockAttributeContentData getData() {
        return data;
    }

    @Override
    public void setData(CodeBlockAttributeContentData data) {
        this.data = data;
    }
}
