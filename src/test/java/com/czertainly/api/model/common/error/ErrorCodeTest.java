package com.czertainly.api.model.common.error;

import com.czertainly.api.model.client.connector.v2.ConnectorInterface;
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
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.POLICY_VIOLATION.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.OPERATION_PAST_POINT_OF_NO_RETURN.getStatus());
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
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.CSR_MALFORMED.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.REVOCATION_NOT_ALLOWED.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.REGISTRATION_NOT_FOUND.getStatus());
        assertEquals(HttpStatus.NOT_FOUND, ErrorCode.RENEWAL_SOURCE_NOT_FOUND.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.CSR_SUBJECT_MISMATCH.getStatus());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, ErrorCode.CERTIFICATE_MISMATCH.getStatus());
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
    }
}
