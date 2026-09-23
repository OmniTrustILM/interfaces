package com.otilm.api.model.connector.cryptography.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.connector.cryptography.v2.key.CreateKeyAttributesRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.CreateKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.DestroyKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyAttributesRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyResultRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.material.EncryptedKeyMaterialV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.CipherDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.RandomDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.VerifyDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.CipherDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureResultItemV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.VerificationResponseItemV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Named.named;

class CryptographyDtoClosedContractTest {

    private final ObjectMapper mapper = JsonMapper
            .builder()
            .findAndAddModules()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    @ParameterizedTest(name = "{0}")
    @MethodSource("closedDtos")
    void dto_rejectsUnknownJsonProperty(DtoContract contract) {
        // given
        String unknownProperty = "unsupportedProperty";
        String json = "{\"" + unknownProperty + "\":true}";

        // when
        Executable deserialize = () -> mapper.readValue(json, contract.type());

        // then
        assertThrows(JsonProcessingException.class, deserialize);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("closedDtos")
    void dtoSchema_publishesAdditionalPropertiesFalse(DtoContract contract) {
        // given
        Map<String, Schema> schemas = openApi31Schemas(contract.type());

        // when
        Schema<?> dtoSchema = schemas.get(contract.type().getSimpleName());

        // then
        assertNotNull(dtoSchema, "DTO schema must be generated");
        assertEquals(Boolean.FALSE, dtoSchema.getAdditionalProperties());
    }

    static Stream<Named<DtoContract>> closedDtos() {
        return Stream
                .of(dto("token-scoped request", TokenScopedRequestV2Dto.class),
                        dto("token-profile-scoped request", TokenProfileScopedRequestV2Dto.class),
                        dto("key-scoped request", KeyScopedRequestV2Dto.class),
                        dto("create-key-attributes request", CreateKeyAttributesRequestV2Dto.class),
                        dto("create-key request", CreateKeyRequestV2Dto.class),
                        dto("destroy-key request", DestroyKeyRequestV2Dto.class),
                        dto("operation-tracking request", OperationTrackingRequestV2Dto.class),
                        dto("cipher-data request", CipherDataRequestV2Dto.class),
                        dto("random-data request", RandomDataRequestV2Dto.class),
                        dto("sign-data request", SignDataRequestV2Dto.class),
                        dto("verify-data request", VerifyDataRequestV2Dto.class),
                        dto("cipher-data item", CipherDataV2Dto.class),
                        dto("signature-data item", SignatureDataV2Dto.class),
                        dto("signature-result item", SignatureResultItemV2Dto.class),
                        dto("verification-response item", VerificationResponseItemV2Dto.class),
                        dto("encrypted key material", EncryptedKeyMaterialV2Dto.class),
                        dto("import-key request", ImportKeyRequestV2Dto.class),
                        dto("import-key-attributes request", ImportKeyAttributesRequestV2Dto.class),
                        dto("import-key-result request", ImportKeyResultRequestV2Dto.class),
                        dto("importable key type", ImportableKeyTypeV2Dto.class),
                        dto("export-key request", ExportKeyRequestV2Dto.class),
                        dto("export-key response", ExportKeyResponseV2Dto.class),
                        dto("exportable key type", ExportableKeyTypeV2Dto.class));
    }

    private static Named<DtoContract> dto(String name, Class<?> type) {
        return named(name, new DtoContract(type));
    }

    private record DtoContract(Class<?> type) {
    }
}
