package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyPairMetaMatcherTest {

    private static MetadataAttribute meta(String uuid, String name, String label, String value) {
        MetadataAttributeV3 attribute = new MetadataAttributeV3();
        attribute.setUuid(uuid);
        attribute.setName(name);
        attribute.setContentType(AttributeContentType.STRING);
        MetadataAttributeProperties properties = new MetadataAttributeProperties();
        properties.setLabel(label);
        attribute.setProperties(properties);
        attribute.setContent(List.of(new StringAttributeContentV3(value)));
        return attribute;
    }

    @Test
    void matches_sameNamesAndContentInAnotherOrder() {
        // given
        List<MetadataAttribute> handle = List
                .of(meta("u1", "keyId", "Key ID", "0a0b"), meta("u2", "keyLabel", "Key label", "signer"));
        List<MetadataAttribute> other = List
                .of(meta("u2", "keyLabel", "Key label", "signer"), meta("u1", "keyId", "Key ID", "0a0b"));

        // when
        boolean matches = KeyPairMetaMatcher.matches(handle, other);

        // then
        assertTrue(matches);
    }

    @Test
    void matches_ignoresUuidAndLabelOfTheSameName() {
        // given
        List<MetadataAttribute> handle = List.of(meta("u1", "keyId", "Key ID", "0a0b"));
        List<MetadataAttribute> other = List.of(meta("u9", "keyId", "Identifier", "0a0b"));

        // when
        boolean matches = KeyPairMetaMatcher.matches(handle, other);

        // then
        assertTrue(matches);
    }

    @Test
    void matches_refusesOtherContent() {
        // given
        List<MetadataAttribute> handle = List.of(meta("u1", "keyId", "Key ID", "0a0b"));
        List<MetadataAttribute> other = List.of(meta("u1", "keyId", "Key ID", "0a0c"));

        // when
        boolean matches = KeyPairMetaMatcher.matches(handle, other);

        // then
        assertFalse(matches);
    }

    @Test
    void matches_refusesAMissingName() {
        // given
        List<MetadataAttribute> handle = List
                .of(meta("u1", "keyId", "Key ID", "0a0b"), meta("u2", "keyLabel", "Key label", "signer"));
        List<MetadataAttribute> other = List.of(meta("u1", "keyId", "Key ID", "0a0b"));

        // when
        boolean matches = KeyPairMetaMatcher.matches(handle, other);

        // then
        assertFalse(matches);
    }

    @Test
    void matches_ignoresTheContentReference() {
        // given
        MetadataAttribute withoutReference = meta("u1", "keyId", "Key ID", "0a0b");
        withoutReference.setContent(List.of(new StringAttributeContentV3(null, "0a0b")));

        // when
        boolean matches = KeyPairMetaMatcher
                .matches(List.of(meta("u1", "keyId", "Key ID", "0a0b")), List.of(withoutReference));

        // then
        assertTrue(matches);
    }

    @Test
    void matches_refusesHandlesWhoseContentHoldsOnlyAReference() {
        // given
        MetadataAttribute referenceOnly = meta("u1", "keyId", "Key ID", null);
        referenceOnly.setContent(List.of(new StringAttributeContentV3("signer-a", null)));
        MetadataAttribute otherReferenceOnly = meta("u1", "keyId", "Key ID", null);
        otherReferenceOnly.setContent(List.of(new StringAttributeContentV3("signer-b", null)));

        // when
        boolean matches = KeyPairMetaMatcher.matches(List.of(referenceOnly), List.of(otherReferenceOnly));

        // then
        assertFalse(matches);
    }

    @Test
    void matches_refusesARepeatedName() {
        // given
        List<MetadataAttribute> repeated = List
                .of(meta("u1", "keyId", "Key ID", "0a0b"), meta("u1", "keyId", "Key ID", "0a0c"));
        List<MetadataAttribute> other = List.of(meta("u1", "keyId", "Key ID", "0a0c"));

        // when
        boolean matches = KeyPairMetaMatcher.matches(repeated, other);

        // then
        assertFalse(matches);
    }
}
