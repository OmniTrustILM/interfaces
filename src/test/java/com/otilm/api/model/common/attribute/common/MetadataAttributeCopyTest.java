package com.otilm.api.model.common.attribute.common;

import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MetadataAttributeCopyTest {

    @Test
    void copyV2_isDistinctInstance_withEqualFieldValues() {
        MetadataAttributeV2 original = new MetadataAttributeV2();
        original.setUuid("uuid-1");
        original.setName("attr");
        original.setContentType(AttributeContentType.STRING);
        MetadataAttributeProperties properties = new MetadataAttributeProperties();
        properties.setLabel("Label");
        original.setProperties(properties);
        StringAttributeContentV2 contentElement = new StringAttributeContentV2("value");
        original.setContent(List.of(contentElement));

        MetadataAttributeV2 copy = original.copy();

        assertNotSame(original, copy);
        assertEquals(original.getUuid(), copy.getUuid());
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getContentType(), copy.getContentType());
        assertEquals(original.getProperties(), copy.getProperties());
        assertEquals(original.getContent(), copy.getContent());

        // documented reference-sharing contract: properties and content elements are shared,
        // only the list container is a distinct instance
        assertSame(original.getProperties(), copy.getProperties());
        assertNotSame(original.getContent(), copy.getContent());
        assertSame(contentElement, copy.getContent().getFirst());
    }

    @Test
    void copyV3_isDistinctInstance_withEqualFieldValues() {
        MetadataAttributeV3 original = new MetadataAttributeV3();
        original.setUuid("uuid-2");
        original.setName("attr");
        original.setContentType(AttributeContentType.STRING);
        original.setSchemaVersion(AttributeVersion.V3);
        MetadataAttributeProperties properties = new MetadataAttributeProperties();
        properties.setLabel("Label");
        original.setProperties(properties);
        StringAttributeContentV3 contentElement = new StringAttributeContentV3("value");
        original.setContent(List.of(contentElement));

        MetadataAttributeV3 copy = original.copy();

        assertNotSame(original, copy);
        assertEquals(original.getUuid(), copy.getUuid());
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getContentType(), copy.getContentType());
        assertEquals(original.getSchemaVersion(), copy.getSchemaVersion());
        assertEquals(original.getProperties(), copy.getProperties());
        assertEquals(original.getContent(), copy.getContent());

        // documented reference-sharing contract: properties and content elements are shared,
        // only the list container is a distinct instance
        assertSame(original.getProperties(), copy.getProperties());
        assertNotSame(original.getContent(), copy.getContent());
        assertSame(contentElement, copy.getContent().getFirst());
    }

    @Test
    void copy_withNullContent_doesNotThrowAndCopiesNull() {
        MetadataAttributeV2 original = new MetadataAttributeV2();
        original.setContentType(AttributeContentType.STRING);
        original.setContent(null);

        MetadataAttributeV2 copy = original.copy();

        assertNull(copy.getContent());
    }

    @Test
    void replacingCopyContentReference_doesNotAffectOriginal() {
        MetadataAttributeV2 original = new MetadataAttributeV2();
        original.setContentType(AttributeContentType.STRING);
        original.setContent(List.of(new StringAttributeContentV2("value")));

        MetadataAttribute copy = original.copy();
        copy.setContent(List.of());

        List<?> copyContent = copy.getContent();
        List<?> originalContent = original.getContent();

        assertTrue(copyContent.isEmpty());
        assertEquals(1, originalContent.size());
        assertEquals("value", ((StringAttributeContentV2) originalContent.get(0)).getData());
    }

    @Test
    void mutatingCopyContentListInPlace_doesNotAffectOriginal() {
        // The content list itself is copied, not shared, so in-place mutation of the copy's
        // list (as opposed to replacing the reference via setContent) must not leak back.
        MetadataAttributeV2 original = new MetadataAttributeV2();
        original.setContentType(AttributeContentType.STRING);
        original.setContent(List.of(new StringAttributeContentV2("value")));

        MetadataAttributeV2 copy = original.copy();
        copy.getContent().clear();

        assertTrue(copy.getContent().isEmpty());
        assertEquals(1, original.getContent().size());
        assertEquals("value", ((StringAttributeContentV2) original.getContent().get(0)).getData());
    }

    @Test
    void copy_preservesRuntimeType() {
        MetadataAttributeV3 original = new MetadataAttributeV3();
        original.setContentType(AttributeContentType.STRING);

        MetadataAttribute copy = original.copy();

        assertInstanceOf(MetadataAttributeV3.class, copy);
    }
}
