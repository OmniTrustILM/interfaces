package com.czertainly.api.model.common.error;

import com.czertainly.api.model.client.connector.v2.ConnectorInterface;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Schema(enumAsRef = true)
public enum ErrorCode {
    VALIDATION_FAILED(ProblemTypeCategory.COMMON, null, "Validation failed", HttpStatus.UNPROCESSABLE_ENTITY, true),
    RESOURCE_NOT_FOUND(ProblemTypeCategory.COMMON, null, "Resource not found", HttpStatus.NOT_FOUND, true),
    RESOURCE_ALREADY_EXISTS(ProblemTypeCategory.COMMON, null, "Resource already exists", HttpStatus.CONFLICT, true),
    REQUEST_TIMEOUT(ProblemTypeCategory.COMMON, null, "Request timeout", HttpStatus.REQUEST_TIMEOUT, true),
    OPERATION_NOT_SUPPORTED(ProblemTypeCategory.COMMON, null, "Operation not supported", HttpStatus.NOT_IMPLEMENTED, true),
    ATTRIBUTES_ERROR(ProblemTypeCategory.COMMON, null, "Attributes handling error", HttpStatus.BAD_REQUEST, true),
    SERVICE_UNAVAILABLE(ProblemTypeCategory.COMMON, null, "Service unavailable", HttpStatus.SERVICE_UNAVAILABLE, true),
    INTERNAL_SERVER_ERROR(ProblemTypeCategory.COMMON, null, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, false),
    BAD_REQUEST(ProblemTypeCategory.COMMON, null, "Bad Request", HttpStatus.BAD_REQUEST, true),
    UNAUTHORIZED(ProblemTypeCategory.COMMON, null, "Unauthorized", HttpStatus.UNAUTHORIZED, true),
    FORBIDDEN(ProblemTypeCategory.COMMON, null, "Forbidden", HttpStatus.FORBIDDEN, true),
    RATE_LIMIT_EXCEEDED(ProblemTypeCategory.COMMON, null, "Rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS, true)
    ;

    private final ProblemTypeCategory category;

    @Schema(description = "Provider interface this code is specific to; null for COMMON and CONNECTOR-general codes")
    private final ConnectorInterface interfaceCode;

    private final String title;
    private final HttpStatus status;
    private final boolean retryable;

    ErrorCode(ProblemTypeCategory category, ConnectorInterface interfaceCode, String title, HttpStatus status, boolean retryable) {
        this.category = category;
        this.interfaceCode = interfaceCode;
        this.title = title;
        this.status = status;
        this.retryable = retryable;
    }

}
