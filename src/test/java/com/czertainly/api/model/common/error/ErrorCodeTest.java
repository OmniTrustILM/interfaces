package com.czertainly.api.model.common.error;

import com.czertainly.api.model.client.connector.v2.ConnectorInterface;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class ErrorCodeTest {

    @Test
    void existingCommonEntriesHaveNullInterfaceCode() {
        assertNull(ErrorCode.VALIDATION_FAILED.getInterfaceCode());
        assertNull(ErrorCode.RESOURCE_NOT_FOUND.getInterfaceCode());
        assertNull(ErrorCode.INTERNAL_SERVER_ERROR.getInterfaceCode());
    }
}
