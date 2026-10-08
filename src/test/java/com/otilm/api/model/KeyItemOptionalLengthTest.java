package com.otilm.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import com.otilm.api.model.core.cryptography.key.KeyItemDetailDto;
import com.otilm.api.model.core.cryptography.key.KeyItemDto;
import com.otilm.api.model.core.cryptography.key.KeyState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyItemOptionalLengthTest {

    @ParameterizedTest
    @ValueSource(classes = {KeyItemDto.class, KeyItemDetailDto.class})
    void response_preservesNullLength_andDeclaresItOptional(Class<?> responseType) throws Exception {
        // given
        Object response = responseType.getConstructor().newInstance();
        responseType.getMethod("setLength", Integer.class).invoke(response, new Object[]{null});

        // when
        JsonNode json = new ObjectMapper().valueToTree(response);
        Schema schema = responseType.getDeclaredField("length").getAnnotation(Schema.class);

        // then
        assertTrue(json.path("length").isNull() || json.path("length").isMissingNode());
        assertEquals(Schema.RequiredMode.NOT_REQUIRED, schema.requiredMode());
        assertTrue(schema.nullable());
    }

    @Test
    void detail_acceptsPrimitiveLength_throughAutoboxing() {
        // given
        int rsaLength = 2048;

        // when
        KeyItemDetailDto response = new KeyItemDetailDto(null, KeyType.PRIVATE_KEY, KeyAlgorithm.RSA, KeyFormat.PRKI,
                null, rsaLength, List.of(), List.of(), true, KeyState.ACTIVE, null, ComplianceStatus.NOT_CHECKED, true);

        // then
        assertEquals(rsaLength, response.getLength());
        assertTrue(response.isExportable());
    }

    @Test
    void summary_acceptsPrimitiveLength_throughAutoboxing() {
        // given
        int rsaLength = 2048;

        // when
        KeyItemDto response = new KeyItemDto(null, null, null, null, null, null, null, null, null, List.of(), 0, null,
                KeyType.PRIVATE_KEY, KeyAlgorithm.RSA, KeyFormat.PRKI, rsaLength, List.of(), true, KeyState.ACTIVE,
                ComplianceStatus.NOT_CHECKED, null);

        // then
        assertEquals(rsaLength, response.getLength());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = 2048)
    void detail_preservesNullableLength_withExportable(Integer length) {
        // given
        Integer providerLength = length;

        // when
        KeyItemDetailDto response = new KeyItemDetailDto(null, KeyType.PRIVATE_KEY, KeyAlgorithm.UNKNOWN,
                KeyFormat.PRKI, null, providerLength, List.of(), List.of(), true, KeyState.ACTIVE, null,
                ComplianceStatus.NOT_CHECKED, true);

        // then
        assertEquals(providerLength, response.getLength());
        assertTrue(response.isExportable());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = 2048)
    void summary_preservesNullableLength_withAttributeValues(Integer length) {
        // given
        Integer providerLength = length;

        // when
        KeyItemDto response = new KeyItemDto(null, null, null, null, null, null, null, null, null, List.of(), 0, null,
                KeyType.PRIVATE_KEY, KeyAlgorithm.UNKNOWN, KeyFormat.PRKI, providerLength, List.of(), true,
                KeyState.ACTIVE, ComplianceStatus.NOT_CHECKED, null);

        // then
        assertEquals(providerLength, response.getLength());
    }
}
