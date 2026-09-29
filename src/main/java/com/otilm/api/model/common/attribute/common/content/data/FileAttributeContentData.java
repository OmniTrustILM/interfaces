package com.otilm.api.model.common.attribute.common.content.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.core.logging.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.MimeType;

@Setter
@Getter
public class FileAttributeContentData implements AttributeContentData {

    @Schema(description = "File content", requiredMode = Schema.RequiredMode.REQUIRED)
    @Sensitive
    private String content;

    @Schema(description = "Name of the file", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fileName;

    @Schema(description = "Type of the file uploaded", requiredMode = Schema.RequiredMode.REQUIRED)
    private String mimeType;

    @JsonIgnore
    public MimeType getMimeTypeObject() {
        return this.mimeType != null ? MimeType.valueOf(this.mimeType) : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FileAttributeContentData that)) {
            return false;
        }
        return Objects.equals(content, that.content) && Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, fileName);
    }

    // Never append the raw content — only its length (or "none"), so a credential or connector-auth keystore
    // never reaches a log line through this toString().
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("content", content == null ? "none" : content.length())
                .append("fileName", fileName)
                .append("mimeType", mimeType)
                .toString();
    }

    @Override
    public void validate() throws ValidationException {
        if (content == null) {
            throw new ValidationException("Content is not present in file attribute content data");
        }
        if (fileName == null) {
            throw new ValidationException("File name is not present in file attribute content data");
        }
        if (mimeType == null) {
            throw new ValidationException("MIME type is not present in file attribute content data");
        }
    }
}
