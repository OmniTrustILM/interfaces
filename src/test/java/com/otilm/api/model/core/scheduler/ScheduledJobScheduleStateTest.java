package com.otilm.api.model.core.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduledJobScheduleStateTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    /** The codes are the wire contract generated clients switch on; renaming one breaks them silently. */
    @Test
    void theWireCodesAreFixed() {
        assertEquals(7, ScheduledJobScheduleState.values().length, "a new state needs its code pinned here");
        assertEquals("scheduled", ScheduledJobScheduleState.SCHEDULED.getCode());
        assertEquals("paused", ScheduledJobScheduleState.PAUSED.getCode());
        assertEquals("blocked", ScheduledJobScheduleState.BLOCKED.getCode());
        assertEquals("error", ScheduledJobScheduleState.ERROR.getCode());
        assertEquals("complete", ScheduledJobScheduleState.COMPLETE.getCode());
        assertEquals("notScheduled", ScheduledJobScheduleState.NOT_SCHEDULED.getCode());
        assertEquals("unknown", ScheduledJobScheduleState.UNKNOWN.getCode());
    }

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
        ScheduledJobDto back = mapper.readValue(json, ScheduledJobDto.class);

        assertTrue(json.contains("\"scheduleState\":\"notScheduled\""), json);
        assertTrue(json.contains("\"lastSkipReason\":\"No stale cryptographic asset to re-evaluate\""), json);
        assertEquals(ScheduledJobScheduleState.NOT_SCHEDULED, back.getScheduleState());
        assertEquals(Instant.parse("2026-09-29T11:30:00Z"), back.getLastSkippedAt());
        assertEquals("No stale cryptographic asset to re-evaluate", back.getLastSkipReason());
    }
}
