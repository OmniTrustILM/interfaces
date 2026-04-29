package com.otilm.api.model.common.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Schema(enumAsRef = true)
public enum ErrorCode {
    VALIDATION_FAILED(ProblemTypeCategory.COMMON, "Validation failed", HttpStatus.UNPROCESSABLE_ENTITY, true),
    RESOURCE_NOT_FOUND(ProblemTypeCategory.COMMON, "Resource not found", HttpStatus.NOT_FOUND, true),
    RESOURCE_ALREADY_EXISTS(ProblemTypeCategory.COMMON, "Resource already exists", HttpStatus.CONFLICT, true),
    REQUEST_TIMEOUT(ProblemTypeCategory.COMMON, "Request timeout", HttpStatus.REQUEST_TIMEOUT, true),
    OPERATION_NOT_SUPPORTED(ProblemTypeCategory.COMMON, "Operation not supported", HttpStatus.NOT_IMPLEMENTED, true),
    ATTRIBUTES_ERROR(ProblemTypeCategory.COMMON, "Attributes handling error", HttpStatus.BAD_REQUEST, true),
    SERVICE_UNAVAILABLE(ProblemTypeCategory.COMMON, "Service unavailable", HttpStatus.SERVICE_UNAVAILABLE, true),
    INTERNAL_SERVER_ERROR(ProblemTypeCategory.COMMON, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, false),
    BAD_REQUEST(ProblemTypeCategory.COMMON, "Bad Request", HttpStatus.BAD_REQUEST, true),
    UNAUTHORIZED(ProblemTypeCategory.COMMON, "Unauthorized", HttpStatus.UNAUTHORIZED, true),
    FORBIDDEN(ProblemTypeCategory.COMMON, "Forbidden", HttpStatus.FORBIDDEN, true),
    RATE_LIMIT_EXCEEDED(ProblemTypeCategory.COMMON, "Rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS, true)
    ;

    private final ProblemTypeCategory category;
    private final String title;
    private final HttpStatus status;
    private final boolean retryable;

    ErrorCode(ProblemTypeCategory category, String title, HttpStatus status, boolean retryable) {
        this.category = category;
        this.title = title;
        this.status = status;
        this.retryable = retryable;
    }

}
