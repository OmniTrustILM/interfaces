package com.otilm.api.model.client.cryptography.key;

import com.otilm.api.model.connector.cryptography.v2.key.ResolveKeysRequestV2Dto;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OnboardTokenKeysRequestValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private static OnboardTokenKeysRequestDto selecting(int keys) {
        List<TokenKeySelectionDto> selections = Stream
                .generate(() -> new TokenKeySelectionDto(validMetadata(), null))
                .limit(keys)
                .toList();
        return new OnboardTokenKeysRequestDto(selections, null);
    }

    @Test
    void aSelectionOfMaxKeysIsValid() {
        // given
        OnboardTokenKeysRequestDto request = selecting(ResolveKeysRequestV2Dto.MAX_KEYS);

        // when
        Set<ConstraintViolation<OnboardTokenKeysRequestDto>> violations = VALIDATORS.validator().validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void aSelectionBeyondMaxKeysIsRefused() {
        // given
        OnboardTokenKeysRequestDto request = selecting(ResolveKeysRequestV2Dto.MAX_KEYS + 1);

        // when
        Set<ConstraintViolation<OnboardTokenKeysRequestDto>> violations = VALIDATORS.validator().validate(request);

        // then
        assertEquals(1, violations.size());
        assertEquals("keys must contain at most " + ResolveKeysRequestV2Dto.MAX_KEYS + " keys",
                violations.iterator().next().getMessage());
    }
}
