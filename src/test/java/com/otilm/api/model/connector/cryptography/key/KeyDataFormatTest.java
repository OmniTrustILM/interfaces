package com.otilm.api.model.connector.cryptography.key;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.connector.cryptography.key.value.CustomKeyValue;
import com.otilm.api.model.connector.cryptography.key.value.EprkiKeyValue;
import com.otilm.api.model.connector.cryptography.key.value.KeyValue;
import com.otilm.api.model.connector.cryptography.key.value.PrkiKeyValue;
import com.otilm.api.model.connector.cryptography.key.value.RawKeyValue;
import com.otilm.api.model.connector.cryptography.key.value.SpkiKeyValue;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Named.named;

/** {@link KeyFormat} also carries v2 formats that v1 key data has no value for. */
class KeyDataFormatTest {

    private static final List<String> V1_FORMAT_CODES = List
            .of("Raw", "SubjectPublicKeyInfo", "PrivateKeyInfo", "EncryptedPrivateKeyInfo", "Custom");

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void publishesTheFormatsItHasValuesFor() {
        // given
        Map<String, Schema> schemas = openApi31Schemas(KeyData.class);

        // when
        Schema<?> format = (Schema<?>) schemas.get("KeyData").getProperties().get("format");

        // then
        assertEquals("#/components/schemas/KeyFormat", format.get$ref());
        assertEquals(V1_FORMAT_CODES, schemas.get("KeyFormat").getEnum());
    }

    @Test
    void rejectsAFormatItHasNoValueFor() {
        // given
        ObjectMapper withoutValueCheck = JsonMapper
                .builder()
                .findAndAddModules()
                .disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY)
                .build();
        String formatOnly = """
                {"type": "Private", "algorithm": "RSA", "format": "AlgorithmIdentifier", "length": 2048}""";

        // when
        JsonMappingException rejected = assertThrows(JsonMappingException.class,
                () -> withoutValueCheck.readValue(formatOnly, KeyData.class));

        // then
        assertEquals("v1 key data has no value in format AlgorithmIdentifier", rejected.getCause().getMessage());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("v1KeyValues")
    void roundTripsEachOfItsFormats(V1KeyValue keyValue) throws Exception {
        // given
        KeyData keyData = new KeyData(KeyType.PUBLIC_KEY, KeyAlgorithm.RSA, keyValue.format(), keyValue.value(), 2048,
                null);

        // when
        KeyData back = mapper.readValue(mapper.writeValueAsString(keyData), KeyData.class);

        // then
        assertEquals(keyValue.format(), back.getFormat());
        assertInstanceOf(keyValue.value().getClass(), back.getValue());
    }

    static Stream<Named<V1KeyValue>> v1KeyValues() {
        HashMap<String, String> customValues = new HashMap<>(Map.of("handle", "42"));
        return Stream
                .of(named("Raw", new V1KeyValue(KeyFormat.RAW, new RawKeyValue("AQID"))),
                        named("SubjectPublicKeyInfo", new V1KeyValue(KeyFormat.SPKI, new SpkiKeyValue("AQID"))),
                        named("PrivateKeyInfo", new V1KeyValue(KeyFormat.PRKI, new PrkiKeyValue("AQID"))),
                        named("EncryptedPrivateKeyInfo", new V1KeyValue(KeyFormat.EPRKI, new EprkiKeyValue("AQID"))),
                        named("Custom", new V1KeyValue(KeyFormat.CUSTOM, new CustomKeyValue(customValues))));
    }

    private record V1KeyValue(KeyFormat format, KeyValue value) {
    }
}
