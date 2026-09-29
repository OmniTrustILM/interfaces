package com.otilm.api.model.core.logging.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** The audit verb Core writes when a file is read to report what it holds, before anything is imported. */
class OperationInspectTest {

    @Test
    void theInspectVerbRoundTripsThroughItsWireCode() {
        assertEquals(Operation.INSPECT, Operation.findByCode("inspect"));
        assertEquals("inspect", Operation.INSPECT.getCode());
        assertEquals("Inspect", Operation.INSPECT.getLabel());
    }
}
