package com.otilm.api.model.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Holds the constructor signatures core builds this class with, now that the trigger facts have grown the all-args
 * constructor: each call below compiles only while the previous signature exists, and states that the facts stay
 * unreported when nobody supplies them.
 */
class SchedulerJobDtoConstructorCompatibilityTest {

    @Test
    void aJobBuiltWithoutAUuidReportsNoTriggerFacts() {
        SchedulerJobDto job = new SchedulerJobDto("job", "0 30 * ? * *", "com.otilm.core.tasks.Task");

        assertEquals("job", job.getJobName());
        assertNull(job.getUuidJob());
        assertNull(job.getTriggerState(), "trigger facts are unreported rather than reported as NONE");
        assertNull(job.getNextFireTime());
        assertNull(job.getPreviousFireTime());
    }

    @Test
    void aJobBuiltWithAUuidReportsNoTriggerFacts() {
        UUID uuid = UUID.randomUUID();

        SchedulerJobDto job = new SchedulerJobDto(uuid, "job", "0 30 * ? * *", "com.otilm.core.tasks.Task");

        assertEquals(uuid, job.getUuidJob());
        assertEquals("0 30 * ? * *", job.getCronExpression());
        assertNull(job.getTriggerState());
    }

    /**
     * What Spring Boot's own mapper writes, so the scheduler's answer is what core's client reads back. Boot's
     * auto-configuration turns {@code WRITE_DATES_AS_TIMESTAMPS} off; the bare builder leaves it on.
     */
    @Test
    void fireTimesTravelAsIsoInstants() throws Exception {
        ObjectMapper mapper = Jackson2ObjectMapperBuilder
                .json()
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        SchedulerJobDto job = new SchedulerJobDto("job", "0 30 * ? * *", "com.otilm.core.tasks.Task");
        job.setNextFireTime(Instant.parse("2026-09-29T12:30:00Z"));
        job.setTriggerState(SchedulerTriggerState.NORMAL);

        String json = mapper.writeValueAsString(job);
        SchedulerJobDto back = mapper.readValue(json, SchedulerJobDto.class);

        assertTrue(json.contains("\"nextFireTime\":\"2026-09-29T12:30:00Z\""), json);
        assertTrue(json.contains("\"triggerState\":\"NORMAL\""), json);
        assertEquals(Instant.parse("2026-09-29T12:30:00Z"), back.getNextFireTime());
        assertEquals(SchedulerTriggerState.NORMAL, back.getTriggerState());
    }
}
