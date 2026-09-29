package com.otilm.api.model.core.cryptoasset;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PqcExplanationStepOutcomeTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void findByCode_resolvesWireCode() {
        Assertions
                .assertEquals(PqcExplanationStepOutcome.NOT_MATCHED,
                        PqcExplanationStepOutcome.findByCode("notMatched"));
        Assertions.assertEquals(PqcExplanationStepOutcome.DECIDED, PqcExplanationStepOutcome.findByCode("decided"));
        Assertions
                .assertEquals(PqcExplanationStepOutcome.NOT_REACHED,
                        PqcExplanationStepOutcome.findByCode("notReached"));
        Assertions.assertEquals(PqcExplanationStepOutcome.RESOLVED, PqcExplanationStepOutcome.findByCode("resolved"));
    }

    @Test
    void findByCode_rejectsUnknownCode() {
        Assertions.assertThrows(ValidationException.class, () -> PqcExplanationStepOutcome.findByCode("skipped"));
    }

    @Test
    void serializesToWireCode() throws Exception {
        Assertions.assertEquals("\"notMatched\"", mapper.writeValueAsString(PqcExplanationStepOutcome.NOT_MATCHED));
        Assertions.assertEquals("\"notReached\"", mapper.writeValueAsString(PqcExplanationStepOutcome.NOT_REACHED));
    }

    @Test
    void deserializesFromWireCode() throws Exception {
        Assertions
                .assertEquals(PqcExplanationStepOutcome.RESOLVED,
                        mapper.readValue("\"resolved\"", PqcExplanationStepOutcome.class));
    }
}
