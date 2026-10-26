package com.otilm.api.model.client.cryptography.key;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.common.attribute.v2.content.BaseAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadataAttribute;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenKeySelectionValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private static final String AMBIGUOUS = "keyPairMeta must name each attribute once, and each content item must carry data";

    @Test
    void theMetadataOfAListedKeyPairIsValid() {
        // given
        TokenKeySelectionDto selection = new TokenKeySelectionDto(validMetadata(), null);

        // when
        Set<ConstraintViolation<TokenKeySelectionDto>> violations = VALIDATORS.validator().validate(selection);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void emptyMetadataIsRefused() {
        // given
        TokenKeySelectionDto selection = new TokenKeySelectionDto(List.of(), null);

        // when
        Set<ConstraintViolation<TokenKeySelectionDto>> violations = VALIDATORS.validator().validate(selection);

        // then
        assertEquals(1, violations.size());
        assertEquals("keyPairMeta is required and must not be empty", violations.iterator().next().getMessage());
    }

    @Test
    void metadataWithoutContentIsRefused() {
        // given
        List<MetadataAttribute> keyPairMeta = validMetadata();
        keyPairMeta.get(0).setContent(null);
        TokenKeySelectionDto selection = new TokenKeySelectionDto(keyPairMeta, null);

        // when
        Set<ConstraintViolation<TokenKeySelectionDto>> violations = VALIDATORS.validator().validate(selection);

        // then
        assertFalse(violations.isEmpty());
    }

    @Test
    void metadataRepeatingANameIsRefused() {
        // given
        TokenKeySelectionDto selection = new TokenKeySelectionDto(
                List.of(validMetadataAttribute(), validMetadataAttribute()), null);

        // when
        Set<ConstraintViolation<TokenKeySelectionDto>> violations = VALIDATORS.validator().validate(selection);

        // then
        assertEquals(1, violations.size());
        assertEquals(AMBIGUOUS, violations.iterator().next().getMessage());
    }

    @Test
    void metadataWithAContentItemWithoutDataIsRefused() {
        // given
        MetadataAttributeV2 handle = validMetadataAttribute();
        handle.setContent(List.of(new StringAttributeContentV2("provider-key-1", null)));
        TokenKeySelectionDto selection = new TokenKeySelectionDto(List.of(handle), null);

        // when
        Set<ConstraintViolation<TokenKeySelectionDto>> violations = VALIDATORS.validator().validate(selection);

        // then
        assertTrue(violations.stream().map(ConstraintViolation::getMessage).anyMatch(AMBIGUOUS::equals));
    }

    @Test
    void metadataWithANullContentItemIsRefused() {
        // given
        MetadataAttributeV2 handle = validMetadataAttribute();
        List<BaseAttributeContentV2<?>> content = new ArrayList<>();
        content.add(null);
        handle.setContent(content);
        TokenKeySelectionDto selection = new TokenKeySelectionDto(List.of(handle), null);

        // when
        Set<ConstraintViolation<TokenKeySelectionDto>> violations = VALIDATORS.validator().validate(selection);

        // then
        assertTrue(violations.stream().map(ConstraintViolation::getMessage).anyMatch(AMBIGUOUS::equals));
    }
}
