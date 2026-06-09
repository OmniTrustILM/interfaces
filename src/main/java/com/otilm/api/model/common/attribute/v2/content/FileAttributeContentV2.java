package com.otilm.api.model.common.attribute.v2.content;

import com.otilm.api.model.common.attribute.common.content.data.FileAttributeContentData;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        description = "File attribute content for storing encoded file content with additional info",
        type = "object")
public class FileAttributeContentV2 extends BaseAttributeContentV2<FileAttributeContentData> {

    @Schema(description = "File attribute content data", requiredMode = Schema.RequiredMode.REQUIRED)
    private FileAttributeContentData data;

    public FileAttributeContentV2(String reference, FileAttributeContentData data) {
        super(reference, data);
        this.data = data;
    }

    public FileAttributeContentV2() {
        super();
    }

    @Override
    public FileAttributeContentData getData() {
        return data;
    }

    @Override
    public void setData(FileAttributeContentData data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FileAttributeContentV2)) return false;
        if (!super.equals(o)) return false;
        FileAttributeContentV2 that = (FileAttributeContentV2) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), data);
    }
}
