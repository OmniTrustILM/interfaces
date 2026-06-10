package com.otilm.api.model.core.logging.records;

import com.otilm.api.model.core.logging.enums.OperationResult;
import com.otilm.api.model.core.logging.enums.Operation;
import com.otilm.api.model.core.logging.enums.Module;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Map;

@Builder
public record LogRecord (
        @NotNull String version,
        @NotNull boolean audited,
        @NotNull Module module,
        @NotNull ActorRecord actor,
        SourceRecord source,
        @NotNull ResourceRecord resource,
        ResourceRecord affiliatedResource,
        @NotNull Operation operation,
        @NotNull OperationResult operationResult,
        String message,
        Serializable operationData,
        Map<String, Object> additionalData,
        @NotNull @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") OffsetDateTime timestamp
        ) implements Serializable {}
