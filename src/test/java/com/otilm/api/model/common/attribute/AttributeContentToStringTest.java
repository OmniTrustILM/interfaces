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
    void v2ContentNamesItsReferenceButNotItsData() {
        String text = new StringAttributeContentV2("activation code", "4711-0815").toString();

        assertTrue(text.contains("activation code"));
        assertFalse(text.contains("4711-0815"));
    }

    @Test
    void v3ContentNamesItsReferenceButNotItsData() {
        String text = new StringAttributeContentV3("activation code", "4711-0815").toString();

        assertTrue(text.contains("activation code"));
        assertFalse(text.contains("4711-0815"));
    }

    @Test
    void secretContentNamesItsReferenceButNotItsSecret() {
        String text = new SecretAttributeContentV2("pin", new SecretAttributeContentData("4711-0815")).toString();

        assertTrue(text.contains("pin"));
        assertFalse(text.contains("4711-0815"));
    }
}
