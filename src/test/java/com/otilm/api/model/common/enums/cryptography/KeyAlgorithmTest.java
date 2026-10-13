package com.otilm.api.model.common.enums.cryptography;

import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class KeyAlgorithmTest {

    private static final Set<KeyAlgorithm> SIZED_BY_LENGTH = EnumSet
            .of(KeyAlgorithm.RSA, KeyAlgorithm.ECDSA, KeyAlgorithm.AES);

    @Test
    void aesIsASecretKeyAlgorithm() {
        assertEquals(KeyAlgorithm.AES, KeyAlgorithm.findByCode("AES"));
        assertEquals("AES", KeyAlgorithm.AES.getCode());
        assertEquals("AES", KeyAlgorithm.AES.getLabel());
        assertEquals("Advanced Encryption Standard", KeyAlgorithm.AES.getDescription());
        assertFalse(KeyAlgorithm.AES.isKeyPairAlgorithm(), "AES produces a secret key, not a key pair");
    }

    @ParameterizedTest
    @EnumSource(KeyAlgorithm.class)
    void isSizedByLength_onlyForRsaEcdsaAndAes(KeyAlgorithm algorithm) {
        assertEquals(SIZED_BY_LENGTH.contains(algorithm), algorithm.isSizedByLength());
    }
}
