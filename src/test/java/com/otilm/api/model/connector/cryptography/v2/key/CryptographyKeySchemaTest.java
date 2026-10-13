package com.otilm.api.model.connector.cryptography.v2.key;

import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class CryptographyKeySchemaTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("creationUnions")
    void keyCreationSchema_publishesDiscriminatedUnionWithOpenCompleteParent(UnionContract contract) {
        // given
        Map<String, Schema> schemas = openApi31Schemas(contract.root());

        // when
        Schema<?> union = schemas.get(contract.schemaName());

        // then
        assertDiscriminator(union, contract.discriminatorProperty(), contract.mapping());
        assertNull(union.getAdditionalProperties(), "union parent must not close over properties declared by its arms");
        assertNull(union.getRequired(), "concrete union arms own their required properties");
        assertNotNull(union.getProperties(), "union parent properties must be generated from its getters");
        assertEquals(contract.parentProperties(), union.getProperties().keySet());
        assertEquals(contract.members(), oneOfReferences(union));
        contract
                .members()
                .forEach(member -> assertEquals(Boolean.FALSE, schemas.get(member).getAdditionalProperties(),
                        member + " must remain closed"));
    }

    static Stream<Named<UnionContract>> creationUnions() {
        return Stream
                .of(named("creation response",
                        new UnionContract(KeyCreationResponseV2Dto.class, "KeyCreationResponse", "keyRequestType",
                                Map
                                        .of("secret", "#/components/schemas/SecretKeyDataResponseV2Dto", "keyPair",
                                                "#/components/schemas/KeyPairDataResponseV2Dto"),
                                Set.of("keyRequestType", "operationMeta"),
                                Set.of("SecretKeyDataResponseV2Dto", "KeyPairDataResponseV2Dto"))),
                        named("creation status", new UnionContract(KeyCreationStatusResponseV2Dto.class,
                                "KeyCreationStatusResponse", "keyRequestType",
                                Map
                                        .of("secret", "#/components/schemas/SecretKeyOperationStatusResponseV2Dto",
                                                "keyPair", "#/components/schemas/KeyPairOperationStatusResponseV2Dto"),
                                Set.of("keyRequestType", "status", "reason", "result"),
                                Set
                                        .of("SecretKeyOperationStatusResponseV2Dto",
                                                "KeyPairOperationStatusResponseV2Dto"))));
    }

    @Test
    void keyDataSchema_publishesDiscriminatedUnionWithOpenCompleteParent() {
        // given
        Map<String, Schema> schemas = openApi31Schemas(KeyDataV2Dto.class);

        // when
        Schema<?> union = schemas.get("KeyDataV2");

        // then
        assertDiscriminator(union, "type",
                Map
                        .of("Secret", "#/components/schemas/SecretKeyDataV2Dto", "Public",
                                "#/components/schemas/PublicKeyDataV2Dto", "Private",
                                "#/components/schemas/PrivateKeyDataV2Dto"));
        assertNull(union.getAdditionalProperties(), "union parent must allow properties declared by its arms");
        assertNull(union.getRequired(), "concrete key-data arms own their required properties");
        assertNotNull(union.getProperties(), "union parent properties must be generated from its getters");
        assertEquals(Set.of("type", "algorithm", "length", "metadata"), union.getProperties().keySet());
        assertTrue(schemas.containsKey("KeyTypeV2"), "KeyTypeV2 enum schema must be generated");
        assertFalse(schemas.containsKey("KeyRoleV2"), "legacy KeyRoleV2 enum schema must not be generated");
        assertEquals(Set.of("SecretKeyDataV2Dto", "PublicKeyDataV2Dto", "PrivateKeyDataV2Dto"), oneOfReferences(union));
        for (String subtype : Set.of("SecretKeyDataV2Dto", "PublicKeyDataV2Dto", "PrivateKeyDataV2Dto")) {
            assertTrue(resolvesRequiredProperty(schemas.get(subtype), "type", schemas), subtype + " requires type");
            assertTrue(resolvesRequiredProperty(schemas.get(subtype), "algorithm", schemas),
                    subtype + " requires algorithm");
            assertFalse(resolvesRequiredProperty(schemas.get(subtype), "length", schemas),
                    subtype + " must allow omission of length for PQC and Unknown");
            assertEquals(Boolean.FALSE, schemas.get(subtype).getAdditionalProperties(),
                    subtype + " must publish additionalProperties=false");
        }
    }

    @Test
    void publicKeySchema_requiresByteFormattedSpki() {
        // given
        Map<String, Schema> schemas = openApi31Schemas(PublicKeyDataV2Dto.class);
        Schema<?> publicKey = schemas.get("PublicKeyDataV2Dto");

        // when
        Schema<?> spki = resolvesProperty(publicKey, "publicKeySpki", schemas);

        // then
        assertNotNull(spki, "publicKeySpki must be published");
        assertEquals("string", spki.getType());
        assertEquals("byte", spki.getFormat());
        assertTrue(resolvesRequiredProperty(publicKey, "publicKeySpki", schemas));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("closedEnvelopeSchemas")
    void keyEnvelopeSchema_publishesAdditionalPropertiesFalse(SchemaContract contract) {
        // given
        Map<String, Schema> schemas = openApi31Schemas(contract.root());

        // when
        Schema<?> schema = schemas.get(contract.schemaName());

        // then
        assertNotNull(schema, "Expected " + contract.schemaName() + "; found " + schemas.keySet());
        assertEquals(Boolean.FALSE, schema.getAdditionalProperties());
    }

    static Stream<Named<SchemaContract>> closedEnvelopeSchemas() {
        return Stream
                .of(schema("operation response", KeyOperationResponseV2Dto.class, "KeyOperationResponseV2Dto"),
                        schema("secret response", SecretKeyDataResponseV2Dto.class, "SecretKeyDataResponseV2Dto"),
                        schema("key pair response", KeyPairDataResponseV2Dto.class, "KeyPairDataResponseV2Dto"),
                        schema("private response", PrivateKeyDataResponseV2Dto.class, "PrivateKeyDataResponseV2Dto"),
                        schema("public response", PublicKeyDataResponseV2Dto.class, "PublicKeyDataResponseV2Dto"),
                        schema("destruction status", KeyDestructionStatusResponseV2Dto.class,
                                "KeyDestructionStatusResponseV2Dto"),
                        schema("secret status", SecretKeyOperationStatusResponseV2Dto.class,
                                "SecretKeyOperationStatusResponseV2Dto"),
                        schema("key pair status", KeyPairOperationStatusResponseV2Dto.class,
                                "KeyPairOperationStatusResponseV2Dto"));
    }

    private static Named<SchemaContract> schema(String name, Class<?> root, String schemaName) {
        return named(name, new SchemaContract(root, schemaName));
    }

    private static void assertDiscriminator(Schema<?> schema, String property, Map<String, String> mapping) {
        assertNotNull(schema, "discriminated union schema must be generated");
        Discriminator discriminator = schema.getDiscriminator();
        assertNotNull(discriminator, "discriminator must be generated");
        assertEquals(property, discriminator.getPropertyName());
        assertEquals(mapping, discriminator.getMapping());
    }

    private static Set<String> oneOfReferences(Schema<?> schema) {
        assertNotNull(schema.getOneOf(), "oneOf members must be generated");
        return schema
                .getOneOf()
                .stream()
                .map(Schema::get$ref)
                .map(reference -> reference.substring(reference.lastIndexOf('/') + 1))
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    private static boolean resolvesRequiredProperty(Schema<?> schema, String property, Map<String, Schema> schemas) {
        if (schema == null) {
            return false;
        }
        if (schema.getRequired() != null && schema.getRequired().contains(property)) {
            return true;
        }
        if (schema.get$ref() != null) {
            String name = schema.get$ref().substring(schema.get$ref().lastIndexOf('/') + 1);
            return resolvesRequiredProperty(schemas.get(name), property, schemas);
        }
        if (schema.getAllOf() != null) {
            return ((List<Schema<?>>) (List<?>) schema.getAllOf())
                    .stream()
                    .anyMatch(member -> resolvesRequiredProperty(member, property, schemas));
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static Schema<?> resolvesProperty(Schema<?> schema, String property, Map<String, Schema> schemas) {
        if (schema == null) {
            return null;
        }
        if (schema.getProperties() != null && schema.getProperties().containsKey(property)) {
            return (Schema<?>) schema.getProperties().get(property);
        }
        if (schema.get$ref() != null) {
            String name = schema.get$ref().substring(schema.get$ref().lastIndexOf('/') + 1);
            return resolvesProperty(schemas.get(name), property, schemas);
        }
        if (schema.getAllOf() != null) {
            for (Schema<?> member : (List<Schema<?>>) (List<?>) schema.getAllOf()) {
                Schema<?> resolved = resolvesProperty(member, property, schemas);
                if (resolved != null) {
                    return resolved;
                }
            }
        }
        return null;
    }

    private record UnionContract(Class<?> root, String schemaName, String discriminatorProperty,
            Map<String, String> mapping, Set<String> parentProperties, Set<String> members) {
    }

    private record SchemaContract(Class<?> root, String schemaName) {
    }
}
