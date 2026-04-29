package com.otilm.api.model.core.audit;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ExportResultDto {
	
	@Schema(
            description = "Name of the file",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String fileName;
	
	@Schema(
            description = "File content in byte array",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] fileContent;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fileName", fileName)
                .append("fileContent", fileContent)
                .toString();
    }
}
