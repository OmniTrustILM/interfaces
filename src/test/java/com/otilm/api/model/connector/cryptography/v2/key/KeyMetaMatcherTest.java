package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import java.util.Arrays;
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

    static Stream<Arguments> unambiguousHandles() {
        return Stream
                .of(argumentSet("an empty handle", List.<MetadataAttribute>of()),
                        argumentSet("one attribute", List.of(meta("u1", "keyId", "Key ID", "0a0b"))),
                        argumentSet("attributes of different names",
                                List
                                        .of(meta("u1", "keyId", "Key ID", "0a0b"),
                                                meta("u2", "keyLabel", "Key label", "signer"))));
    }

    private static MetadataAttribute metaWithNullContentItem() {
        MetadataAttribute attribute = meta("u1", "keyId", "Key ID", "0a0b");
        attribute.setContent(Arrays.asList((StringAttributeContentV3) null));
        return attribute;
    }

    static Stream<Arguments> ambiguousHandles() {
        return Stream
                .of(argumentSet("a repeated name",
                        List.of(meta("u1", "keyId", "Key ID", "0a0b"), meta("u1", "keyId", "Key ID", "0a0c"))),
                        argumentSet("a null content item", List.of(metaWithNullContentItem())),
                        argumentSet("content holding only a reference", List
                                .of(meta("u1", "keyId", "Key ID", new StringAttributeContentV3("signer-a", null)))));
    }

    @ParameterizedTest
    @MethodSource("unambiguousHandles")
    void isUnambiguous_accepts(List<MetadataAttribute> handle) {
        // when
        boolean unambiguous = KeyMetaMatcher.isUnambiguous(handle);

        // then
        assertTrue(unambiguous);
    }

    @ParameterizedTest
    @MethodSource("ambiguousHandles")
    void isUnambiguous_refuses(List<MetadataAttribute> handle) {
        // when
        boolean unambiguous = KeyMetaMatcher.isUnambiguous(handle);

        // then
        assertFalse(unambiguous);
    }
}
