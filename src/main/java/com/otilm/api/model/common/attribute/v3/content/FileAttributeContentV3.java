package com.otilm.api.model.common.attribute.v3.content;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.content.data.FileAttributeContentData;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        description = "File attribute content for storing encoded file content with additional info",
        type = "object")
@EqualsAndHashCode(callSuper = true)
public class FileAttributeContentV3 extends BaseAttributeContentV3<FileAttributeContentData> {

    @Schema(description = "File attribute content data", requiredMode = Schema.RequiredMode.REQUIRED)
    private FileAttributeContentData data;

    public FileAttributeContentV3(String reference, FileAttributeContentData data) {
        super(reference, data);
        this.data = data;
        setContentType(AttributeContentType.FILE);
    }

    public FileAttributeContentV3() {
        super();
        setContentType(AttributeContentType.FILE);
    }

    @Override
    public FileAttributeContentData getData() {
        return data;
    }

    @Override
    public void setData(FileAttributeContentData data) {
        this.data = data;
    }

}
