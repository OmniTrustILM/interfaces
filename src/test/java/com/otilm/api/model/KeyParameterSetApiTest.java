package com.otilm.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import com.otilm.api.model.core.cryptography.key.KeyItemDetailDto;
import com.otilm.api.model.core.cryptography.key.KeyItemDto;
import com.otilm.api.model.core.cryptography.key.KeyItemListDto;
import com.otilm.api.model.core.cryptography.key.KeyState;
import com.otilm.api.model.core.cryptography.key.TokenKeyDto;
import com.otilm.api.model.core.discovery.DiscoveryItemDto;
import com.otilm.api.model.core.discovery.KeyDescriptionDto;
import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Key items and discovery items name each key's parameters. An operator can then tell P-256 from P-384. */
class KeyParameterSetApiTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @ParameterizedTest
    @ValueSource(classes = {KeyItemDto.class, KeyItemListDto.class, KeyItemDetailDto.class, TokenKeyDto.class})
    void publishesTheParameterSetAsAnOptionalString(Class<?> responseType) {
        // given
        Map<String, Schema> schemas = openApi31Schemas(responseType);

        // when
        Schema<?> published = schemas.get(responseType.getSimpleName());
        Schema<?> parameterSet = resolvedProperty(published, "parameterSet", schemas);

        // then
        assertNotNull(parameterSet, responseType.getSimpleName() + " must publish parameterSet");
        assertEquals(Set.of("string", "null"), parameterSet.getTypes());
        assertFalse(published.getRequired() != null && published.getRequired().contains("parameterSet"));
    }

    @ParameterizedTest
    @ValueSource(classes = {KeyItemDto.class, KeyItemListDto.class, KeyItemDetailDto.class})
    void carriesTheParameterSetOfAnInventoryKey(Class<?> responseType) throws Exception {
        // given
        Object response = responseType.getConstructor().newInstance();
        responseType.getMethod("setParameterSet", String.class).invoke(response, "P-256");

        // when
        JsonNode json = mapper.valueToTree(response);

        // then
        assertEquals("P-256", json.path("parameterSet").asText());
    }

    @Test
    void carriesTheParameterSetOfAKeyInTheKeyStorage() {
        // given
        TokenKeyDto key = TokenKeyDto.builder().keyAlgorithm(KeyAlgorithm.MLDSA).parameterSet("ML-DSA-65").build();

        // when
        JsonNode json = mapper.valueToTree(key);

        // then
        assertEquals("ML-DSA-65", json.path("parameterSet").asText());
    }

    @Test
    void appendsTheParameterSetLastToTheSummaryConstructor() {
        // given
        // when
        KeyItemDto key = new KeyItemDto(null, null, null, null, null, null, null, null, null, List.of(), 0, null,
                KeyType.PUBLIC_KEY, KeyAlgorithm.ECDSA, KeyFormat.SPKI, 256, List.of(), true, KeyState.ACTIVE,
                ComplianceStatus.NOT_CHECKED, null, "P-256");

        // then
        assertEquals("P-256", key.getParameterSet());
        assertEquals(256, key.getLength());
    }

    @Test
    void keepsTheSummaryConstructorWithoutTheParameterSet() {
        // given
        // when
        KeyItemDto key = new KeyItemDto(null, null, null, null, null, null, null, null, null, List.of(), 0, null,
                KeyType.PRIVATE_KEY, KeyAlgorithm.RSA, KeyFormat.PRKI, 2048, List.of(), true, KeyState.ACTIVE,
                ComplianceStatus.NOT_CHECKED, null);

        // then
        assertNull(key.getParameterSet(), "the previous signature leaves the key unnamed");
        assertEquals(2048, key.getLength());
    }

    @Test
    void appendsTheParameterSetLastToTheDetailConstructor() {
        // given
        // when
        KeyItemDetailDto key = new KeyItemDetailDto(null, KeyType.PRIVATE_KEY, KeyAlgorithm.MLDSA,
                KeyFormat.ALGORITHM_IDENTIFIER, null, 4032, List.of(), List.of(), true, KeyState.ACTIVE, null,
                ComplianceStatus.NOT_CHECKED, false, "ML-DSA-65");

        // then
        assertEquals("ML-DSA-65", key.getParameterSet());
        assertFalse(key.isExportable());
    }

    @Test
    void keepsTheDetailConstructorWithoutTheParameterSet() {
        // given
        // when
        KeyItemDetailDto key = new KeyItemDetailDto(null, KeyType.PRIVATE_KEY, KeyAlgorithm.RSA, KeyFormat.PRKI, null,
                2048, List.of(), List.of(), true, KeyState.ACTIVE, null, ComplianceStatus.NOT_CHECKED, true);

        // then
        assertNull(key.getParameterSet(), "the previous signature leaves the key unnamed");
        assertTrue(key.isExportable());
    }

    @Test
    void publishesTheKeyDescriptionOfADiscoveryItem() {
        // given
        Map<String, Schema> schemas = openApi31Schemas(DiscoveryItemDto.class);

        // when
        Schema<?> item = schemas.get("DiscoveryItemDto");
        Schema<?> keyDescription = (Schema<?>) item.getProperties().get("keyDescription");
        Schema<?> description = schemas.get("KeyDescriptionDto");

        // then
        assertNotNull(keyDescription, "DiscoveryItemDto must publish keyDescription");
        assertFalse(item.getRequired().contains("keyDescription"));
        assertEquals(Set.of("algorithm", "parameterSet", "length"), description.getProperties().keySet());
        assertEquals(List.of("algorithm"), description.getRequired());
        assertEquals(Set.of("string"), ((Schema<?>) description.getProperties().get("parameterSet")).getTypes());
        Schema<?> length = (Schema<?>) description.getProperties().get("length");
        assertEquals(Set.of("integer"), length.getTypes());
        assertEquals(BigDecimal.ONE, length.getMinimum());
    }

    @Test
    void describesADiscoveredKeyAndOmitsAnAbsentDescription() throws Exception {
        // given
        DiscoveryItemDto described = new DiscoveryItemDto();
        described.setResource(Resource.CRYPTOGRAPHIC_KEY);
        described.setKeyDescription(new KeyDescriptionDto(KeyAlgorithm.ECDSA, "P-384", 384));
        DiscoveryItemDto undescribed = new DiscoveryItemDto();
        undescribed.setResource(Resource.CRYPTOGRAPHIC_KEY);

        // when
        String describedJson = mapper.writeValueAsString(described);
        String undescribedJson = mapper.writeValueAsString(undescribed);
        DiscoveryItemDto back = mapper.readValue(describedJson, DiscoveryItemDto.class);

        // then
        assertEquals(KeyAlgorithm.ECDSA, back.getKeyDescription().getAlgorithm());
        assertEquals("P-384", back.getKeyDescription().getParameterSet());
        assertEquals(384, back.getKeyDescription().getLength());
        assertFalse(undescribedJson.contains("keyDescription"), undescribedJson);
    }

    @Test
    void keepsTheDiscoveryItemConstructorWithoutTheKeyDescription() {
        // given
        // when
        DiscoveryItemDto item = new DiscoveryItemDto("uuid", null, 1L, "ref", null, Resource.CRYPTOGRAPHIC_KEY, null,
                true, false, null, List.of());

        // then
        assertEquals("ref", item.getUniqueRef());
        assertNull(item.getKeyDescription(), "the previous signature leaves the key description absent");
    }

    @SuppressWarnings("unchecked")
    private static Schema<?> resolvedProperty(Schema<?> schema, String property, Map<String, Schema> schemas) {
        if (schema == null) {
            return null;
        }
        if (schema.getProperties() != null && schema.getProperties().containsKey(property)) {
            return (Schema<?>) schema.getProperties().get(property);
        }
        if (schema.get$ref() != null) {
            return resolvedProperty(schemas.get(schema.get$ref().substring(schema.get$ref().lastIndexOf('/') + 1)),
                    property, schemas);
        }
        if (schema.getAllOf() != null) {
            return ((List<Schema<?>>) (List<?>) schema.getAllOf())
                    .stream()
                    .map(member -> resolvedProperty(member, property, schemas))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
