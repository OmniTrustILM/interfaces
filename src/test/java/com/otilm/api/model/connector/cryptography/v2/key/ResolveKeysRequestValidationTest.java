package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResolveKeysRequestValidationTest {

    private static final String KEYS_SIZE_MESSAGE = "keys must hold 1 to 1000 handles";

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private static ResolveKeysRequestV2Dto request(List<KeyHandleV2Dto> keys) {
        return ResolveKeysRequestV2Dto
                .builder()
                .scope(withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto()))
                .keyRequestType(KeyRequestType.KEY_PAIR)
                .keys(keys)
                .build();
    }

    private static KeyHandleV2Dto handle(String identifier) {
        return new KeyHandleV2Dto(identifier, validMetadata());
    }

    private static void assertSingleViolation(ResolveKeysRequestV2Dto request, String expectedMessage) {
        Set<ConstraintViolation<ResolveKeysRequestV2Dto>> violations = VALIDATORS.validator().validate(request);
        assertEquals(1, violations.size());
        assertEquals(expectedMessage, violations.iterator().next().getMessage());
    }

    @Test
    void oneHandleIsValid() {
        // given
        ResolveKeysRequestV2Dto request = request(List.of(handle("0")));

        // when
        Set<ConstraintViolation<ResolveKeysRequestV2Dto>> violations = VALIDATORS.validator().validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void noHandlesAreRefused() {
        // given
        ResolveKeysRequestV2Dto request = request(List.of());

        // when
        // then
        assertSingleViolation(request, KEYS_SIZE_MESSAGE);
    }

    @Test
    void moreHandlesThanMaxKeysAreRefused() {
        // given
        List<KeyHandleV2Dto> keys = IntStream
                .rangeClosed(0, ResolveKeysRequestV2Dto.MAX_KEYS)
                .mapToObj(position -> handle(String.valueOf(position)))
                .toList();

        // when
        // then
        assertSingleViolation(request(keys), KEYS_SIZE_MESSAGE);
    }

    @Test
    void aRepeatedIdentifierIsRefused() {
        // given
        ResolveKeysRequestV2Dto request = request(List.of(handle("0"), handle("0")));

        // when
        // then
        assertSingleViolation(request, "identifiers must be unique within the batch; duplicates item at index 0");
    }

    @Test
    void aBlankIdentifierIsRefused() {
        // given
        ResolveKeysRequestV2Dto request = request(List.of(handle(" ")));

        // when
        // then
        assertSingleViolation(request, "identifier is required");
    }

    @Test
    void anEmptyHandleIsRefused() {
        // given
        ResolveKeysRequestV2Dto request = request(List.of(new KeyHandleV2Dto("0", List.of())));

        // when
        // then
        assertSingleViolation(request, "keyMeta must not be empty");
    }

    @Test
    void theKeyRequestTypeIsRequired() {
        // given
        ResolveKeysRequestV2Dto request = request(List.of(handle("0")));
        request.setKeyRequestType(null);

        // when
        // then
        assertSingleViolation(request, "keyRequestType is required");
    }
}
