package com.otilm.api.model.core.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduledJobScheduleStateTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void everyStateRoundTripsThroughItsCode() {
        for (ScheduledJobScheduleState state : ScheduledJobScheduleState.values()) {
            assertEquals(state, ScheduledJobScheduleState.fromCode(state.getCode()));
        }
    }

    @Test
    void anUnknownCodeIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> ScheduledJobScheduleState.fromCode("running"));
    }

    @Test
    void theStateIsWrittenAsItsCode() throws Exception {
        ScheduledJobDto dto = new ScheduledJobDto();
        dto.setScheduleState(ScheduledJobScheduleState.NOT_SCHEDULED);
        dto.setLastSkippedAt(Instant.parse("2026-09-29T11:30:00Z"));
        dto.setLastSkipReason("No stale cryptographic asset to re-evaluate");

        String json = mapper.writeValueAsString(dto);

        assertTrue(json.contains("\"scheduleState\":\"notScheduled\""), json);
        assertTrue(json.contains("\"lastSkipReason\":\"No stale cryptographic asset to re-evaluate\""), json);
        assertEquals(ScheduledJobScheduleState.NOT_SCHEDULED,
                mapper.readValue(json, ScheduledJobDto.class).getScheduleState());
    }
}
