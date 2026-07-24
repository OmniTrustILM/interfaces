package com.otilm.api.model.common.enums.cryptography;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyAlgorithmTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void aesUsesStableProtocolValue() throws Exception {
        assertEquals(KeyAlgorithm.AES, KeyAlgorithm.findByCode("AES"));
        assertEquals("\"AES\"", mapper.writeValueAsString(KeyAlgorithm.AES));
        assertEquals(KeyAlgorithm.AES, mapper.readValue("\"AES\"", KeyAlgorithm.class));
    }
}
