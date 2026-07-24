package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.interfaces.connector.cryptography.v2.KeyController;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class KeyDataV2OpenApiTest {

    private static final Set<String> FORBIDDEN_LEGACY_SCHEMAS = Set.of(
            "KeyData", "KeyFormat", "KeyValue", "KeyValueDto", "RawKeyValue", "PrkiKeyValue",
            "EprkiKeyValue", "CustomKeyValue", "SpkiKeyValue", "MetadataAttribute", "BaseAttribute",
            "AttributeContent");

    @Test
    void v2ResponseSchemasCannotReachLegacyKeyMaterialModels() throws Exception {
        Map<String, Schema> schemas = resolveAll(
                KeyDataResponseV2Dto.class,
                KeyPairDataResponseV2Dto.class,
                KeyOperationStatusResponseV2Dto.class,
                SecretKeyDataResponseV2Dto.class);

        assertTrue(schemas.keySet().stream().noneMatch(FORBIDDEN_LEGACY_SCHEMAS::contains),
                () -> "V2 key schemas reached legacy key schemas: " + schemas.keySet());
        assertTrue(schemas.containsKey("MetadataAttributeV2"),
                () -> "Resolved schemas: " + schemas.keySet());

        String schemaJson = new ObjectMapper().writeValueAsString(schemas);
        assertFalse(schemaJson.contains("EncryptedPrivateKeyInfo"));
        assertFalse(schemaJson.contains("PrivateKeyInfo"));
        assertFalse(schemaJson.contains("\"Raw\""));

        for (String responseSchema : Set.of(
                "KeyDataResponseV2Dto",
                "KeyPairDataResponseV2Dto",
                "KeyOperationStatusResponseV2Dto",
                "SecretKeyDataResponseV2Dto",
                "PrivateKeyDataResponseV2Dto",
                "PublicKeyDataResponseV2Dto")) {
            Schema<?> schema = schemas.get(responseSchema);
            assertNotNull(schema, () -> "Resolved schemas: " + schemas.keySet());
            assertEquals(Boolean.FALSE, schema.getAdditionalProperties(),
                    () -> responseSchema + " must reject unadvertised properties");
        }
    }

    @Test
    void heterogeneousKeyDataUsesExactlyThreeRoleSubtypes() {
        Map<String, Schema> schemas = resolveAll(KeyDataV2Dto.class);
        Schema<?> union = schemas.get("KeyDataV2");

        assertNotNull(union, () -> "Resolved schemas: " + schemas.keySet());
        assertNotEquals(Boolean.FALSE, union.getAdditionalProperties(),
                "The oneOf root must not reject properties declared by its closed role branches");
        assertNotNull(union.getDiscriminator());
        assertEquals("type", union.getDiscriminator().getPropertyName());
        assertNotNull(union.getOneOf());
        assertEquals(Set.of(
                        "#/components/schemas/SecretKeyDataV2Dto",
                        "#/components/schemas/PublicKeyDataV2Dto",
                        "#/components/schemas/PrivateKeyDataV2Dto"),
                union.getOneOf().stream().map(Schema::get$ref).collect(Collectors.toSet()));
        assertEquals(Map.of(
                        "Secret", "#/components/schemas/SecretKeyDataV2Dto",
                        "Public", "#/components/schemas/PublicKeyDataV2Dto",
                        "Private", "#/components/schemas/PrivateKeyDataV2Dto"),
                union.getDiscriminator().getMapping());

        Schema<?> roleSchema = schemas.get("KeyRoleV2");
        assertNotNull(roleSchema, () -> "Resolved schemas: " + schemas.keySet());
        assertEquals(List.of("Secret", "Public", "Private"), roleSchema.getEnum());

        assertClosedRole(schemas, "SecretKeyDataV2Dto", "Secret", false);
        assertClosedRole(schemas, "PublicKeyDataV2Dto", "Public", true);
        assertClosedRole(schemas, "PrivateKeyDataV2Dto", "Private", false);
    }

    @Test
    void secretEndpointPublishesSecretSpecificEnvelope() throws Exception {
        Method createSecretKey = KeyController.class.getMethod("createSecretKey", CreateKeyRequestV2Dto.class);
        ParameterizedType responseEntity = assertInstanceOf(
                ParameterizedType.class, createSecretKey.getGenericReturnType());

        assertEquals(ResponseEntity.class, responseEntity.getRawType());
        assertArrayEquals(new Object[]{SecretKeyDataResponseV2Dto.class}, responseEntity.getActualTypeArguments());

        Map<String, Schema> schemas = resolveAll(SecretKeyDataResponseV2Dto.class);
        assertTrue(schemas.containsKey("SecretKeyDataV2Dto"), () -> "Resolved schemas: " + schemas.keySet());
        assertFalse(schemas.containsKey("PublicKeyDataV2Dto"), () -> "Resolved schemas: " + schemas.keySet());
        assertFalse(schemas.containsKey("PrivateKeyDataV2Dto"), () -> "Resolved schemas: " + schemas.keySet());
        assertFalse(schemas.containsKey("SplitKeyDataV2"), () -> "Resolved schemas: " + schemas.keySet());
    }

    @Test
    void keyPairPublishesExactPublicAndPrivateRoles() throws Exception {
        Map<String, Schema> schemas = resolveAll(KeyPairDataResponseV2Dto.class);

        assertTrue(schemas.containsKey("PublicKeyDataV2Dto"), () -> "Resolved schemas: " + schemas.keySet());
        assertTrue(schemas.containsKey("PrivateKeyDataV2Dto"), () -> "Resolved schemas: " + schemas.keySet());
        assertFalse(schemas.containsKey("SecretKeyDataV2Dto"), () -> "Resolved schemas: " + schemas.keySet());
        assertFalse(schemas.containsKey("SplitKeyDataV2"), () -> "Resolved schemas: " + schemas.keySet());

        String privateSchemaJson = new ObjectMapper().writeValueAsString(
                resolveAll(PrivateKeyDataResponseV2Dto.class));
        String publicSchemaJson = new ObjectMapper().writeValueAsString(
                resolveAll(PublicKeyDataResponseV2Dto.class));
        assertFalse(privateSchemaJson.contains("publicKeySpki"));
        assertTrue(publicSchemaJson.contains("publicKeySpki"));
    }

    private static Map<String, Schema> resolveAll(Class<?>... types) {
        Map<String, Schema> schemas = new LinkedHashMap<>();
        for (Class<?> type : types) {
            ResolvedSchema resolved = ModelConverters.getInstance().resolveAsResolvedSchema(
                    new AnnotatedType(type).resolveAsRef(true));
            schemas.putAll(resolved.referencedSchemas);
        }
        return schemas;
    }

    @SuppressWarnings("unchecked")
    private static void assertClosedRole(Map<String, Schema> schemas, String schemaName,
                                         String role, boolean permitsSpki) {
        Schema<?> schema = schemas.get(schemaName);
        assertNotNull(schema, () -> "Resolved schemas: " + schemas.keySet());
        assertEquals(Boolean.FALSE, schema.getAdditionalProperties(),
                () -> schemaName + " must reject unadvertised properties");

        Schema<?> type = (Schema<?>) schema.getProperties().get("type");
        assertNotNull(type, () -> schemaName + " has no type property");
        assertEquals(List.of(role), type.getEnum(), () -> schemaName + " discriminator is not role-specific");
        assertNotNull(schema.getRequired(), () -> schemaName + " has no required properties");
        assertTrue(schema.getRequired().containsAll(Set.of("type", "algorithm", "length")),
                () -> schemaName + " does not require its complete descriptor: " + schema.getRequired());

        Set<String> properties = schema.getProperties().keySet();
        assertEquals(permitsSpki, properties.contains("publicKeySpki"));
        assertFalse(properties.contains("format"));
        assertFalse(properties.contains("value"));
    }
}
