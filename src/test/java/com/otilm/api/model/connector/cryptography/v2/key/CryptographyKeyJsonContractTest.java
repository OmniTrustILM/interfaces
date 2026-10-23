package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.connector.common.v2.OperationStatus;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Named.named;

class CryptographyKeyJsonContractTest {

    private final ObjectMapper mapper = JsonMapper
            .builder()
            .findAndAddModules()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    @Test
    void keyData_hasNoPublicTypeSetter() {
        // given
        String discriminatorSetterName = "setType";

        // when
        Executable findDiscriminatorSetter = () -> KeyDataV2Dto.class
                .getMethod(discriminatorSetterName, KeyTypeV2.class);

        // then
        assertThrows(NoSuchMethodException.class, findDiscriminatorSetter);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("creationResponses")
    void keyCreationResponse_roundTripsDiscriminatedSubtype(CreationResponseContract contract) throws Exception {
        // given
        KeyCreationResponseV2Dto response = contract.response();

        // when
        String json = mapper.writeValueAsString(response);
        KeyCreationResponseV2Dto roundTripped = mapper.readValue(json, KeyCreationResponseV2Dto.class);

        // then
        assertEquals(contract.wireCode(), mapper.readTree(json).get("keyRequestType").textValue());
        assertInstanceOf(contract.type(), roundTripped);
        assertEquals(contract.requestType(), roundTripped.getKeyRequestType());
    }

    static Stream<Named<CreationResponseContract>> creationResponses() {
        return Stream
                .of(named("secret key",
                        new CreationResponseContract(new SecretKeyDataResponseV2Dto(), SecretKeyDataResponseV2Dto.class,
                                KeyRequestType.SECRET, KeyRequestType.Codes.SECRET)),
                        named("key pair",
                                new CreationResponseContract(new KeyPairDataResponseV2Dto(),
                                        KeyPairDataResponseV2Dto.class, KeyRequestType.KEY_PAIR,
                                        KeyRequestType.Codes.KEY_PAIR)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("creationStatusResponses")
    void keyCreationStatus_roundTripsDiscriminatedSubtype(CreationStatusContract contract) throws Exception {
        // given
        KeyCreationStatusResponseV2Dto response = contract.response();
        response.setStatus(OperationStatus.IN_PROGRESS);

        // when
        String json = mapper.writeValueAsString(response);
        KeyCreationStatusResponseV2Dto roundTripped = mapper.readValue(json, KeyCreationStatusResponseV2Dto.class);

        // then
        assertEquals(contract.wireCode(), mapper.readTree(json).get("keyRequestType").textValue());
        assertInstanceOf(contract.type(), roundTripped);
        assertEquals(OperationStatus.IN_PROGRESS, roundTripped.getStatus());
        assertEquals(contract.requestType(), roundTripped.getKeyRequestType());
    }

    static Stream<Named<CreationStatusContract>> creationStatusResponses() {
        return Stream
                .of(named("secret key",
                        new CreationStatusContract(new SecretKeyOperationStatusResponseV2Dto(),
                                SecretKeyOperationStatusResponseV2Dto.class, KeyRequestType.SECRET,
                                KeyRequestType.Codes.SECRET)),
                        named("key pair",
                                new CreationStatusContract(new KeyPairOperationStatusResponseV2Dto(),
                                        KeyPairOperationStatusResponseV2Dto.class, KeyRequestType.KEY_PAIR,
                                        KeyRequestType.Codes.KEY_PAIR)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("listedKeys")
    void listedKey_roundTripsDiscriminatedSubtype(ListedKeyContract contract) throws Exception {
        // given
        ListedKeyV2Dto key = contract.key();
        key.setLabel("signing");

        // when
        String json = mapper.writeValueAsString(key);
        ListedKeyV2Dto roundTripped = mapper.readValue(json, ListedKeyV2Dto.class);

        // then
        assertEquals(contract.wireCode(), mapper.readTree(json).get("keyRequestType").textValue());
        assertInstanceOf(contract.type(), roundTripped);
        assertEquals("signing", roundTripped.getLabel());
    }

    static Stream<Named<ListedKeyContract>> listedKeys() {
        return Stream
                .of(named("secret key",
                        new ListedKeyContract(new ListedSecretKeyV2Dto(), ListedSecretKeyV2Dto.class,
                                KeyRequestType.Codes.SECRET)),
                        named("key pair", new ListedKeyContract(new ListedKeyPairV2Dto(), ListedKeyPairV2Dto.class,
                                KeyRequestType.Codes.KEY_PAIR)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("polymorphicBases")
    void polymorphicBase_rejectsUnknownDiscriminator(PolymorphicBase base) {
        // given
        String unknownDiscriminator = "unsupported-key-request-type";
        String json = "{\"keyRequestType\":\"" + unknownDiscriminator + "\"}";

        // when
        Executable deserialize = () -> mapper.readValue(json, base.type());

        // then
        assertThrows(JsonProcessingException.class, deserialize);
    }

    static Stream<Named<PolymorphicBase>> polymorphicBases() {
        return Stream
                .of(named("creation response", new PolymorphicBase(KeyCreationResponseV2Dto.class)),
                        named("creation status", new PolymorphicBase(KeyCreationStatusResponseV2Dto.class)),
                        named("listed key", new PolymorphicBase(ListedKeyV2Dto.class)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("closedKeyDtos")
    void closedKeyDto_rejectsUnknownJsonProperty(ClosedDto contract) {
        // given
        String jsonWithUnknownProperty = contract.json().replace("}", ",\"legacyKeyData\":true}");

        // when
        Executable deserialize = () -> mapper.readValue(jsonWithUnknownProperty, contract.type());

        // then
        assertThrows(JsonProcessingException.class, deserialize);
    }

    static Stream<Named<ClosedDto>> closedKeyDtos() {
        return Stream
                .of(closed("key operation response", KeyOperationResponseV2Dto.class, "{}"),
                        closed("secret key descriptor", KeyDataV2Dto.class,
                                "{\"type\":\"Secret\",\"algorithm\":\"RSA\",\"length\":2048}"),
                        closed("private key descriptor", KeyDataV2Dto.class,
                                "{\"type\":\"Private\",\"algorithm\":\"RSA\",\"length\":2048}"),
                        closed("public key descriptor", KeyDataV2Dto.class,
                                "{\"type\":\"Public\",\"algorithm\":\"RSA\",\"length\":2048}"),
                        closed("secret key response", KeyCreationResponseV2Dto.class,
                                "{\"keyRequestType\":\"secret\"}"),
                        closed("key pair response", KeyCreationResponseV2Dto.class, "{\"keyRequestType\":\"keyPair\"}"),
                        closed("private key response", PrivateKeyDataResponseV2Dto.class, "{}"),
                        closed("public key response", PublicKeyDataResponseV2Dto.class, "{}"),
                        closed("key destruction status", KeyDestructionStatusResponseV2Dto.class,
                                "{\"status\":\"inProgress\"}"),
                        closed("secret creation status", KeyCreationStatusResponseV2Dto.class,
                                "{\"keyRequestType\":\"secret\",\"status\":\"inProgress\"}"),
                        closed("key pair creation status", KeyCreationStatusResponseV2Dto.class,
                                "{\"keyRequestType\":\"keyPair\",\"status\":\"inProgress\"}"),
                        closed("listed secret key", ListedKeyV2Dto.class, "{\"keyRequestType\":\"secret\"}"),
                        closed("listed key pair", ListedKeyV2Dto.class, "{\"keyRequestType\":\"keyPair\"}"));
    }

    private static Named<ClosedDto> closed(String name, Class<?> type, String json) {
        return named(name, new ClosedDto(type, json));
    }

    private record CreationResponseContract(KeyCreationResponseV2Dto response, Class<?> type,
            KeyRequestType requestType, String wireCode) {
    }

    private record CreationStatusContract(KeyCreationStatusResponseV2Dto response, Class<?> type,
            KeyRequestType requestType, String wireCode) {
    }

    private record ListedKeyContract(ListedKeyV2Dto key, Class<?> type, String wireCode) {
    }

    private record PolymorphicBase(Class<?> type) {
    }

    private record ClosedDto(Class<?> type, String json) {
    }
}
