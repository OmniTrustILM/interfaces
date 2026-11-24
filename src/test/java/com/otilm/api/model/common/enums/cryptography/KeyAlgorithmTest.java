package com.otilm.api.model.common.enums.cryptography;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class KeyAlgorithmTest {

    @Test
    void aesIsASecretKeyAlgorithm() {
        assertEquals(KeyAlgorithm.AES, KeyAlgorithm.findByCode("AES"));
        assertEquals("AES", KeyAlgorithm.AES.getCode());
        assertEquals("AES", KeyAlgorithm.AES.getLabel());
        assertEquals("Advanced Encryption Standard", KeyAlgorithm.AES.getDescription());
        assertFalse(KeyAlgorithm.AES.isKeyPairAlgorithm(), "AES produces a secret key, not a key pair");
    }
}
