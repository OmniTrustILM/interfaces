package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.enums.cryptography.EncryptionAlgorithm;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.OperationValidationResult;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Guards case-insensitive reserved UUID identity across algorithm schema endpoints.
 */
class AlgorithmAttributeUuidCaseTest {
    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final OperationResponseValidator VALIDATOR = new OperationResponseValidator(VALIDATORS.validator());

    @ParameterizedTest
    @MethodSource("operationsAndUuidCases")
    void schema_acceptsReservedUuidWithDifferentCase(Operation operation, String reservedUuid) {
        // given
        DataAttributeV3 reserved = definition(operation);
        reserved.setUuid(reservedUuid);
        List<BaseAttribute> schema = List.of(reserved);

        // when
        OperationValidationResult result = validateSchema(operation, schema);

        // then
        assertTrue(result.isValid(), () -> String.valueOf(result.getCause()));
    }

    @ParameterizedTest
    @MethodSource("operationsAndUuidCases")
    void schema_rejectsProviderReusingReservedUuidWithDifferentCase(Operation operation, String reservedUuid) {
        // given
        String providerName = "providerOption";
        DataAttributeV3 reserved = definition(operation);
        DataAttributeV3 provider = definition(operation);
        provider.setName(providerName);
        provider.setUuid(reservedUuid);
        List<BaseAttribute> schema = List.of(provider, reserved);

        // when
        OperationValidationResult result = validateSchema(operation, schema);

        // then
        assertFalse(result.isValid());
        assertTrue(result.getCause().getMessage().contains("must declare exactly one v3"));
        assertTrue(result.getCause().getMessage().contains(reserved.getUuid()));
        assertTrue(result.getCause().getMessage().contains(reserved.getName()));
    }

    private static Stream<Arguments> operationsAndUuidCases() {
        return Arrays.stream(Operation.values()).flatMap(operation -> {
            String uuid = definition(operation).getUuid();
            String uppercaseUuid = uuid.toUpperCase(Locale.ROOT);
            String mixedCaseUuid = uppercaseUuid.substring(0, 18) + uuid.substring(18);
            return Stream
                    .of(arguments(operation, named("uppercase UUID", uppercaseUuid)),
                            arguments(operation, named("mixed-case UUID", mixedCaseUuid)));
        });
    }

    private static DataAttributeV3 definition(Operation operation) {
        return operation == Operation.CIPHER
                ? EncryptionAlgorithmAttribute.definition(List.of(EncryptionAlgorithm.RSA_OAEP_SHA256))
                : SignatureAlgorithmAttribute.definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA));
    }

    private static OperationValidationResult validateSchema(Operation operation, List<BaseAttribute> schema) {
        return switch (operation) {
            case SIGN -> VALIDATOR.validateSignAttributeList(schema);
            case VERIFY -> VALIDATOR.validateVerifyAttributeList(schema);
            case CIPHER -> VALIDATOR.validateCipherAttributeList(schema);
        };
    }

    private enum Operation {
        SIGN,
        VERIFY,
        CIPHER
    }
}
