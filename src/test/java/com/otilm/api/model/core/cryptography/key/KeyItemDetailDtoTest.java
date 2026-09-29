package com.otilm.api.model.core.cryptography.key;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyItemDetailDtoTest {

    @Test
    void toStringLeavesTheKeyDataOut() {
        KeyItemDetailDto item = new KeyItemDetailDto();
        item.setName("signing key");
        item.setKeyData("MIIEvQIBADANBgkqhkiG9w0BAQEFAASC");

        String text = item.toString();

        assertTrue(text.contains("signing key"));
        assertFalse(text.contains("MIIEvQIBADANBgkqhkiG9w0BAQEFAASC"));
    }
}
