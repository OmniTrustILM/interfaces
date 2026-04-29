package com.otilm.api.model.core.audit;

import com.otilm.api.model.core.logging.enums.Module;
import com.otilm.api.model.core.logging.enums.Operation;
import com.otilm.api.model.core.logging.enums.OperationResult;
import com.otilm.api.model.core.logging.records.ActorRecord;
import com.otilm.api.model.core.logging.records.ResourceRecord;
import com.otilm.api.model.core.logging.records.SourceRecord;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@JsonPropertyOrder({"id", "version", "loggedAt", "timestamp", "module", "actor", "source", "resource", "affiliatedResource",
        "operation", "operationResult", "message", "operationData", "additionalData"})
public class AuditLogDto {

    @Schema(
            description = "Audit log Id",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long id;

    @Schema(
            description = "Log schema version",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String version;

    @Schema(
            description = "Time when the audit log is stored",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private OffsetDateTime loggedAt;

    @Schema(
            description = "Time when the audit log has been created",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private OffsetDateTime timestamp;

    @Schema(
            description = "Module of platform where log occurred",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Module module;

    @Schema(
            description = "Actor of log record",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ActorRecord actor;

    @Schema(
            description = "Source of log record",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private SourceRecord source;

    @Schema(
            description = "Resource that is subject of action logged",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ResourceRecord resource;

    @Schema(
            description = "Affiliated resource that is related to subject resource",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private ResourceRecord affiliatedResource;

    @Schema(
            description = "Resource operation that is being logged",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Operation operation;

    @Schema(
            description = "Result of the resource operation. Either success or failed",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private OperationResult operationResult;

    @Schema(
            description = "Additional message",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String message;

    @Schema(
            description = "Structured data dependent on resource and its operation",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Serializable operationData;

    @Schema(
            description = "Additional data specific to event logged",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Map<String, Object> additionalData;
}
