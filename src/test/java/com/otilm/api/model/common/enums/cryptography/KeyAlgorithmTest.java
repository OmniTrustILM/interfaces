package com.otilm.api.model.common.enums.cryptography;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** AES is a secret-key algorithm, so a provider can declare a SECRET key type against it. */
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
