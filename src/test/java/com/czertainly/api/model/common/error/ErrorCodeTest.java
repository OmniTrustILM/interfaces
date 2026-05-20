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
