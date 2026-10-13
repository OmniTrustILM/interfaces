package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.enums.cryptography.EncryptionAlgorithm;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.OperationValidationResult;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AlgorithmAttributeIdentityTest {
    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final OperationResponseValidator VALIDATOR = new OperationResponseValidator(VALIDATORS.validator());

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void schema_acceptsOneReservedAttributeAmongProviderAttributes(AlgorithmKind kind) {
        // given
        List<BaseAttribute> schema = List
                .of(providerDefinition(kind), definition(kind), providerDefinition(kind, "anotherProviderOption"));

        // when
        OperationValidationResult result = validateSchema(kind, schema);

        // then
        assertTrue(result.isValid());
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void schema_rejectsReservedNameWithWrongUuid(AlgorithmKind kind) {
        // given
        UUID wrongUuid = UUID.randomUUID();
        DataAttributeV3 reserved = definition(kind);
        reserved.setUuid(wrongUuid.toString());
        List<BaseAttribute> schema = List.of(providerDefinition(kind), reserved);

        // when
        OperationValidationResult result = validateSchema(kind, schema);

        // then
        assertFalse(result.isValid());
        assertIdentifiesReservedAttribute(kind, result.getCause().getMessage());
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void schema_rejectsReservedUuidWithWrongName(AlgorithmKind kind) {
        // given
        String wrongName = "renamedAlgorithm";
        DataAttributeV3 reserved = definition(kind);
        reserved.setName(wrongName);
        List<BaseAttribute> schema = List.of(providerDefinition(kind), reserved);

        // when
        OperationValidationResult result = validateSchema(kind, schema);

        // then
        assertFalse(result.isValid());
        assertIdentifiesReservedAttribute(kind, result.getCause().getMessage());
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void schema_rejectsProviderAttributeReusingReservedUuid(AlgorithmKind kind) {
        // given
        DataAttributeV3 reserved = definition(kind);
        DataAttributeV3 provider = providerDefinition(kind);
        provider.setUuid(reserved.getUuid());
        List<BaseAttribute> schema = List.of(provider, reserved);

        // when
        OperationValidationResult result = validateSchema(kind, schema);

        // then
        assertFalse(result.isValid());
        assertIdentifiesReservedAttribute(kind, result.getCause().getMessage());
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void schema_rejectsProviderAttributeReusingReservedName(AlgorithmKind kind) {
        // given
        DataAttributeV3 reserved = definition(kind);
        DataAttributeV3 provider = providerDefinition(kind);
        provider.setName(reserved.getName());
        List<BaseAttribute> schema = List.of(provider, reserved);

        // when
        OperationValidationResult result = validateSchema(kind, schema);

        // then
        assertFalse(result.isValid());
        assertIdentifiesReservedAttribute(kind, result.getCause().getMessage());
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void schema_rejectsDuplicateReservedUuidAndName(AlgorithmKind kind) {
        // given
        List<BaseAttribute> schema = List.of(definition(kind), definition(kind));

        // when
        OperationValidationResult result = validateSchema(kind, schema);

        // then
        assertFalse(result.isValid());
        assertIdentifiesReservedAttribute(kind, result.getCause().getMessage());
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void schema_acceptsDuplicateUuidOnUnrelatedProviderAttributes(AlgorithmKind kind) {
        // given
        DataAttributeV3 reserved = definition(kind);
        DataAttributeV3 first = providerDefinition(kind);
        DataAttributeV3 second = providerDefinition(kind, "anotherProviderOption");
        second.setUuid(first.getUuid());
        List<BaseAttribute> schema = List.of(first, reserved, second);

        // when
        OperationValidationResult result = validateSchema(kind, schema);

        // then
        assertTrue(result.isValid());
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void schema_acceptsDuplicateNameOnUnrelatedProviderAttributes(AlgorithmKind kind) {
        // given
        DataAttributeV3 reserved = definition(kind);
        DataAttributeV3 first = providerDefinition(kind);
        DataAttributeV3 second = providerDefinition(kind, "anotherProviderOption");
        second.setName(first.getName());
        List<BaseAttribute> schema = List.of(first, reserved, second);

        // when
        OperationValidationResult result = validateSchema(kind, schema);

        // then
        assertTrue(result.isValid());
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void selection_readsAlgorithmFromOneReservedAttributeAmongProviderAttributes(AlgorithmKind kind) {
        // given
        Object expected = expectedAlgorithm(kind);
        List<RequestAttributeV3> attributes = List
                .of(providerRequest(kind), request(kind), providerRequest(kind, "anotherProviderOption"));

        // when
        Object selected = readSelection(kind, attributes);

        // then
        assertEquals(expected, selected);
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void selection_rejectsReservedNameWithWrongUuid(AlgorithmKind kind) {
        // given
        UUID wrongUuid = UUID.randomUUID();
        RequestAttributeV3 reserved = request(kind);
        reserved.setUuid(wrongUuid);
        List<RequestAttributeV3> attributes = List.of(providerRequest(kind), reserved);

        // when
        Executable read = () -> readSelection(kind, attributes);

        // then
        ValidationException exception = assertThrows(ValidationException.class, read);
        assertIdentifiesReservedAttribute(kind, exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void selection_rejectsReservedUuidWithWrongName(AlgorithmKind kind) {
        // given
        String wrongName = "renamedAlgorithm";
        RequestAttributeV3 reserved = request(kind);
        reserved.setName(wrongName);
        List<RequestAttributeV3> attributes = List.of(providerRequest(kind), reserved);

        // when
        Executable read = () -> readSelection(kind, attributes);

        // then
        ValidationException exception = assertThrows(ValidationException.class, read);
        assertIdentifiesReservedAttribute(kind, exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void selection_rejectsProviderAttributeReusingReservedUuid(AlgorithmKind kind) {
        // given
        RequestAttributeV3 reserved = request(kind);
        RequestAttributeV3 provider = providerRequest(kind);
        provider.setUuid(reserved.getUuid());
        List<RequestAttributeV3> attributes = List.of(provider, reserved);

        // when
        Executable read = () -> readSelection(kind, attributes);

        // then
        ValidationException exception = assertThrows(ValidationException.class, read);
        assertIdentifiesReservedAttribute(kind, exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void selection_rejectsProviderAttributeReusingReservedName(AlgorithmKind kind) {
        // given
        RequestAttributeV3 reserved = request(kind);
        RequestAttributeV3 provider = providerRequest(kind);
        provider.setName(reserved.getName());
        List<RequestAttributeV3> attributes = List.of(provider, reserved);

        // when
        Executable read = () -> readSelection(kind, attributes);

        // then
        ValidationException exception = assertThrows(ValidationException.class, read);
        assertIdentifiesReservedAttribute(kind, exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void selection_rejectsDuplicateReservedUuidAndName(AlgorithmKind kind) {
        // given
        List<RequestAttributeV3> attributes = List.of(request(kind), request(kind));

        // when
        Executable read = () -> readSelection(kind, attributes);

        // then
        ValidationException exception = assertThrows(ValidationException.class, read);
        assertIdentifiesReservedAttribute(kind, exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void selection_readsAlgorithmDespiteDuplicateUuidOnUnrelatedProviderAttributes(AlgorithmKind kind) {
        // given
        Object expected = expectedAlgorithm(kind);
        RequestAttributeV3 reserved = request(kind);
        RequestAttributeV3 first = providerRequest(kind);
        RequestAttributeV3 second = providerRequest(kind, "anotherProviderOption");
        second.setUuid(first.getUuid());
        List<RequestAttributeV3> attributes = List.of(first, reserved, second);

        // when
        Object selected = readSelection(kind, attributes);

        // then
        assertEquals(expected, selected);
    }

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void selection_readsAlgorithmDespiteDuplicateNameOnUnrelatedProviderAttributes(AlgorithmKind kind) {
        // given
        Object expected = expectedAlgorithm(kind);
        RequestAttributeV3 reserved = request(kind);
        RequestAttributeV3 first = providerRequest(kind);
        RequestAttributeV3 second = providerRequest(kind, "anotherProviderOption");
        second.setName(first.getName());
        List<RequestAttributeV3> attributes = List.of(first, reserved, second);

        // when
        Object selected = readSelection(kind, attributes);

        // then
        assertEquals(expected, selected);
    }

    private static void assertIdentifiesReservedAttribute(AlgorithmKind kind, String message) {
        DataAttributeV3 expected = definition(kind);
        assertTrue(message.contains(expected.getUuid()), message);
        assertTrue(message.contains(expected.getName()), message);
    }

    private static OperationValidationResult validateSchema(AlgorithmKind kind, List<BaseAttribute> schema) {
        return kind == AlgorithmKind.SIGNATURE
                ? VALIDATOR.validateSignAttributeList(schema)
                : VALIDATOR.validateCipherAttributeList(schema);
    }

    private static Object readSelection(AlgorithmKind kind, List<RequestAttributeV3> attributes) {
        return kind == AlgorithmKind.SIGNATURE
                ? SignatureAlgorithmAttribute.selectedAlgorithm(attributes)
                : EncryptionAlgorithmAttribute.selectedAlgorithm(attributes);
    }

    private static Object expectedAlgorithm(AlgorithmKind kind) {
        return kind == AlgorithmKind.SIGNATURE
                ? SignatureAlgorithm.SHA256_WITH_RSA
                : EncryptionAlgorithm.RSA_OAEP_SHA256;
    }

    private static DataAttributeV3 providerDefinition(AlgorithmKind kind) {
        return providerDefinition(kind, "providerOption");
    }

    private static DataAttributeV3 providerDefinition(AlgorithmKind kind, String name) {
        DataAttributeV3 provider = definition(kind);
        provider.setUuid(UUID.randomUUID().toString());
        provider.setName(name);
        return provider;
    }

    private static RequestAttributeV3 providerRequest(AlgorithmKind kind) {
        return providerRequest(kind, "providerOption");
    }

    private static RequestAttributeV3 providerRequest(AlgorithmKind kind, String name) {
        RequestAttributeV3 provider = request(kind);
        provider.setUuid(UUID.randomUUID());
        provider.setName(name);
        return provider;
    }

    private static RequestAttributeV3 request(AlgorithmKind kind) {
        return kind == AlgorithmKind.SIGNATURE
                ? SignatureAlgorithmAttribute.request(SignatureAlgorithm.SHA256_WITH_RSA)
                : EncryptionAlgorithmAttribute.request(EncryptionAlgorithm.RSA_OAEP_SHA256);
    }

    private static DataAttributeV3 definition(AlgorithmKind kind) {
        return kind == AlgorithmKind.SIGNATURE
                ? SignatureAlgorithmAttribute.definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA))
                : EncryptionAlgorithmAttribute.definition(List.of(EncryptionAlgorithm.RSA_OAEP_SHA256));
    }

    enum AlgorithmKind {
        SIGNATURE,
        CIPHER
    }
}
