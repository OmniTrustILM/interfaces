package com.otilm.api.model.connector.cryptography.v2.operations.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.OperationEventType;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.OperationEventV2Dto;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.EVENT_TIME;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validEvent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class SignatureResponseItemV2DtoTest {

    private static final String IDENTIFIER = "item-1";
    private static final byte[] SIGNATURE = {101, 102, 103};

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void validation_acceptsASignatureWithItsEvents() {
        // given
        SignatureResponseItemV2Dto item = new SignatureResponseItemV2Dto(SIGNATURE, IDENTIFIER,
                List
                        .of(validEvent(OperationEventType.AUTHORIZATION_GRANTED),
                                validEvent(OperationEventType.SIGNATURE_CREATED)));

        // when
        Set<ConstraintViolation<SignatureResponseItemV2Dto>> violations = VALIDATOR.validate(item);

        // then
        assertTrue(violations.isEmpty(), () -> "Expected no violations, got " + describe(violations));
    }

    @Test
    void validation_acceptsASignatureWithoutEvents() {
        // given
        SignatureResponseItemV2Dto item = new SignatureResponseItemV2Dto(SIGNATURE, IDENTIFIER);

        // when
        Set<ConstraintViolation<SignatureResponseItemV2Dto>> violations = VALIDATOR.validate(item);

        // then
        assertTrue(violations.isEmpty(), () -> "Expected no violations, got " + describe(violations));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidItems")
    void validation_rejectsAnInvalidField(InvalidItem invalid) {
        // given
        SignatureResponseItemV2Dto item = invalid.item();

        // when
        Set<ConstraintViolation<SignatureResponseItemV2Dto>> violations = VALIDATOR.validate(item);

        // then
        assertTrue(
                violations
                        .stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals(invalid.path())
                                && violation.getMessage().equals(invalid.message())),
                () -> "Expected " + invalid.path() + ": " + invalid.message() + ", got " + describe(violations));
    }

    static Stream<Named<InvalidItem>> invalidItems() {
        OperationEventV2Dto eventWithoutType = validEvent(OperationEventType.SIGNATURE_CREATED);
        eventWithoutType.setType(null);
        return Stream
                .of(named("empty signature",
                        new InvalidItem(new SignatureResponseItemV2Dto(new byte[0], IDENTIFIER), "data",
                                "data is required and must not be empty")),
                        named("blank identifier",
                                new InvalidItem(new SignatureResponseItemV2Dto(SIGNATURE, " "), "identifier",
                                        "identifier is required")),
                        named("null event",
                                new InvalidItem(
                                        new SignatureResponseItemV2Dto(SIGNATURE, IDENTIFIER,
                                                Collections.singletonList(null)),
                                        "events[0].<list element>", "events must not contain null items")),
                        named("event without its type", new InvalidItem(
                                new SignatureResponseItemV2Dto(SIGNATURE, IDENTIFIER, List.of(eventWithoutType)),
                                "events[0].type", "type is required")));
    }

    @Test
    void toString_redactsSignature() {
        // given
        String signatureMarker = "[101, 102, 103]";
        SignatureResponseItemV2Dto item = new SignatureResponseItemV2Dto(SIGNATURE, IDENTIFIER);

        // when
        String representation = item.toString();

        // then
        assertFalse(representation.contains(signatureMarker));
        assertTrue(representation.contains(IDENTIFIER));
    }

    @Test
    void json_writesEachEventTypeAsItsCode() throws Exception {
        // given
        SignatureResponseItemV2Dto item = new SignatureResponseItemV2Dto(SIGNATURE, IDENTIFIER,
                List.of(validEvent(OperationEventType.SIGNATURE_CREATED)));

        // when
        String json = mapper.writeValueAsString(item);
        JsonNode tree = mapper.readTree(json);
        SignatureResponseItemV2Dto roundTripped = mapper.readValue(json, SignatureResponseItemV2Dto.class);

        // then
        assertEquals("signatureCreated", tree.get("events").get(0).get("type").textValue());
        assertEquals(OperationEventType.SIGNATURE_CREATED, roundTripped.getEvents().get(0).getType());
        assertTrue(EVENT_TIME.isEqual(roundTripped.getEvents().get(0).getAt()));
    }

    @Test
    void json_omitsEventsWhenTheConnectorReportsNone() throws Exception {
        // given
        SignatureResponseItemV2Dto item = new SignatureResponseItemV2Dto(SIGNATURE, IDENTIFIER, List.of());

        // when
        JsonNode tree = mapper.readTree(mapper.writeValueAsString(item));

        // then
        assertFalse(tree.has("events"));
        assertEquals(IDENTIFIER, tree.get("identifier").textValue());
    }

    private static List<String> describe(Set<ConstraintViolation<SignatureResponseItemV2Dto>> violations) {
        return violations
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
    }

    private record InvalidItem(SignatureResponseItemV2Dto item, String path, String message) {
    }
}
