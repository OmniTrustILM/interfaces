package com.otilm.api.model.common.enums.cryptography;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyFormatTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void namesTheAlgorithmIdentifierFormatByItsCode() throws Exception {
        // given
        String code = "AlgorithmIdentifier";

        // when
        KeyFormat format = mapper.readValue("\"" + code + "\"", KeyFormat.class);

        // then
        assertEquals(KeyFormat.ALGORITHM_IDENTIFIER, format);
        assertEquals("\"" + code + "\"", mapper.writeValueAsString(format));
        assertEquals(KeyFormat.ALGORITHM_IDENTIFIER, KeyFormat.findByCode(code));
    }
}
