package com.otilm.api.model.core.logging.records;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record SourceRecord(
        @NotNull String method,
        @NotNull String path,
        String contentType,
        String ipAddress,
        String userAgent
) implements Serializable {
}