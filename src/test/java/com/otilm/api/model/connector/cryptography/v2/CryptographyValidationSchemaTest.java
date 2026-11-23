package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.connector.cryptography.v2.key.CreateKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyOperationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyPairDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.PrivateKeyDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.TransferableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.RandomDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataResponseV2Dto;
import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Named.named;

class CryptographyValidationSchemaTest {

    @Test
    void importKeyRequestSchema_publishesKeyImportIdLengthBounds() {
        // given
        int minimumLength = 1;
        int maximumLength = 256;
        Map<String, Schema> schemas = openApi31Schemas(ImportKeyRequestV2Dto.class);

        // when
        Schema<?> keyImportId = property(schemas, ImportKeyRequestV2Dto.class, "keyImportId");

        // then
        assertEquals(minimumLength, keyImportId.getMinLength());
        assertEquals(maximumLength, keyImportId.getMaxLength());
    }

    @Test
    void createKeyRequestSchema_publishesKeyCreationIdLengthBounds() {
        // given
        int minimumLength = 1;
        int maximumLength = 256;
        Map<String, Schema> schemas = openApi31Schemas(CreateKeyRequestV2Dto.class);

        // when
        Schema<?> keyCreationId = property(schemas, CreateKeyRequestV2Dto.class, "keyCreationId");

        // then
        assertEquals(minimumLength, keyCreationId.getMinLength());
        assertEquals(maximumLength, keyCreationId.getMaxLength());
    }

    @Test
    void randomDataRequestSchema_publishesLengthBounds() {
        // given
        BigDecimal minimumLength = BigDecimal.ONE;
        BigDecimal maximumLength = BigDecimal.valueOf(RandomDataRequestV2Dto.MAX_LENGTH);
        Map<String, Schema> schemas = openApi31Schemas(RandomDataRequestV2Dto.class);

        // when
        Schema<?> length = property(schemas, RandomDataRequestV2Dto.class, "length");

        // then
        assertEquals(minimumLength, length.getMinimum());
        assertEquals(maximumLength, length.getMaximum());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("keyDataSchemas")
    void keyDataSchema_publishesPositiveLength(Class<?> keyDataType) {
        // given
        BigDecimal minimumLength = BigDecimal.ONE;
        Map<String, Schema> schemas = openApi31Schemas(keyDataType);

        // when
        Schema<?> length = property(schemas, keyDataType, "length");

        // then
        assertEquals(minimumLength, length.getMinimum());
    }

    static Stream<Named<Class<?>>> keyDataSchemas() {
        return Stream
                .of(named("secret key", SecretKeyDataV2Dto.class), named("private key", PrivateKeyDataV2Dto.class));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("conditionallyNonEmptyResponseProperties")
    void responseSchema_publishesMinItemsWithoutRequiringConditionalProperty(SchemaPropertyContract contract) {
        // given
        int minimumItems = 1;
        Map<String, Schema> schemas = openApi31Schemas(contract.type());
        Schema<?> response = schemas.get(contract.type().getSimpleName());

        // when
        Schema<?> property = property(response, contract.property());

        // then
        assertEquals(minimumItems, property.getMinItems());
        assertFalse(response.getRequired() != null && response.getRequired().contains(contract.property()),
                contract.property() + " must remain conditionally optional");
    }

    static Stream<Named<SchemaPropertyContract>> conditionallyNonEmptyResponseProperties() {
        return Stream
                .of(responseProperty("key operation metadata", KeyOperationResponseV2Dto.class, "operationMeta"),
                        responseProperty("secret-key operation metadata", SecretKeyDataResponseV2Dto.class,
                                "operationMeta"),
                        responseProperty("secret-key metadata", SecretKeyDataResponseV2Dto.class, "keyMeta"),
                        responseProperty("key-pair operation metadata", KeyPairDataResponseV2Dto.class,
                                "operationMeta"),
                        responseProperty("key-pair metadata", KeyPairDataResponseV2Dto.class, "keyPairMeta"),
                        responseProperty("synchronous signatures", SignDataResponseV2Dto.class, "signatures"),
                        responseProperty("signing operation metadata", SignDataResponseV2Dto.class, "operationMeta"));
    }

    private static Named<SchemaPropertyContract> responseProperty(String name, Class<?> type, String property) {
        return named(name, new SchemaPropertyContract(type, property));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("keyTypeDeclarationSchemas")
    void keyTypeDeclarationSchema_isPublishedInFullAndStaysClosed(Class<?> declarationType) {
        // given
        Map<String, Schema> schemas = openApi31Schemas(declarationType);

        // when
        Schema<?> declaration = schemas.get(declarationType.getSimpleName());

        // then
        assertNotNull(declaration, "schema must be generated for " + declarationType.getSimpleName());
        assertNotNull(property(declaration, "keyRequestType"));
        assertNotNull(property(declaration, "algorithms"));
        assertEquals(Boolean.FALSE, declaration.getAdditionalProperties());
        assertFalse(schemas.containsKey(TransferableKeyTypeV2Dto.class.getSimpleName()),
                "the shared base must not reach the published document as a schema of its own");
    }

    static Stream<Named<Class<?>>> keyTypeDeclarationSchemas() {
        return Stream
                .of(named("importable key type", ImportableKeyTypeV2Dto.class),
                        named("exportable key type", ExportableKeyTypeV2Dto.class));
    }

    private static Schema<?> property(Map<String, Schema> schemas, Class<?> type, String property) {
        Schema<?> schema = schemas.get(type.getSimpleName());
        assertNotNull(schema, "schema must be generated for " + type.getSimpleName());
        return property(schema, property);
    }

    private static Schema<?> property(Schema<?> schema, String property) {
        assertNotNull(schema.getProperties(), "schema properties must be generated");
        Schema<?> propertySchema = (Schema<?>) schema.getProperties().get(property);
        assertNotNull(propertySchema, property + " must be published");
        return propertySchema;
    }

    private record SchemaPropertyContract(Class<?> type, String property) {
    }
}
