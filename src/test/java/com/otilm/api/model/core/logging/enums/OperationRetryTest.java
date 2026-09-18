package com.otilm.api.model.core.logging.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** The audit verb Core writes for an operator's retry of a CBOM sync skip; the code is what audit-log filters use. */
class OperationRetryTest {

    @Test
    void theRetryVerbKeepsItsWireCodeAndSaysWhatIsRetried() {
        assertEquals("retry", Operation.RETRY.getCode());
        assertEquals("Retry", Operation.RETRY.getLabel());
        assertFalse(Operation.RETRY.getDescription().isBlank(), "an operator reads what is retried");
    }
}
