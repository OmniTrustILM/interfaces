package com.otilm.api.model.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SchedulerJobExecutionStatusTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void aRunSomeoneCancelledHasAStatusOfItsOwn() throws Exception {
        assertEquals("\"cancelled\"", mapper.writeValueAsString(SchedulerJobExecutionStatus.CANCELLED));
        assertEquals(SchedulerJobExecutionStatus.CANCELLED,
                mapper.readValue("\"cancelled\"", SchedulerJobExecutionStatus.class));
        assertEquals("Cancelled", SchedulerJobExecutionStatus.CANCELLED.getLabel());
    }
}
