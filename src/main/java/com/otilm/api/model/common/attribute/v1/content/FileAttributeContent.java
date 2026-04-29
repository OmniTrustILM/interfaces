package com.otilm.api.model.common.attribute.v1.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileAttributeContent extends BaseAttributeContent<String> {
    @Schema(description = "Name of the file", examples = {"example.txt"})
    private String fileName;

    @Schema(description = "Content-Type of the data", example="xml")
    private String contentType;

    public FileAttributeContent() { }

    public FileAttributeContent(String value, String fileName, String contentType) {
        super(value);
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
