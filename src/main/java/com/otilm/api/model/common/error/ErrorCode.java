package com.otilm.api.model.common.error;

import com.otilm.api.model.client.connector.v2.ConnectorInterface;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Schema(enumAsRef = true)
public enum ErrorCode {
    VALIDATION_FAILED(ProblemTypeCategory.COMMON, null, "Validation failed", HttpStatus.UNPROCESSABLE_ENTITY, false),
    RESOURCE_NOT_FOUND(ProblemTypeCategory.COMMON, null, "Resource not found", HttpStatus.NOT_FOUND, false),
    RESOURCE_ALREADY_EXISTS(ProblemTypeCategory.COMMON, null, "Resource already exists", HttpStatus.CONFLICT, false),
    REQUEST_TIMEOUT(ProblemTypeCategory.COMMON, null, "Request timeout", HttpStatus.REQUEST_TIMEOUT, true),
    OPERATION_NOT_SUPPORTED(ProblemTypeCategory.COMMON, null, "Operation not supported", HttpStatus.NOT_IMPLEMENTED, false),
    ATTRIBUTES_ERROR(ProblemTypeCategory.COMMON, null, "Attributes handling error", HttpStatus.BAD_REQUEST, false),
    SERVICE_UNAVAILABLE(ProblemTypeCategory.COMMON, null, "Service unavailable", HttpStatus.SERVICE_UNAVAILABLE, true),
    INTERNAL_SERVER_ERROR(ProblemTypeCategory.COMMON, null, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, false),
    BAD_REQUEST(ProblemTypeCategory.COMMON, null, "Bad Request", HttpStatus.BAD_REQUEST, false),
    UNAUTHORIZED(ProblemTypeCategory.COMMON, null, "Unauthorized", HttpStatus.UNAUTHORIZED, false),
    FORBIDDEN(ProblemTypeCategory.COMMON, null, "Forbidden", HttpStatus.FORBIDDEN, false),
    RATE_LIMIT_EXCEEDED(ProblemTypeCategory.COMMON, null, "Rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS, true),
    GATEWAY_TIMEOUT(ProblemTypeCategory.COMMON, null, "Gateway timeout", HttpStatus.GATEWAY_TIMEOUT, true),

    // CONNECTOR general — cross-interface
    UPSTREAM_ERROR(ProblemTypeCategory.CONNECTOR, null, "Upstream system returned an error", HttpStatus.BAD_GATEWAY, false),
    CREDENTIAL_INVALID(ProblemTypeCategory.CONNECTOR, null, "Credentials invalid for upstream system", HttpStatus.UNAUTHORIZED, false),
    POLICY_VIOLATION(ProblemTypeCategory.CONNECTOR, null, "Policy violation at upstream system", HttpStatus.UNPROCESSABLE_ENTITY, false),
    OPERATION_PAST_POINT_OF_NO_RETURN(ProblemTypeCategory.CONNECTOR, null, "Cancel refused — operation past point of no return", HttpStatus.UNPROCESSABLE_ENTITY, false),
    OPERATION_NOT_TRACKED(ProblemTypeCategory.CONNECTOR, null, "Async operation no longer tracked by connector", HttpStatus.NOT_FOUND, false),
    // Distinct from RESOURCE_NOT_FOUND: Core reacts by refreshing its attribute-definition registry
    // from the connector and retrying, rather than surfacing a hard failure to the caller.
    ATTRIBUTE_DEFINITION_NOT_FOUND(ProblemTypeCategory.CONNECTOR, null, "Attribute definition not found", HttpStatus.NOT_FOUND, false),

    // CONNECTOR + AUTHORITY — interface-specific
    CSR_MALFORMED(ProblemTypeCategory.CONNECTOR, ConnectorInterface.AUTHORITY, "CSR malformed", HttpStatus.UNPROCESSABLE_ENTITY, false),
    REVOCATION_NOT_ALLOWED(ProblemTypeCategory.CONNECTOR, ConnectorInterface.AUTHORITY, "Revocation not allowed by upstream CA", HttpStatus.UNPROCESSABLE_ENTITY, false),
    REGISTRATION_NOT_FOUND(ProblemTypeCategory.CONNECTOR, ConnectorInterface.AUTHORITY, "Pre-registration reference not tracked by upstream CA", HttpStatus.UNPROCESSABLE_ENTITY, false),
    RENEWAL_SOURCE_NOT_FOUND(ProblemTypeCategory.CONNECTOR, ConnectorInterface.AUTHORITY, "Source certificate for renewal not found at upstream CA", HttpStatus.NOT_FOUND, false),
    CSR_SUBJECT_MISMATCH(ProblemTypeCategory.CONNECTOR, ConnectorInterface.AUTHORITY, "CSR subject does not match the pre-registration", HttpStatus.UNPROCESSABLE_ENTITY, false),
    CERTIFICATE_MISMATCH(ProblemTypeCategory.CONNECTOR, ConnectorInterface.AUTHORITY, "Certificate belongs to a different authority than requested", HttpStatus.UNPROCESSABLE_ENTITY, false)
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
