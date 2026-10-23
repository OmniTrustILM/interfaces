package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListKeysRequestValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private static ListKeysRequestV2Dto request() {
        ListKeysRequestV2Dto request = withValidTokenProfileScope(new ListKeysRequestV2Dto());
        request.setKeyRequestType(KeyRequestType.KEY_PAIR);
        request.setOffset(0);
        request.setLimit(10);
        return request;
    }

    @Test
    void aPageRequestIsValid() {
        // given
        ListKeysRequestV2Dto request = request();

        // when
        Set<ConstraintViolation<ListKeysRequestV2Dto>> violations = VALIDATORS.validator().validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void theKeyRequestTypeIsRequired() {
        // given
        ListKeysRequestV2Dto request = request();
        request.setKeyRequestType(null);

        // when
        Set<ConstraintViolation<ListKeysRequestV2Dto>> violations = VALIDATORS.validator().validate(request);

        // then
        assertEquals(1, violations.size());
        assertEquals("keyRequestType is required", violations.iterator().next().getMessage());
    }
}
