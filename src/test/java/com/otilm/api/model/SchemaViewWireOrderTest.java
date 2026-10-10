package com.otilm.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.connector.common.v2.OperationStatus;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyPairDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyPairOperationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyOperationStatusResponseV2Dto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPrivateKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPublicKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validSecretKeyDataResponse;
import static com.otilm.util.builders.DataAttributeV3Builder.aDataAttribute;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Named.named;

/**
 * A schema view's {@code @JsonPropertyOrder} can reorder the JSON of the DTOs beneath it. Clients hash that JSON as a
 * sorting mapper writes it.
 */
class SchemaViewWireOrderTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();
    private final ObjectMapper sortingMapper = JsonMapper
            .builder()
            .findAndAddModules()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .build();

    @ParameterizedTest(name = "{0}")
    @MethodSource("dtosBeneathOrderedViews")
    void dtoBeneathAnOrderedView_writesItsPropertiesInOrder(WireOrder wireOrder) throws Exception {
        // given
        Object dto = wireOrder.dto();

        // when
        List<String> propertyNames = propertyNames(mapper, dto);

        // then
        assertEquals(wireOrder.propertyNames(), propertyNames);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("dtosBeneathOrderedViews")
    void dtoBeneathAnOrderedView_writesItsPropertiesAlphabetically_whenTheMapperSorts(WireOrder wireOrder)
            throws Exception {
        // given
        Object dto = wireOrder.dto();

        // when
        List<String> propertyNames = propertyNames(sortingMapper, dto);

        // then
        assertEquals(wireOrder.propertyNames().stream().sorted().toList(), propertyNames);
    }

    static Stream<Named<WireOrder>> dtosBeneathOrderedViews() {
        return Stream
                .of(wireOrder("v2 content", new StringAttributeContentV2("reference", "data"),
                        List.of("reference", "data")),
                        wireOrder("v3 content", new StringAttributeContentV3("reference", "data"),
                                List.of("reference", "data", "contentType")),
                        wireOrder("data attribute", aDataAttribute().build(), List
                                .of("uuid", "name", "version", "type", "contentType", "properties", "schemaVersion")),
                        wireOrder("secret key creation response", fullSecretKeyCreationResponse(),
                                List.of("operationMeta", "keyData", "keyMeta", "keyRequestType")),
                        wireOrder("key pair creation response", fullKeyPairCreationResponse(),
                                List
                                        .of("operationMeta", "publicKeyData", "privateKeyData", "keyPairMeta",
                                                "keyRequestType")),
                        wireOrder("secret key creation status",
                                fullCreationStatus(new SecretKeyOperationStatusResponseV2Dto()),
                                List.of("status", "reason", "result", "keyRequestType")),
                        wireOrder("key pair creation status",
                                fullCreationStatus(new KeyPairOperationStatusResponseV2Dto()),
                                List.of("status", "reason", "result", "keyRequestType")));
    }

    private static SecretKeyDataResponseV2Dto fullSecretKeyCreationResponse() {
        SecretKeyDataResponseV2Dto response = validSecretKeyDataResponse();
        response.setOperationMeta(validMetadata());
        return response;
    }

    private static KeyPairDataResponseV2Dto fullKeyPairCreationResponse() {
        KeyPairDataResponseV2Dto response = new KeyPairDataResponseV2Dto();
        response.setOperationMeta(validMetadata());
        response.setPublicKeyData(validPublicKeyDataResponse());
        response.setPrivateKeyData(validPrivateKeyDataResponse());
        response.setKeyPairMeta(validMetadata());
        return response;
    }

    private static KeyCreationStatusResponseV2Dto fullCreationStatus(SecretKeyOperationStatusResponseV2Dto status) {
        status.setResult(fullSecretKeyCreationResponse());
        return withStatusAndReason(status);
    }

    private static KeyCreationStatusResponseV2Dto fullCreationStatus(KeyPairOperationStatusResponseV2Dto status) {
        status.setResult(fullKeyPairCreationResponse());
        return withStatusAndReason(status);
    }

    private static KeyCreationStatusResponseV2Dto withStatusAndReason(KeyCreationStatusResponseV2Dto status) {
        status.setStatus(OperationStatus.FAILED);
        status.setReason("reason");
        return status;
    }

    private static List<String> propertyNames(ObjectMapper mapper, Object dto) throws JsonProcessingException {
        JsonNode json = mapper.readTree(mapper.writeValueAsString(dto));
        List<String> propertyNames = new ArrayList<>();
        json.fieldNames().forEachRemaining(propertyNames::add);
        return propertyNames;
    }

    private static Named<WireOrder> wireOrder(String name, Object dto, List<String> propertyNames) {
        return named(name, new WireOrder(dto, propertyNames));
    }

    private record WireOrder(Object dto, List<String> propertyNames) {
    }
}
