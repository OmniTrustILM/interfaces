package com.otilm.api.model.common.attribute;

import com.otilm.api.model.common.attribute.common.content.data.SecretAttributeContentData;
import com.otilm.api.model.common.attribute.v2.content.SecretAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttributeContentToStringTest {

    @Test
    void v2ContentNamesItsTypeButNotItsReferenceOrData() {
        String text = new StringAttributeContentV2("activation code", "4711-0815").toString();

        assertTrue(text.contains("StringAttributeContentV2"));
        assertFalse(text.contains("activation code"));
        assertFalse(text.contains("4711-0815"));
    }

    @Test
    void v2ContentBuiltFromItsDataAloneDoesNotPrintIt() {
        String text = new StringAttributeContentV2("4711-0815").toString();

        assertTrue(text.contains("StringAttributeContentV2"));
        assertFalse(text.contains("4711-0815"));
    }

    @Test
    void v3ContentNamesItsTypeButNotItsReferenceOrData() {
        String text = new StringAttributeContentV3("activation code", "4711-0815").toString();

        assertTrue(text.contains("StringAttributeContentV3"));
        assertFalse(text.contains("activation code"));
        assertFalse(text.contains("4711-0815"));
    }

    @Test
    void v3ContentBuiltFromItsDataAloneDoesNotPrintIt() {
        String text = new StringAttributeContentV3("4711-0815").toString();

        assertTrue(text.contains("StringAttributeContentV3"));
        assertFalse(text.contains("4711-0815"));
    }

    @Test
    void secretContentNamesItsTypeButNotItsSecret() {
        String text = new SecretAttributeContentV2("pin", new SecretAttributeContentData("4711-0815")).toString();

        assertTrue(text.contains("SecretAttributeContentV2"));
        assertFalse(text.contains("4711-0815"));
    }
}
