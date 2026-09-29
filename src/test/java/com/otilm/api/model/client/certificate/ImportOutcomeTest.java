package com.otilm.api.model.client.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The codes are what a generated client sends and receives, so they are part of the contract rather than an
 * implementation detail of the enum.
 */
class ImportOutcomeTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @ParameterizedTest
    @EnumSource(ImportOutcome.class)
    void everyOutcomeRoundTripsThroughItsCode(ImportOutcome outcome) throws Exception {
        // given
        String json = MAPPER.writeValueAsString(outcome);

        // when
        ImportOutcome read = MAPPER.readValue(json, ImportOutcome.class);

        // then
        assertEquals('"' + outcome.getCode() + '"', json);
        assertSame(outcome, read);
        assertSame(outcome, ImportOutcome.findByCode(outcome.getCode()));
    }

    @ParameterizedTest
    @EnumSource(ImportOutcome.class)
    void everyOutcomeIsDescribedForSomeoneReadingAResult(ImportOutcome outcome) {
        // given
        // when
        // then
        assertFalse(outcome.getLabel().isBlank(), outcome.name());
        assertFalse(outcome.getDescription().isBlank(), outcome.name());
    }

    @Test
    void refusesACodeItDoesNotDefine() {
        // given
        String unknown = "reused";

        // when
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> ImportOutcome.findByCode(unknown));

        // then
        assertEquals("Unknown import outcome " + unknown, failure.getMessage());
    }

    /**
     * The codes are part of the wire contract, so the set and their casing are pinned rather than left to enum order.
     */
    @Test
    void publishesExactlyTheCodesTheContractDefines() {
        assertEquals(List.of("created", "existing", "adopted"),
                Arrays.stream(ImportOutcome.values()).map(ImportOutcome::getCode).toList());
    }
}
