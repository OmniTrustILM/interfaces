package com.otilm.api.model.common.error;

import com.otilm.api.model.client.connector.v2.ConnectorInterface;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Schema(enumAsRef = true)
public enum ErrorCode {
    VALIDATION_FAILED(ProblemTypeCategory.COMMON, null, "Validation failed", HttpStatus.UNPROCESSABLE_CONTENT, false),
    RESOURCE_NOT_FOUND(ProblemTypeCategory.COMMON, null, "Resource not found", HttpStatus.NOT_FOUND, false),
    RESOURCE_ALREADY_EXISTS(ProblemTypeCategory.COMMON, null, "Resource already exists", HttpStatus.CONFLICT, false),
    REQUEST_TIMEOUT(ProblemTypeCategory.COMMON, null, "Request timeout", HttpStatus.REQUEST_TIMEOUT, true),
    OPERATION_NOT_SUPPORTED(ProblemTypeCategory.COMMON, null, "Operation not supported", HttpStatus.NOT_IMPLEMENTED,
            false),
    ATTRIBUTES_ERROR(ProblemTypeCategory.COMMON, null, "Attributes handling error", HttpStatus.BAD_REQUEST, false),
    SERVICE_UNAVAILABLE(ProblemTypeCategory.COMMON, null, "Service unavailable", HttpStatus.SERVICE_UNAVAILABLE, true),
    INTERNAL_SERVER_ERROR(ProblemTypeCategory.COMMON, null, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR,
            false),
    BAD_REQUEST(ProblemTypeCategory.COMMON, null, "Bad Request", HttpStatus.BAD_REQUEST, false),
    UNAUTHORIZED(ProblemTypeCategory.COMMON, null, "Unauthorized", HttpStatus.UNAUTHORIZED, false),
    FORBIDDEN(ProblemTypeCategory.COMMON, null, "Forbidden", HttpStatus.FORBIDDEN, false),
    RATE_LIMIT_EXCEEDED(ProblemTypeCategory.COMMON, null, "Rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS, true),
    GATEWAY_TIMEOUT(ProblemTypeCategory.COMMON, null, "Gateway timeout", HttpStatus.GATEWAY_TIMEOUT, true),

    // CONNECTOR general — cross-interface
    UPSTREAM_ERROR(ProblemTypeCategory.CONNECTOR, null, "Upstream system returned an error", HttpStatus.BAD_GATEWAY,
            false),
    CREDENTIAL_INVALID(ProblemTypeCategory.CONNECTOR, null, "Credentials invalid for upstream system",
            HttpStatus.UNAUTHORIZED, false),
    POLICY_VIOLATION(ProblemTypeCategory.CONNECTOR, null, "Policy violation at upstream system",
            HttpStatus.UNPROCESSABLE_CONTENT, false),
    OPERATION_PAST_POINT_OF_NO_RETURN(ProblemTypeCategory.CONNECTOR, null,
            "Cancel refused — operation past point of no return", HttpStatus.UNPROCESSABLE_CONTENT, false),
    OPERATION_NOT_TRACKED(ProblemTypeCategory.CONNECTOR, null, "Async operation no longer tracked by connector",
            HttpStatus.NOT_FOUND, false),
    // Distinct from RESOURCE_NOT_FOUND: Core handles this internally — it refreshes its
    // attribute-definition registry from the connector and retries the operation transparently,
    // rather than surfacing the failure to the API caller. Not client-retryable (retryable=false).
    ATTRIBUTE_DEFINITION_NOT_FOUND(ProblemTypeCategory.CONNECTOR, null, "Attribute definition not found",
            HttpStatus.NOT_FOUND, false),

    // Document handling — raised by the content-signing formatting contract.
    DOCUMENT_MALFORMED(ProblemTypeCategory.CONNECTOR, null, "Document cannot be parsed as the declared format",
            HttpStatus.UNPROCESSABLE_CONTENT, false),
    DOCUMENT_TOO_LARGE(ProblemTypeCategory.CONNECTOR, null, "Document exceeds the size the connector accepts",
            HttpStatus.CONTENT_TOO_LARGE, false),
    SIGNATURE_NOT_FOUND(ProblemTypeCategory.CONNECTOR, null, "Document contains no signature this operation can act on",
            HttpStatus.UNPROCESSABLE_CONTENT, false),
    PARAMETER_UNSUPPORTED(ProblemTypeCategory.CONNECTOR, null,
            "Requested parameter or signature family is not supported by this connector",
            HttpStatus.UNPROCESSABLE_CONTENT, false),

    // Replayed-context divergence. Distinct from VALIDATION_FAILED: the body is well formed, so no field
    // rule is what failed. It says the platform's own state moved between a compute step and the embed
    // step that replays its context, which is a condition an operator can alert on. It stays 422 because
    // the connector cannot complete the operation as asked.
    CONTEXT_MISMATCH(ProblemTypeCategory.CONNECTOR, null, "Request contradicts the context it replays",
            HttpStatus.UNPROCESSABLE_CONTENT, false),

    // CONNECTOR + AUTHORITY — interface-specific
    CSR_MALFORMED(ProblemTypeCategory.CONNECTOR, ConnectorInterface.AUTHORITY, "CSR malformed",
            HttpStatus.UNPROCESSABLE_CONTENT, false),
    REVOCATION_NOT_ALLOWED(ProblemTypeCategory.CONNECTOR, ConnectorInterface.AUTHORITY,
            "Revocation not allowed by upstream CA", HttpStatus.UNPROCESSABLE_CONTENT, false),
    REGISTRATION_NOT_FOUND(ProblemTypeCategory.CONNECTOR, ConnectorInterface.AUTHORITY,
            "Pre-registration reference not tracked by upstream CA", HttpStatus.UNPROCESSABLE_CONTENT, false),
    RENEWAL_SOURCE_NOT_FOUND(ProblemTypeCategory.CONNECTOR, ConnectorInterface.AUTHORITY,
            "Source certificate for renewal not found at upstream CA", HttpStatus.NOT_FOUND, false),
    CSR_SUBJECT_MISMATCH(ProblemTypeCategory.CONNECTOR, ConnectorInterface.AUTHORITY,
            "CSR subject does not match the pre-registration", HttpStatus.UNPROCESSABLE_CONTENT, false),
    CERTIFICATE_MISMATCH(ProblemTypeCategory.CONNECTOR, ConnectorInterface.AUTHORITY,
            "Certificate belongs to a different authority than requested", HttpStatus.UNPROCESSABLE_CONTENT, false),

    // CONNECTOR + DISCOVERY — interface-specific
    CHECKPOINT_LOST(ProblemTypeCategory.CONNECTOR, ConnectorInterface.DISCOVERY,
            "Discovery checkpoint lost — run cannot be resumed", HttpStatus.GONE, false),

    // CONNECTOR + CRYPTOGRAPHY — interface-specific
    KEY_TYPE_NOT_IMPORTABLE(ProblemTypeCategory.CONNECTOR, ConnectorInterface.CRYPTOGRAPHY,
            "Key type or algorithm cannot be imported into this token", HttpStatus.UNPROCESSABLE_CONTENT, false),
    KEY_TYPE_NOT_EXPORTABLE(ProblemTypeCategory.CONNECTOR, ConnectorInterface.CRYPTOGRAPHY,
            "Key type or algorithm cannot be exported from this token", HttpStatus.UNPROCESSABLE_CONTENT, false),
    KEY_MATERIAL_MISMATCH(ProblemTypeCategory.CONNECTOR, ConnectorInterface.CRYPTOGRAPHY,
            "Key material does not match the declared key type or algorithm", HttpStatus.UNPROCESSABLE_CONTENT, false),
    KEY_DECRYPTION_FAILED(ProblemTypeCategory.CONNECTOR, ConnectorInterface.CRYPTOGRAPHY,
            "Key material could not be decrypted with the supplied passphrase", HttpStatus.UNPROCESSABLE_CONTENT,
            false),
    EXPORTABLE_NOT_SUPPORTED(ProblemTypeCategory.CONNECTOR, ConnectorInterface.CRYPTOGRAPHY,
            "Token cannot hold a key that stays exportable", HttpStatus.UNPROCESSABLE_CONTENT, false),
    KEY_NOT_EXPORTABLE(ProblemTypeCategory.CONNECTOR, ConnectorInterface.CRYPTOGRAPHY,
            "Key was not created or imported as exportable", HttpStatus.UNPROCESSABLE_CONTENT, false);

    private final ProblemTypeCategory category;

    @Schema(description = "Provider interface this code is specific to; null for COMMON and CONNECTOR-general codes")
    private final ConnectorInterface interfaceCode;

    private final String title;
    private final HttpStatus status;
    private final boolean retryable;

    ErrorCode(ProblemTypeCategory category, ConnectorInterface interfaceCode, String title, HttpStatus status,
            boolean retryable) {
        this.category = category;
        this.interfaceCode = interfaceCode;
        this.title = title;
        this.status = status;
        this.retryable = retryable;
    }

}
