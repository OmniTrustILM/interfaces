package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryErrorEvent;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryHeartbeatEvent;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryProgressEvent;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryResultBatchEvent;
import com.otilm.api.model.connector.discovery.v2.event.DiscoveryStateChangedEvent;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link DiscoveryEvent} is flat: {@code type} is a field of the event object itself, not a wrapper around a nested
 * payload. Every fixture below is therefore a single flat JSON object — no {@code payload} key.
 */
class DiscoveryEventTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();
    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @Test
    void progressLineParsesAndRoundTripsVerbatim() throws Exception {
        // A progress event as a connector emits it on the NDJSON stream. The irregular whitespace
        // is deliberate: parsing must not depend on this module's own serializer formatting.
        String json = "{\"type\": \"progress\",     \"targetsProcessed\": 1200, \"targetsTotal\": 5000, "
                + "\"phase\": \"scan\", \"byResource\": {\"certificates\": {\"processed\": 900}}}";

        DiscoveryEvent event = mapper.readValue(json, DiscoveryEvent.class);

        assertEquals(DiscoveryEventType.PROGRESS, event.getType());
        DiscoveryProgressEvent progress = assertInstanceOf(DiscoveryProgressEvent.class, event);
        assertEquals(1200L, progress.getTargetsProcessed());
        assertEquals(5000L, progress.getTargetsTotal());
        assertEquals("scan", progress.getPhase());
        assertEquals(900L, progress.getByResource().get(Resource.CERTIFICATE).getProcessed());

        String reSerialized = mapper.writeValueAsString(event);
        assertFalse(reSerialized.contains("\n"), "an NDJSON line must not contain an embedded newline");
        assertFalse(reSerialized.contains("\r"), "an NDJSON line must not contain an embedded carriage return");
        assertEquals(1, StringUtils.countMatches(reSerialized, "\"type\""));
        assertTrue(reSerialized.contains("\"type\":\"progress\""));
        // A nested byResource entry is the plain (non-event) DiscoveryProgressDto shape, so it
        // must never carry a type of its own — only the enclosing event is discriminated.
        assertFalse(reSerialized.contains("\"certificates\":{\"type\""),
                "a nested byResource entry must not carry a type field of its own");
    }

    @Test
    void resultBatchLineParsesAndRoundTripsVerbatim() throws Exception {
        // A resultBatch event carrying one real DiscoveredItemDto, whose shape
        // DiscoveredItemDtoTest covers independently. Irregular whitespace again, for the same
        // reason as above.
        String json = "{\"type\": \"resultBatch\",  \"items\": [" + "{\"sequence\": 1, \"uniqueRef\": \"cert-ref-1\", "
                + "\"payload\": {\"resource\": \"certificates\", \"certificateData\": \"Y2VydC1kYXRh\"}}]}";

        DiscoveryEvent event = mapper.readValue(json, DiscoveryEvent.class);

        assertEquals(DiscoveryEventType.RESULT_BATCH, event.getType());
        DiscoveryResultBatchEvent batch = assertInstanceOf(DiscoveryResultBatchEvent.class, event);
        assertEquals(1, batch.getItems().size());
        DiscoveredItemDto item = batch.getItems().get(0);
        assertEquals(1L, item.getSequence());
        assertEquals(Resource.CERTIFICATE, item.getResource());
        assertEquals("cert-ref-1", item.getUniqueRef());
        DiscoveredCertificateDto itemPayload = assertInstanceOf(DiscoveredCertificateDto.class, item.getPayload());
        assertEquals("Y2VydC1kYXRh", itemPayload.getCertificateData());

        String reSerialized = mapper.writeValueAsString(event);
        assertFalse(reSerialized.contains("\n"), "an NDJSON line must not contain an embedded newline");
        assertEquals(1, StringUtils.countMatches(reSerialized, "\"type\""));
        assertTrue(reSerialized.contains("\"type\":\"resultBatch\""));
    }

    @Test
    void resultBatchDoesNotDefaultItemsToAnEmptyList() {
        // An unset items must stay null so @NotNull catches it. A field initializer would make an
        // event that omits items validate as an empty batch instead of being rejected.
        DiscoveryResultBatchEvent batch = new DiscoveryResultBatchEvent();

        assertNull(batch.getItems(), "items must not be defaulted to an empty list");

        Set<ConstraintViolation<DiscoveryResultBatchEvent>> violations = VALIDATOR.validate(batch);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items")),
                "an unset items must fail the @NotNull constraint rather than pass as an empty batch");
    }

    @Test
    void resultBatchOmittingItemsOnTheWireFailsValidation() throws Exception {
        DiscoveryEvent event = mapper.readValue("{\"type\":\"resultBatch\"}", DiscoveryEvent.class);

        DiscoveryResultBatchEvent batch = assertInstanceOf(DiscoveryResultBatchEvent.class, event);
        assertNull(batch.getItems(), "an omitted items must deserialize to null, not to an empty list");

        Set<ConstraintViolation<DiscoveryResultBatchEvent>> violations = VALIDATOR.validate(batch);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items")),
                "an event omitting items must be rejected, not read as an empty batch");
    }

    @Test
    void resultBatchEmptyBatchSerializesItemsAsEmptyArray() throws Exception {
        DiscoveryResultBatchEvent batch = new DiscoveryResultBatchEvent();
        batch.setItems(List.of()); // an empty batch: explicit [], which is what the contract requires

        assertTrue(VALIDATOR.validate(batch).isEmpty(), "an explicit empty batch must be valid");

        String reSerialized = mapper.writeValueAsString(batch);
        assertTrue(reSerialized.contains("\"items\":[]"), "an empty batch must serialize items as [], not omit it");
    }

    @Test
    void resultBatchMissingItemsFailsNotNullValidation() {
        DiscoveryResultBatchEvent batch = new DiscoveryResultBatchEvent();
        batch.setItems(null);

        Set<ConstraintViolation<DiscoveryResultBatchEvent>> violations = VALIDATOR.validate(batch);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items")),
                "a null items list must fail the @NotNull constraint");
    }

    @Test
    void resultBatchNullItemElementIsRejected() {
        DiscoveryResultBatchEvent batch = new DiscoveryResultBatchEvent();
        batch.setItems(Collections.singletonList(null));

        Set<ConstraintViolation<DiscoveryResultBatchEvent>> violations = VALIDATOR.validate(batch);
        // A container-element constraint reports the element node, so the path a caller sees for a
        // null entry is items[0].<list element>, not items[0].
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items[0].<list element>")),
                "a null entry inside items must be rejected, not counted as a discovered item");
    }

    @Test
    void resultBatchItemConstraintsCascadeViaValid() {
        DiscoveryResultBatchEvent batch = new DiscoveryResultBatchEvent();
        batch.setItems(List.of(new DiscoveredItemDto())); // fields all unset

        Set<ConstraintViolation<DiscoveryResultBatchEvent>> violations = VALIDATOR.validate(batch);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items[0].uniqueRef")),
                "@Valid must cascade into items so each DiscoveredItemDto's own constraints are evaluated");
    }

    @Test
    void stateChangedLineParsesAndRoundTripsVerbatim() throws Exception {
        String json = "{\"type\": \"stateChanged\", \"state\": \"completed\"}";

        DiscoveryEvent event = mapper.readValue(json, DiscoveryEvent.class);

        assertEquals(DiscoveryEventType.STATE_CHANGED, event.getType());
        DiscoveryStateChangedEvent stateChanged = assertInstanceOf(DiscoveryStateChangedEvent.class, event);
        assertEquals(DiscoveryRunState.COMPLETED, stateChanged.getState());

        String reSerialized = mapper.writeValueAsString(event);
        assertFalse(reSerialized.contains("\n"), "an NDJSON line must not contain an embedded newline");
        assertEquals(1, StringUtils.countMatches(reSerialized, "\"type\""));
        assertTrue(reSerialized.contains("\"type\":\"stateChanged\""));
        // Platform wire codes are not upper-snake — pin the real code, not the enum's own name().
        assertTrue(reSerialized.contains("\"state\":\"completed\""),
                "state must serialize using the wire code, not COMPLETED");
    }

    @Test
    void heartbeatLineParsesAndRoundTripsVerbatim() throws Exception {
        // sentAt keeps a heartbeat shape-distinct from an all-optional progress event, which would
        // otherwise serialize identically, and doubles as a liveness measurement.
        String json = "{\"type\": \"heartbeat\",    \"sentAt\": \"2026-08-01T00:00:00Z\"}";

        DiscoveryEvent event = mapper.readValue(json, DiscoveryEvent.class);

        assertEquals(DiscoveryEventType.HEARTBEAT, event.getType());
        DiscoveryHeartbeatEvent heartbeat = assertInstanceOf(DiscoveryHeartbeatEvent.class, event);
        assertEquals(OffsetDateTime.parse("2026-08-01T00:00:00Z"), heartbeat.getSentAt());

        String reSerialized = mapper.writeValueAsString(event);
        assertFalse(reSerialized.contains("\n"), "an NDJSON line must not contain an embedded newline");
        assertEquals(1, StringUtils.countMatches(reSerialized, "\"type\""));
        assertTrue(reSerialized.contains("\"type\":\"heartbeat\""));
        assertTrue(reSerialized.contains("\"sentAt\""), "sentAt is heartbeat's one required field");
    }

    @Test
    void heartbeatMissingSentAtFailsNotNullValidation() {
        DiscoveryHeartbeatEvent heartbeat = new DiscoveryHeartbeatEvent();
        // sentAt intentionally left unset.

        Set<ConstraintViolation<DiscoveryHeartbeatEvent>> violations = VALIDATOR.validate(heartbeat);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sentAt")),
                "a null sentAt must fail the @NotNull constraint");
    }

    @Test
    void errorLineParsesAndRoundTripsVerbatim() throws Exception {
        String json = "{\"type\": \"error\",        \"code\": \"CONN_TIMEOUT\", "
                + "\"message\": \"upstream connection timed out\"}";

        DiscoveryEvent event = mapper.readValue(json, DiscoveryEvent.class);

        assertEquals(DiscoveryEventType.ERROR, event.getType());
        DiscoveryErrorEvent error = assertInstanceOf(DiscoveryErrorEvent.class, event);
        assertEquals("CONN_TIMEOUT", error.getCode());
        assertEquals("upstream connection timed out", error.getMessage());

        String reSerialized = mapper.writeValueAsString(event);
        assertFalse(reSerialized.contains("\n"), "an NDJSON line must not contain an embedded newline");
        assertEquals(1, StringUtils.countMatches(reSerialized, "\"type\""));
        assertTrue(reSerialized.contains("\"type\":\"error\""));
    }

    @Test
    void errorMissingCodeFailsNotNullValidation() {
        DiscoveryErrorEvent error = new DiscoveryErrorEvent();
        error.setMessage("upstream connection timed out");
        // code intentionally left unset.

        Set<ConstraintViolation<DiscoveryErrorEvent>> violations = VALIDATOR.validate(error);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code")),
                "a null code must fail the @NotNull constraint");
    }

    @Test
    void errorCodeLongerThanTheBoundFailsValidation() {
        DiscoveryErrorEvent error = new DiscoveryErrorEvent();
        error.setCode("C".repeat(65));
        error.setMessage("upstream connection timed out");

        Set<ConstraintViolation<DiscoveryErrorEvent>> violations = VALIDATOR.validate(error);

        // The platform stores the code as the identity of a kind of problem. An unbounded one has to be shortened
        // to be stored, and two long codes sharing a prefix would then aggregate as one entry -- so the contract
        // refuses the code rather than leaving it to be silently rewritten.
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code")),
                "a code past 64 characters must fail the @Size constraint");
    }

    @Test
    void errorCodeAtTheBoundPassesValidation() {
        DiscoveryErrorEvent error = new DiscoveryErrorEvent();
        error.setCode("C".repeat(64));
        error.setMessage("upstream connection timed out");

        assertTrue(VALIDATOR.validate(error).isEmpty(), "64 characters is the longest code a connector may send");
    }

    @Test
    void errorMissingMessageFailsNotNullValidation() {
        DiscoveryErrorEvent error = new DiscoveryErrorEvent();
        error.setCode("CONN_TIMEOUT");
        // message intentionally left unset.

        Set<ConstraintViolation<DiscoveryErrorEvent>> violations = VALIDATOR.validate(error);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("message")),
                "a null message must fail the @NotNull constraint");
    }

    @Test
    void unregisteredOrUnknownEventTypeCodeFailsTypeResolution() {
        // Jackson matches the wire string against the registered @JsonSubTypes names, never
        // against DiscoveryEventType's own codes, so an unregistered code fails the same way
        // whether or not it is also a real enum member. BaseApiClient turns this failure into a
        // PlatformException at the client boundary (see BaseApiClientTest); here the mapper is
        // called directly, so the raw Jackson exception surfaces.
        assertThrows(InvalidTypeIdException.class, () -> mapper
                .readValue("{\"type\":\"widgets\",\"code\":\"x\",\"message\":\"y\"}", DiscoveryEvent.class));
    }

    @Test
    void missingTypeFieldFailsTypeResolution() {
        // No "type" key anywhere in the object: Jackson has no discriminator to resolve a subtype
        // from at all, distinct from a syntactically-present-but-unregistered code above.
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue("{\"code\":\"x\",\"message\":\"y\"}", DiscoveryEvent.class));
    }

    @Test
    void nullTypeFieldFailsTypeResolution() {
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue("{\"type\":null,\"code\":\"x\",\"message\":\"y\"}", DiscoveryEvent.class));
    }

    @Test
    void nullTypeFieldOnConcreteInstanceFailsNotNullValidation() {
        // Below the discriminator layer (a concrete event built directly, not deserialized), type
        // is a plain @NotNull field like any other — this is the validation-cascade guarantee
        // DiscoveryOperationController's @Valid @RequestBody relies on.
        DiscoveryHeartbeatEvent heartbeat = new DiscoveryHeartbeatEvent();
        heartbeat.setType(null);
        heartbeat.setSentAt(OffsetDateTime.parse("2026-08-01T00:00:00Z"));

        Set<ConstraintViolation<DiscoveryHeartbeatEvent>> violations = VALIDATOR.validate(heartbeat);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("type")),
                "a null type must fail the @NotNull constraint");
    }

    @Test
    void nonObjectTopLevelInputIsRejected() {
        assertThrows(MismatchedInputException.class, () -> mapper.readValue("\"just-a-string\"", DiscoveryEvent.class));
    }

    @Test
    void mismatchedShapeFailsValidationNotDeserialization() throws Exception {
        // type says "stateChanged" but the object carries the error fields. Because
        // DiscoveryStateChangedEvent tolerates unknown properties — so connectors can add a field
        // without a lock-step release — this deserializes with a null state rather than throwing,
        // and the @NotNull on state catches it. A shape mismatch is a validation failure here,
        // not a deserialization one.
        String json = "{\"type\":\"stateChanged\",\"code\":\"x\",\"message\":\"y\"}";

        DiscoveryEvent event = mapper.readValue(json, DiscoveryEvent.class);
        DiscoveryStateChangedEvent stateChanged = assertInstanceOf(DiscoveryStateChangedEvent.class, event);
        assertNull(stateChanged.getState());

        Set<ConstraintViolation<DiscoveryStateChangedEvent>> violations = VALIDATOR.validate(stateChanged);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")),
                "an error-shaped object under type: stateChanged must fail validation on the required state");
    }

    @Test
    void unknownAdditionalPropertyOnCorrectlyShapedEventIsTolerated() throws Exception {
        // This is the case @JsonIgnoreProperties(ignoreUnknown = true) exists for: a connector
        // adds a genuinely new field to an otherwise-correctly-shaped event, and Core must not
        // choke on it (that is what keeps Go/Java/Python connectors free of a lock-step release).
        String json = "{\"type\":\"error\",\"code\":\"CONN_TIMEOUT\","
                + "\"message\":\"upstream connection timed out\",\"retryable\":true}";

        DiscoveryEvent event = mapper.readValue(json, DiscoveryEvent.class);
        DiscoveryErrorEvent error = assertInstanceOf(DiscoveryErrorEvent.class, event);
        assertEquals("CONN_TIMEOUT", error.getCode());
        assertEquals("upstream connection timed out", error.getMessage());

        Set<ConstraintViolation<DiscoveryErrorEvent>> violations = VALIDATOR.validate(error);
        assertTrue(violations.isEmpty(),
                "an unknown but additional field on an otherwise-correct event must not fail validation");
    }

    @Test
    void unknownAdditionalPropertyOnProgressEventIsTolerated() throws Exception {
        String json = "{\"type\":\"progress\",\"targetsProcessed\":10,\"etaSeconds\":42}";

        DiscoveryEvent event = mapper.readValue(json, DiscoveryEvent.class);
        DiscoveryProgressEvent progress = assertInstanceOf(DiscoveryProgressEvent.class, event);
        assertEquals(10L, progress.getTargetsProcessed());

        Set<ConstraintViolation<DiscoveryProgressEvent>> violations = VALIDATOR.validate(progress);
        assertTrue(violations.isEmpty(),
                "an unknown but additional field on an otherwise-correct progress event must not fail validation");
    }
}
