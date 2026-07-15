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

class MetadataAttributeCloneTest {

    @Test
    void cloneV2_isDistinctInstance_withEqualFieldValues() {
        MetadataAttributeV2 original = new MetadataAttributeV2();
        original.setUuid("uuid-1");
        original.setName("attr");
        original.setContentType(AttributeContentType.STRING);
        MetadataAttributeProperties properties = new MetadataAttributeProperties();
        properties.setLabel("Label");
        original.setProperties(properties);
        original.setContent(List.of(new StringAttributeContentV2("value")));

        MetadataAttributeV2 clone = (MetadataAttributeV2) original.clone();

        assertNotSame(original, clone);
        assertEquals(original.getUuid(), clone.getUuid());
        assertEquals(original.getName(), clone.getName());
        assertEquals(original.getContentType(), clone.getContentType());
        assertEquals(original.getProperties(), clone.getProperties());
        assertEquals(original.getContent(), clone.getContent());
    }

    @Test
    void cloneV3_isDistinctInstance_withEqualFieldValues() {
        MetadataAttributeV3 original = new MetadataAttributeV3();
        original.setUuid("uuid-2");
        original.setName("attr");
        original.setContentType(AttributeContentType.STRING);
        MetadataAttributeProperties properties = new MetadataAttributeProperties();
        properties.setLabel("Label");
        original.setProperties(properties);
        original.setContent(List.of(new StringAttributeContentV3("value")));

        MetadataAttributeV3 clone = (MetadataAttributeV3) original.clone();

        assertNotSame(original, clone);
        assertEquals(original.getUuid(), clone.getUuid());
        assertEquals(original.getName(), clone.getName());
        assertEquals(original.getContentType(), clone.getContentType());
        assertEquals(original.getProperties(), clone.getProperties());
        assertEquals(original.getContent(), clone.getContent());
    }

    @Test
    void mutatingCloneContent_doesNotAffectOriginal() {
        MetadataAttributeV2 original = new MetadataAttributeV2();
        original.setContentType(AttributeContentType.STRING);
        original.setContent(List.of(new StringAttributeContentV2("value")));

        MetadataAttribute clone = original.clone();
        clone.setContent(List.of());

        List<?> cloneContent = clone.getContent();
        List<?> originalContent = original.getContent();

        assertTrue(cloneContent.isEmpty());
        assertEquals(1, originalContent.size());
        assertEquals("value", ((StringAttributeContentV2) originalContent.get(0)).getData());
    }

    @Test
    void clone_preservesRuntimeType() {
        MetadataAttributeV3 original = new MetadataAttributeV3();
        original.setContentType(AttributeContentType.STRING);

        MetadataAttribute clone = original.clone();

        assertInstanceOf(MetadataAttributeV3.class, clone);
    }
}
