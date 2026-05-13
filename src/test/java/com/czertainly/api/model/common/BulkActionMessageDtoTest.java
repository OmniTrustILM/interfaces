package com.czertainly.api.model.common;

import com.czertainly.api.exception.PlatformException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BulkActionMessageDtoTest {

    private static class DomainException extends RuntimeException implements PlatformException {
        DomainException(String msg) {
            super(msg);
        }
    }

    @Test
    void failure_returnsDomainExceptionMessage_whenPlatformException() {
        BulkActionMessageDto dto = BulkActionMessageDto.failure("uuid-1", "name-1",
                new DomainException("domain error"), "fallback");
        assertEquals("domain error", dto.getMessage());
    }

    @Test
    void failure_returnsFallback_forNonPlatformException() {
        BulkActionMessageDto dto = BulkActionMessageDto.failure("uuid-1", "name-1",
                new RuntimeException("SQL: SELECT * FROM secrets"), "Operation failed");
        assertEquals("Operation failed", dto.getMessage());
    }

    @Test
    void failure_propagatesUuidAndName() {
        BulkActionMessageDto dto = BulkActionMessageDto.failure("abc", "Widget",
                new RuntimeException(), "err");
        assertEquals("abc", dto.getUuid());
        assertEquals("Widget", dto.getName());
    }
}
