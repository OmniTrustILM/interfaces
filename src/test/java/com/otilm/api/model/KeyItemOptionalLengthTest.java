package com.otilm.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.cryptography.key.KeyCompromiseReason;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import com.otilm.api.model.core.cryptography.key.KeyItemDetailDto;
import com.otilm.api.model.core.cryptography.key.KeyItemDto;
import com.otilm.api.model.core.cryptography.key.KeyState;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.reflect.Constructor;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Holds public response nullability and the previous primitive constructor signatures.
 */
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
    void detail_retainsPrimitiveLengthConstructor_withExportable() throws Exception {
        // given
        int rsaLength = 2048;

        // when
        KeyItemDetailDto response = new KeyItemDetailDto(null, KeyType.PRIVATE_KEY, KeyAlgorithm.RSA, KeyFormat.PRKI,
                null, rsaLength, List.of(), List.of(), true, KeyState.ACTIVE, null, ComplianceStatus.NOT_CHECKED, true);

        // then
        assertEquals(rsaLength, response.getLength());
        assertTrue(response.isExportable());
        assertNotNull(KeyItemDetailDto.class
                .getConstructor(String.class, KeyType.class, KeyAlgorithm.class, KeyFormat.class, String.class,
                        int.class, List.class, List.class, boolean.class, KeyState.class, KeyCompromiseReason.class,
                        ComplianceStatus.class, boolean.class));
    }

    @Test
    void summary_retainsPrimitiveLengthConstructor_withAttributeValues() throws Exception {
        // given
        Class<?>[] parameterTypes = {
                String.class,
                OffsetDateTime.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                List.class,
                int.class,
                String.class,
                KeyType.class,
                KeyAlgorithm.class,
                KeyFormat.class,
                int.class,
                List.class,
                boolean.class,
                KeyState.class,
                ComplianceStatus.class,
                Map.class};

        // when
        Constructor<KeyItemDto> constructor = KeyItemDto.class.getConstructor(parameterTypes);

        // then
        assertNotNull(constructor);
    }
}
