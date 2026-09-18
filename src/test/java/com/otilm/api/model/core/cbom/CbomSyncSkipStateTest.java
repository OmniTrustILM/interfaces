package com.otilm.api.model.core.cbom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CbomSyncSkipStateTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void findByCode_resolvesWireCode() {
        Assertions.assertEquals(CbomSyncSkipState.RETRYING, CbomSyncSkipState.findByCode("retrying"));
        Assertions
                .assertEquals(CbomSyncSkipState.PERMANENTLY_SKIPPED,
                        CbomSyncSkipState.findByCode("permanentlySkipped"));
    }

    @Test
    void findByCode_rejectsUnknownCode() {
        Assertions.assertThrows(ValidationException.class, () -> CbomSyncSkipState.findByCode("skipped"));
    }

    @Test
    void serializesToWireCode() throws Exception {
        Assertions
                .assertEquals("\"permanentlySkipped\"",
                        mapper.writeValueAsString(CbomSyncSkipState.PERMANENTLY_SKIPPED));
    }

    @Test
    void deserializesFromWireCode() throws Exception {
        Assertions.assertEquals(CbomSyncSkipState.RETRYING, mapper.readValue("\"retrying\"", CbomSyncSkipState.class));
    }
}
