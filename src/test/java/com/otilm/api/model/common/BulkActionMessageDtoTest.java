package com.otilm.api.model.common;

import com.otilm.api.exception.PlatformException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BulkActionMessageDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void roundTrip_serializesAndDeserializesAllFields() throws Exception {
        BulkActionMessageDto original = BulkActionMessageDto.failureWithMessage(
                "uuid-42", "Widget", "Object is associated with other items");

        String json = mapper.writeValueAsString(original);
        BulkActionMessageDto restored = mapper.readValue(json, BulkActionMessageDto.class);

        assertEquals(original.getUuid(), restored.getUuid());
        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getMessage(), restored.getMessage());
    }


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
    void failure_propagatesUuidAndName_forNonPlatformException() {
        BulkActionMessageDto dto = BulkActionMessageDto.failure("abc", "Widget",
                new RuntimeException(), "err");
        assertEquals("abc", dto.getUuid());
        assertEquals("Widget", dto.getName());
    }

    @Test
    void failure_propagatesUuidAndName_forPlatformException() {
        BulkActionMessageDto dto = BulkActionMessageDto.failure("abc", "Widget",
                new DomainException("domain msg"), "fallback");
        assertEquals("abc", dto.getUuid());
        assertEquals("Widget", dto.getName());
    }
}
