package com.otilm.api.model.connector.cryptography.key.value;

import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.connector.cryptography.key.KeyData;
import com.otilm.api.model.connector.cryptography.key.KeyDataResponseDto;
import com.otilm.api.model.connector.cryptography.key.KeyPairDataResponseDto;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyValueToStringTest {

    @Test
    void prkiKeyValueToStringLeavesTheValueOut() {
        PrkiKeyValue keyValue = new PrkiKeyValue();
        keyValue.setValue("prki-secret-9f3a");

        String text = keyValue.toString();

        assertTrue(text.contains("PrkiKeyValue"));
        assertFalse(text.contains("prki-secret-9f3a"));
    }

    @Test
    void eprkiKeyValueToStringLeavesTheValueOut() {
        EprkiKeyValue keyValue = new EprkiKeyValue();
        keyValue.setValue("eprki-secret-2b7d");

        String text = keyValue.toString();

        assertTrue(text.contains("EprkiKeyValue"));
        assertFalse(text.contains("eprki-secret-2b7d"));
    }

    @Test
    void rawKeyValueToStringLeavesTheValueOut() {
        RawKeyValue keyValue = new RawKeyValue();
        keyValue.setValue("raw-secret-6e1c");

        String text = keyValue.toString();

        assertTrue(text.contains("RawKeyValue"));
        assertFalse(text.contains("raw-secret-6e1c"));
    }

    @Test
    void customKeyValueToStringLeavesTheValuesOut() {
        HashMap<String, String> values = new HashMap<>();
        values.put("custom-secret-key-1a4f", "custom-secret-value-1a4f");
        CustomKeyValue keyValue = new CustomKeyValue();
        keyValue.setValues(values);

        String text = keyValue.toString();

        assertTrue(text.contains("CustomKeyValue"));
        assertFalse(text.contains("custom-secret-key-1a4f"));
        assertFalse(text.contains("custom-secret-value-1a4f"));
    }

    @Test
    void keyPairDataResponseDtoToStringLeavesThePrivateKeyValueOut() {
        PrkiKeyValue prkiKeyValue = new PrkiKeyValue();
        prkiKeyValue.setValue("carrier-secret-4c1e");

        KeyData keyData = new KeyData();
        keyData.setFormat(KeyFormat.PRKI);
        keyData.setValue(prkiKeyValue);

        KeyDataResponseDto privateKeyData = new KeyDataResponseDto();
        privateKeyData.setName("private key");
        privateKeyData.setKeyData(keyData);

        KeyPairDataResponseDto pair = new KeyPairDataResponseDto();
        pair.setPrivateKeyData(privateKeyData);

        String text = pair.toString();

        assertTrue(text.contains("private key"));
        assertFalse(text.contains("carrier-secret-4c1e"));
    }
}
