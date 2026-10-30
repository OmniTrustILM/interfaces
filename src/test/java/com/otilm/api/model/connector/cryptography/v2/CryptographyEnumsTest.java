package com.otilm.api.model.connector.cryptography.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.common.v2.OperationStatus;
import com.otilm.api.model.connector.cryptography.v2.key.KeyResolutionOutcome;
import com.otilm.api.model.connector.cryptography.v2.key.KeyTypeV2;
import com.otilm.api.model.connector.cryptography.v2.key.UnusableKeyReason;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.OperationEventType;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusV2;
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

class CryptographyEnumsTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @ParameterizedTest(name = "{0}")
    @MethodSource("platformEnumContracts")
    void enum_exposesStableCodeLabelAndDescription(PlatformEnumContract contract) {
        // given
        IPlatformEnum enumValue = contract.value();

        // when
        String code = enumValue.getCode();

        // then
        assertEquals(contract.code(), code);
        assertEquals(contract.label(), enumValue.getLabel());
        assertEquals(contract.description(), enumValue.getDescription());
    }

    static Stream<Named<PlatformEnumContract>> platformEnumContracts() {
        return Stream
                .of(contract(KeyRequestType.SECRET, "secret", "Secret key", null),
                        contract(KeyRequestType.KEY_PAIR, "keyPair", "Key pair", null),
                        contract(OperationExecutionMode.SYNCHRONOUS, "synchronous", "Synchronous",
                                "Complete the operation before returning the response"),
                        contract(OperationExecutionMode.ASYNCHRONOUS, "asynchronous", "Asynchronous",
                                "Return 202 Accepted with an operation tracking handle"),
                        contract(OperationStatus.IN_PROGRESS, "inProgress", "In progress",
                                "Operation is still running at the upstream system"),
                        contract(OperationStatus.COMPLETED, "completed", "Completed",
                                "Operation has reached terminal success"),
                        contract(OperationStatus.FAILED, "failed", "Failed", "Operation has reached terminal failure"),
                        contract(OperationStatus.CANCELLED, "cancelled", "Cancelled",
                                "Operation was deliberately terminated before completion"),
                        contract(UnusableKeyReason.UNADDRESSABLE, "unaddressable", "Unaddressable",
                                "The connector cannot name the key in a handle that addresses it alone"),
                        contract(UnusableKeyReason.UNSUPPORTED_TYPE, "unsupportedType", "Unsupported type",
                                "The key is of a type the connector cannot operate"),
                        contract(UnusableKeyReason.UNREADABLE, "unreadable", "Unreadable",
                                "The connector cannot read the key"),
                        contract(UnusableKeyReason.NOT_FOUND, "notFound", "Not found",
                                "The token no longer holds the key it counted"),
                        contract(KeyResolutionOutcome.FOUND, "found", "Found",
                                "The token holds one key under the handle"),
                        contract(KeyResolutionOutcome.NOT_FOUND, "notFound", "Not found",
                                "The token holds no key under the handle"),
                        contract(KeyResolutionOutcome.AMBIGUOUS, "ambiguous", "Ambiguous",
                                "The token holds more than one key under the handle"),
                        contract(KeyResolutionOutcome.UNSUPPORTED_TYPE, "unsupportedType", "Unsupported type",
                                "The token holds the key, and it is of a type the connector cannot operate"),
                        contract(KeyResolutionOutcome.UNREADABLE, "unreadable", "Unreadable",
                                "The token holds the key, and the connector cannot read it"),
                        contract(KeyResolutionOutcome.INVALID_HANDLE, "invalidHandle", "Invalid handle",
                                "The handle is not one this connector issues"),
                        contract(TokenStatusV2.CONNECTED, "Connected", "Connected",
                                "The token or its backing provider is reachable"),
                        contract(TokenStatusV2.DISCONNECTED, "Disconnected", "Disconnected",
                                "The token or its backing provider is not reachable"),
                        contract(TokenStatusV2.WARNING, "Warning", "Warning",
                                "The token is reachable, but a provider-observable condition requires attention"),
                        contract(TokenStatusV2.UNKNOWN, "Unknown", "Unknown",
                                "The connector cannot determine the token status"),
                        contract(OperationEventType.OPERATION_ACCEPTED, "operationAccepted", "Operation accepted",
                                "The signature activation module holds a request for the operation"),
                        contract(OperationEventType.AUTHORIZATION_REQUESTED, "authorizationRequested",
                                "Authorization requested",
                                "The module's authorization material for the operation is in flight"),
                        contract(OperationEventType.AUTHORIZATION_PROGRESS, "authorizationProgress",
                                "Authorization progress",
                                "Part of a multi-party authorization completed; the step may repeat"),
                        contract(OperationEventType.AUTHORIZATION_GRANTED, "authorizationGranted",
                                "Authorization granted",
                                "The module realized the sole-control decision inside the signature creation device"),
                        contract(OperationEventType.SIGNATURE_CREATED, "signatureCreated", "Signature created",
                                "The module created the signature over one item or the whole batch"),
                        contract(OperationEventType.REJECTED, "rejected", "Rejected",
                                "The module refused the authorization"),
                        contract(OperationEventType.EXPIRED, "expired", "Expired",
                                "The authorization lapsed before the signature was created"),
                        contract(OperationEventType.FAILED, "failed", "Failed",
                                "The operation failed for a technical reason"));
    }

    @Test
    void keyRequestTypeCodes_exposeStableDiscriminatorValues() {
        // given
        String expectedSecretCode = "secret";
        String expectedKeyPairCode = "keyPair";

        // when
        String secretCode = KeyRequestType.Codes.SECRET;
        String keyPairCode = KeyRequestType.Codes.KEY_PAIR;

        // then
        assertEquals(expectedSecretCode, secretCode);
        assertEquals(expectedKeyPairCode, keyPairCode);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("wireEnumContracts")
    void enum_roundTripsWireCode(WireEnumContract contract) throws JsonProcessingException {
        // given
        Object enumValue = contract.value();

        // when
        String json = mapper.writeValueAsString(enumValue);
        Object roundTripped = mapper.readValue(json, contract.type());

        // then
        assertEquals('"' + contract.code() + '"', json);
        assertEquals(enumValue, roundTripped);
    }

    static Stream<Named<WireEnumContract>> wireEnumContracts() {
        return Stream
                .of(Stream
                        .of(KeyRequestType.values())
                        .map(value -> wireContract(value, value.getCode(), KeyRequestType.class)),
                        Stream
                                .of(OperationExecutionMode.values())
                                .map(value -> wireContract(value, value.getCode(), OperationExecutionMode.class)),
                        Stream
                                .of(OperationStatus.values())
                                .map(value -> wireContract(value, value.getCode(), OperationStatus.class)),
                        Stream
                                .of(UnusableKeyReason.values())
                                .map(value -> wireContract(value, value.getCode(), UnusableKeyReason.class)),
                        Stream
                                .of(KeyResolutionOutcome.values())
                                .map(value -> wireContract(value, value.getCode(), KeyResolutionOutcome.class)),
                        Stream
                                .of(KeyTypeV2.values())
                                .map(value -> wireContract(value, value.getCode(), KeyTypeV2.class)),
                        Stream
                                .of(TokenStatusV2.values())
                                .map(value -> wireContract(value, value.getCode(), TokenStatusV2.class)),
                        Stream
                                .of(OperationEventType.values())
                                .map(value -> wireContract(value, value.getCode(), OperationEventType.class)))
                .flatMap(stream -> stream);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("unknownWireCodes")
    void enum_rejectsUnknownWireCode(UnknownWireCode unknown) {
        // given
        String unknownCodeJson = '"' + unknown.code() + '"';

        // when
        Executable deserialize = () -> mapper.readValue(unknownCodeJson, unknown.type());

        // then
        JsonProcessingException exception = assertThrows(JsonProcessingException.class, deserialize);
        assertInstanceOf(unknown.causeType(), rootCause(exception));
    }

    static Stream<Named<UnknownWireCode>> unknownWireCodes() {
        String unknownCode = "not-a-cryptography-code";
        return Stream
                .of(named("key request type",
                        new UnknownWireCode(KeyRequestType.class, unknownCode, ValidationException.class)),
                        named("execution mode",
                                new UnknownWireCode(OperationExecutionMode.class, unknownCode,
                                        ValidationException.class)),
                        named("operation status",
                                new UnknownWireCode(OperationStatus.class, unknownCode, ValidationException.class)),
                        named("unusable key reason",
                                new UnknownWireCode(UnusableKeyReason.class, unknownCode, ValidationException.class)),
                        named("key resolution outcome",
                                new UnknownWireCode(KeyResolutionOutcome.class, unknownCode,
                                        ValidationException.class)),
                        named("operation event type",
                                new UnknownWireCode(OperationEventType.class, unknownCode, ValidationException.class)),
                        named("key type",
                                new UnknownWireCode(KeyTypeV2.class, unknownCode, IllegalArgumentException.class)),
                        named("legacy split key type",
                                new UnknownWireCode(KeyTypeV2.class, "Split", IllegalArgumentException.class)),
                        named("token status",
                                new UnknownWireCode(TokenStatusV2.class, unknownCode, ValidationException.class)));
    }

    private static Named<PlatformEnumContract> contract(IPlatformEnum value, String code, String label,
            String description) {
        return named(value.getClass().getSimpleName() + '.' + value,
                new PlatformEnumContract(value, code, label, description));
    }

    private static Named<WireEnumContract> wireContract(Object value, String code, Class<?> type) {
        return named(type.getSimpleName() + '.' + value, new WireEnumContract(value, code, type));
    }

    private static Throwable rootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    private record PlatformEnumContract(IPlatformEnum value, String code, String label, String description) {
    }

    private record WireEnumContract(Object value, String code, Class<?> type) {
    }

    private record UnknownWireCode(Class<?> type, String code, Class<? extends Throwable> causeType) {
    }
}
