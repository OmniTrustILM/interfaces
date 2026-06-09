package com.otilm.api.model.common;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ErrorMessageDto {

    @Schema(description = "Error message detail",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"Error message"})
    private String message;

    public ErrorMessageDto(String message) {
        this.message = message;
    }

    public static ErrorMessageDto getInstance(String message) {
        return new ErrorMessageDto(message);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("message", message)
                .toString();
    }
}
