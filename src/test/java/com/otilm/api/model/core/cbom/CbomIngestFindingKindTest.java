package com.otilm.api.model.core.cbom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CbomIngestFindingKindTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void findByCode_resolvesWireCode() {
        Assertions.assertEquals(CbomIngestFindingKind.FINDING, CbomIngestFindingKind.findByCode("finding"));
        Assertions.assertEquals(CbomIngestFindingKind.SKIP, CbomIngestFindingKind.findByCode("skip"));
    }

    @Test
    void findByCode_rejectsUnknownCode() {
        Assertions.assertThrows(ValidationException.class, () -> CbomIngestFindingKind.findByCode("FINDING"));
    }

    @Test
    void serializesToWireCode() throws Exception {
        Assertions.assertEquals("\"skip\"", mapper.writeValueAsString(CbomIngestFindingKind.SKIP));
    }

    @Test
    void deserializesFromWireCode() throws Exception {
        Assertions
                .assertEquals(CbomIngestFindingKind.FINDING,
                        mapper.readValue("\"finding\"", CbomIngestFindingKind.class));
    }
}
