package com.otilm.api.model.common.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.http.ProblemDetail;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ProblemDetailExtended extends ProblemDetail {

    @Schema(description = "RFC 9457 type URI identifying the problem type",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "uri",
            example = "https://docs.otilm.com/problems/common/validation-failed")
    @Override
    @NonNull
    public URI getType() {
        return super.getType();
    }

    @Schema(description = "Short human-readable summary of the problem type",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "Validation failed")
    @Override
    @Nullable
    public String getTitle() {
        return super.getTitle();
    }

    @Schema(description = "HTTP status code (MUST match the actual response code)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "422",
            minimum = "100", maximum = "599")
    @Override
    public int getStatus() {
        return super.getStatus();
    }

    @Schema(description = "Human-readable explanation specific to this occurrence",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "One or more fields failed validation.")
    @Override
    @Nullable
    public String getDetail() {
        return super.getDetail();
    }

    @Schema(description = "URI reference identifying the occurrence (e.g., request path or operation ID).",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            format = "uri",
            example = "https://api.example.org/foo/bar/123")
    @Override
    @Nullable
    public URI getInstance() {
        return super.getInstance();
    }

    @Schema(
            description = "Generic map of properties that are not known ahead of time",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    @Override
    @Nullable
    @SuppressWarnings("java:S2638") // it is intended to be nullable as is in parent class
    public Map<String, Object> getProperties() {
        return super.getProperties();
    }

    @Schema(description = "Application specific error code", requiredMode = Schema.RequiredMode.REQUIRED)
    private ErrorCode errorCode;

    @Schema(description = "Timestamp of when the error occurred (RFC 3339 format)", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"2026-01-22T15:10:20.123Z", "2026-01-22T15:10:20.123+02:00"})
    private OffsetDateTime timestamp;

    @Schema(description = "Correlation ID for tracking the request. Trace value from X-Request-Id or W3C Trace Context.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String correlationId;

    @Schema(description = "Indicates if the operation that caused the error can be retried safely", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean retryable;

    @Schema(description = "Backoff hint, number of seconds after which the client can retry the operation. Aligns with Retry-After header.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer retryAfterSeconds;

    public static ProblemDetailExtended fromErrorCode(ErrorCode errorCode, String detail, URI instance, String correlationId) {
        ProblemDetailExtended problemDetail = new ProblemDetailExtended();
        problemDetail.setType(URI.create("https://docs.otilm.com/problems/%s/%s".formatted(errorCode.getCategory().getCode(), errorCode.name())));
        problemDetail.setTitle(errorCode.getTitle());
        problemDetail.setStatus(errorCode.getStatus().value());
        problemDetail.setDetail(detail);
        if (instance != null) {
            problemDetail.setInstance(instance);
        }
        problemDetail.setErrorCode(errorCode);
        problemDetail.setTimestamp(OffsetDateTime.now());
        problemDetail.setCorrelationId(correlationId);
        problemDetail.setRetryable(errorCode.isRetryable());
        if (errorCode.isRetryable()) {
            problemDetail.retryAfterSeconds = 30;
        }
        return problemDetail;
    }

}
