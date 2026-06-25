package com.otilm.api.model.common.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttributeDefinitionErrorCodeTest {

    @Test
    void attributeDefinitionNotFound_isConnectorGeneral404() {
        ErrorCode code = ErrorCode.ATTRIBUTE_DEFINITION_NOT_FOUND;
        assertEquals(ProblemTypeCategory.CONNECTOR, code.getCategory());
        assertNull(code.getInterfaceCode(), "CONNECTOR-general code must not be interface-specific");
        assertEquals(HttpStatus.NOT_FOUND, code.getStatus());
        assertFalse(code.isRetryable());
    }

    @Test
    void attributeDefinitionNotFound_uriIsTwoSegment() {
        URI type = ProblemDetailExtended
                .fromErrorCode(ErrorCode.ATTRIBUTE_DEFINITION_NOT_FOUND, "x", null, null)
                .getType();
        assertEquals("https://docs.otilm.com/problems/connector/ATTRIBUTE_DEFINITION_NOT_FOUND",
                type.toString());
    }

    @Test
    void valuesOf_stillResolvesAllCodes() {
        // adding the new entry must not break enum iteration / valueOf
        assertTrue(ErrorCode.values().length > 1);
        assertEquals(ErrorCode.ATTRIBUTE_DEFINITION_NOT_FOUND,
                ErrorCode.valueOf("ATTRIBUTE_DEFINITION_NOT_FOUND"));
    }
}
