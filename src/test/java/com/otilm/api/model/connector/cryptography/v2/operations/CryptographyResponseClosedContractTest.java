package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.connector.cryptography.v2.key.KeyOperationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusResponseV2Dto;
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

class CryptographyResponseClosedContractTest {

    private final ObjectMapper mapper = JsonMapper
            .builder()
            .findAndAddModules()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    @ParameterizedTest(name = "{0}")
    @MethodSource("closedResponses")
    void response_rejectsUnknownJsonProperty(ResponseContract contract) {
        // given
        String unknownProperty = "unsupportedProperty";
        String json = "{\"" + unknownProperty + "\":true}";

        // when
        Executable deserialize = () -> mapper.readValue(json, contract.type());

        // then
        assertThrows(JsonProcessingException.class, deserialize);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("closedResponses")
    void responseSchema_publishesAdditionalPropertiesFalse(ResponseContract contract) {
        // given
        Map<String, Schema> schemas = openApi31Schemas(contract.type());

        // when
        Schema<?> responseSchema = schemas.get(contract.type().getSimpleName());

        // then
        assertNotNull(responseSchema, "response schema must be generated");
        assertEquals(Boolean.FALSE, responseSchema.getAdditionalProperties());
    }

    static Stream<Named<ResponseContract>> closedResponses() {
        return Stream
                .of(response("sign response", SignDataResponseV2Dto.class),
                        response("encrypt response", EncryptDataResponseV2Dto.class),
                        response("decrypt response", DecryptDataResponseV2Dto.class),
                        response("verify response", VerifyDataResponseV2Dto.class),
                        response("random response", RandomDataResponseV2Dto.class),
                        response("sign status response", SignOperationStatusResponseV2Dto.class),
                        response("key operation response", KeyOperationResponseV2Dto.class),
                        response("token status response", TokenStatusResponseV2Dto.class));
    }

    private static Named<ResponseContract> response(String name, Class<?> type) {
        return named(name, new ResponseContract(type));
    }

    private record ResponseContract(Class<?> type) {
    }
}
