package com.otilm.api.model.common.error;

import com.otilm.api.model.client.connector.v2.ConnectorInterface;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorCodeTest {

    @Test
    void existingCommonEntriesHaveNullInterfaceCode() {
        assertNull(ErrorCode.VALIDATION_FAILED.getInterfaceCode());
        assertNull(ErrorCode.RESOURCE_NOT_FOUND.getInterfaceCode());
        assertNull(ErrorCode.INTERNAL_SERVER_ERROR.getInterfaceCode());
    }

    @Test
    void gatewayTimeoutEntryExists() {
        assertEquals(HttpStatus.GATEWAY_TIMEOUT, ErrorCode.GATEWAY_TIMEOUT.getStatus());
        assertTrue(ErrorCode.GATEWAY_TIMEOUT.isRetryable());
        assertEquals(ProblemTypeCategory.COMMON, ErrorCode.GATEWAY_TIMEOUT.getCategory());
        assertNull(ErrorCode.GATEWAY_TIMEOUT.getInterfaceCode());
    }

    @Test
    void connectorGeneralEntries() {
        for (ErrorCode code : new ErrorCode[]{
                ErrorCode.UPSTREAM_ERROR,
                ErrorCode.CREDENTIAL_INVALID,
                ErrorCode.POLICY_VIOLATION,
                ErrorCode.OPERATION_PAST_POINT_OF_NO_RETURN,
                ErrorCode.OPERATION_NOT_TRACKED}) {
            assertEquals(ProblemTypeCategory.CONNECTOR, code.getCategory(), code.name() + " category");
            assertNull(code.getInterfaceCode(), code.name() + " interfaceCode");
            assertFalse(code.isRetryable(), code.name() + " retryable");
        }
        assertEquals(HttpStatus.BAD_GATEWAY, ErrorCode.UPSTREAM_ERROR.getStatus());
        assertEquals(HttpStatus.UNAUTHORIZED, ErrorCode.CREDENTIAL_INVALID.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCode.POLICY_VIOLATION.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCode.OPERATION_PAST_POINT_OF_NO_RETURN.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, ErrorCode.OPERATION_NOT_TRACKED.getStatus());
    }

    @Test
    void connectorAuthorityEntries() {
        for (ErrorCode code : new ErrorCode[]{
                ErrorCode.CSR_MALFORMED,
                ErrorCode.REVOCATION_NOT_ALLOWED,
                ErrorCode.REGISTRATION_NOT_FOUND,
                ErrorCode.RENEWAL_SOURCE_NOT_FOUND,
                ErrorCode.CSR_SUBJECT_MISMATCH,
                ErrorCode.CERTIFICATE_MISMATCH}) {
            assertEquals(ProblemTypeCategory.CONNECTOR, code.getCategory(), code.name() + " category");
            assertEquals(ConnectorInterface.AUTHORITY, code.getInterfaceCode(), code.name() + " interfaceCode");
            assertFalse(code.isRetryable(), code.name() + " retryable");
        }
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCode.CSR_MALFORMED.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCode.REVOCATION_NOT_ALLOWED.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCode.REGISTRATION_NOT_FOUND.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, ErrorCode.RENEWAL_SOURCE_NOT_FOUND.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCode.CSR_SUBJECT_MISMATCH.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCode.CERTIFICATE_MISMATCH.getStatus());
    }

    @Test
    void connectorDiscoveryEntries() {
        assertEquals(ProblemTypeCategory.CONNECTOR, ErrorCode.CHECKPOINT_LOST.getCategory());
        assertEquals(ConnectorInterface.DISCOVERY, ErrorCode.CHECKPOINT_LOST.getInterfaceCode());
        assertEquals(HttpStatus.GONE, ErrorCode.CHECKPOINT_LOST.getStatus());
        assertFalse(ErrorCode.CHECKPOINT_LOST.isRetryable());
    }

    @Test
    void connectorCryptographyEntries() {
        for (ErrorCode code : List
                .of(ErrorCode.KEY_TYPE_NOT_IMPORTABLE, ErrorCode.KEY_TYPE_NOT_EXPORTABLE,
                        ErrorCode.KEY_MATERIAL_MISMATCH, ErrorCode.KEY_DECRYPTION_FAILED,
                        ErrorCode.EXPORTABLE_NOT_SUPPORTED, ErrorCode.KEY_NOT_EXPORTABLE)) {
            assertEquals(ProblemTypeCategory.CONNECTOR, code.getCategory(), code.name());
            assertEquals(ConnectorInterface.CRYPTOGRAPHY, code.getInterfaceCode(), code.name());
            assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, code.getStatus(), code.name());
            assertFalse(code.isRetryable(), code.name());
        }
    }

    @Test
    void connectorDocumentHandlingEntries() {
        ErrorCode[] documentCodes = {
                ErrorCode.DOCUMENT_MALFORMED,
                ErrorCode.DOCUMENT_TOO_LARGE,
                ErrorCode.SIGNATURE_NOT_FOUND,
                ErrorCode.PARAMETER_UNSUPPORTED};

        for (ErrorCode code : documentCodes) {
            assertEquals(ProblemTypeCategory.CONNECTOR, code.getCategory(), code.name() + " category");
            assertNull(code.getInterfaceCode(), code.name() + " interfaceCode");
            assertFalse(code.isRetryable(), code.name() + " retryable");
        }

        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCode.DOCUMENT_MALFORMED.getStatus());
        assertEquals(HttpStatus.CONTENT_TOO_LARGE, ErrorCode.DOCUMENT_TOO_LARGE.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCode.SIGNATURE_NOT_FOUND.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCode.PARAMETER_UNSUPPORTED.getStatus());
    }

    @Test
    void connectorContextMismatchEntry() {
        assertEquals(ProblemTypeCategory.CONNECTOR, ErrorCode.CONTEXT_MISMATCH.getCategory());
        assertNull(ErrorCode.CONTEXT_MISMATCH.getInterfaceCode());
        assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ErrorCode.CONTEXT_MISMATCH.getStatus());
        assertFalse(ErrorCode.CONTEXT_MISMATCH.isRetryable());
    }

    @Test
    void retryableTrueOnlyForTransientCodes() {
        // Transient infrastructure / rate-limit recovery → retryable
        assertTrue(ErrorCode.REQUEST_TIMEOUT.isRetryable());
        assertTrue(ErrorCode.SERVICE_UNAVAILABLE.isRetryable());
        assertTrue(ErrorCode.GATEWAY_TIMEOUT.isRetryable());
        assertTrue(ErrorCode.RATE_LIMIT_EXCEEDED.isRetryable());

        // Everything else → not retryable
        assertFalse(ErrorCode.VALIDATION_FAILED.isRetryable());
        assertFalse(ErrorCode.RESOURCE_NOT_FOUND.isRetryable());
        assertFalse(ErrorCode.RESOURCE_ALREADY_EXISTS.isRetryable());
        assertFalse(ErrorCode.OPERATION_NOT_SUPPORTED.isRetryable());
        assertFalse(ErrorCode.ATTRIBUTES_ERROR.isRetryable());
        assertFalse(ErrorCode.INTERNAL_SERVER_ERROR.isRetryable());
        assertFalse(ErrorCode.BAD_REQUEST.isRetryable());
        assertFalse(ErrorCode.UNAUTHORIZED.isRetryable());
        assertFalse(ErrorCode.FORBIDDEN.isRetryable());
        assertFalse(ErrorCode.CHECKPOINT_LOST.isRetryable());
        assertFalse(ErrorCode.DOCUMENT_MALFORMED.isRetryable());
        assertFalse(ErrorCode.DOCUMENT_TOO_LARGE.isRetryable());
        assertFalse(ErrorCode.SIGNATURE_NOT_FOUND.isRetryable());
        assertFalse(ErrorCode.PARAMETER_UNSUPPORTED.isRetryable());
        assertFalse(ErrorCode.CONTEXT_MISMATCH.isRetryable());
    }

    @Test
    void noCodeIsDeclaredWithADeprecatedHttpStatus() throws NoSuchFieldException {
        for (ErrorCode code : ErrorCode.values()) {
            assertFalse(HttpStatus.class.getField(code.getStatus().name()).isAnnotationPresent(Deprecated.class),
                    code.name());
        }
    }
}
