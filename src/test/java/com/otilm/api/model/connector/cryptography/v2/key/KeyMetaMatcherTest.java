package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.argumentSet;

class KeyMetaMatcherTest {

    private static MetadataAttribute meta(String uuid, String name, String label, String value) {
        return meta(uuid, name, label, new StringAttributeContentV3(value));
    }

    private static MetadataAttribute meta(String uuid, String name, String label, StringAttributeContentV3 content) {
        MetadataAttributeV3 attribute = new MetadataAttributeV3();
        attribute.setUuid(uuid);
        attribute.setName(name);
        attribute.setContentType(AttributeContentType.STRING);
        MetadataAttributeProperties properties = new MetadataAttributeProperties();
        properties.setLabel(label);
        attribute.setProperties(properties);
        attribute.setContent(List.of(content));
        return attribute;
    }

    static Stream<Arguments> matchingHandles() {
        return Stream
                .of(argumentSet("the same names and content in another order",
                        List.of(meta("u1", "keyId", "Key ID", "0a0b"), meta("u2", "keyLabel", "Key label", "signer")),
                        List.of(meta("u2", "keyLabel", "Key label", "signer"), meta("u1", "keyId", "Key ID", "0a0b"))),
                        argumentSet("another uuid and label of the same name",
                                List.of(meta("u1", "keyId", "Key ID", "0a0b")),
                                List.of(meta("u9", "keyId", "Identifier", "0a0b"))),
                        argumentSet("content without its reference", List.of(meta("u1", "keyId", "Key ID", "0a0b")),
                                List.of(meta("u1", "keyId", "Key ID", new StringAttributeContentV3(null, "0a0b")))));
    }

    static Stream<Arguments> refusedHandles() {
        return Stream
                .of(argumentSet("other content", List.of(meta("u1", "keyId", "Key ID", "0a0b")),
                        List.of(meta("u1", "keyId", "Key ID", "0a0c"))),
                        argumentSet("a missing name",
                                List
                                        .of(meta("u1", "keyId", "Key ID", "0a0b"),
                                                meta("u2", "keyLabel", "Key label", "signer")),
                                List.of(meta("u1", "keyId", "Key ID", "0a0b"))),
                        argumentSet("content holding only a reference",
                                List.of(meta("u1", "keyId", "Key ID", new StringAttributeContentV3("signer-a", null))),
                                List.of(meta("u1", "keyId", "Key ID", new StringAttributeContentV3("signer-b", null)))),
                        argumentSet("a repeated name",
                                List.of(meta("u1", "keyId", "Key ID", "0a0b"), meta("u1", "keyId", "Key ID", "0a0c")),
                                List.of(meta("u1", "keyId", "Key ID", "0a0c"))));
    }

    @ParameterizedTest
    @MethodSource("matchingHandles")
    void matches_accepts(List<MetadataAttribute> handle, List<MetadataAttribute> other) {
        // when
        boolean matches = KeyMetaMatcher.matches(handle, other);

        // then
        assertTrue(matches);
    }

    @ParameterizedTest
    @MethodSource("refusedHandles")
    void matches_refuses(List<MetadataAttribute> handle, List<MetadataAttribute> other) {
        // when
        boolean matches = KeyMetaMatcher.matches(handle, other);

        // then
        assertFalse(matches);
    }
}
