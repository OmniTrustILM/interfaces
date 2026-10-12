package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListKeyPairsRequestValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private static ListKeyPairsRequestV2Dto request() {
        ListKeyPairsRequestV2Dto request = withValidTokenProfileScope(new ListKeyPairsRequestV2Dto());
        request.setOffset(0);
        request.setLimit(10);
        return request;
    }

    @Test
    void aFilterNamingKeyPairsIsValid() {
        // given
        ListKeyPairsRequestV2Dto request = request();
        request.setKeyPairMetaFilter(List.of(validMetadata()));

        // when
        Set<ConstraintViolation<ListKeyPairsRequestV2Dto>> violations = VALIDATORS.validator().validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void anEmptyFilterIsRefused() {
        // given
        ListKeyPairsRequestV2Dto request = request();
        request.setKeyPairMetaFilter(List.of());

        // when
        Set<ConstraintViolation<ListKeyPairsRequestV2Dto>> violations = VALIDATORS.validator().validate(request);

        // then
        assertEquals(1, violations.size());
        assertEquals("keyPairMetaFilter must name at least one key pair when present",
                violations.iterator().next().getMessage());
    }
}
