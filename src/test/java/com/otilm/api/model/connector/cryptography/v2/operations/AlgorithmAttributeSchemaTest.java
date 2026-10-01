package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.IntegerAttributeContentV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.common.enums.cryptography.EncryptionAlgorithm;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.OperationValidationResult;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class AlgorithmAttributeSchemaTest {
    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final OperationResponseValidator VALIDATOR = new OperationResponseValidator(VALIDATORS.validator());

    @ParameterizedTest
    @EnumSource(AlgorithmKind.class)
    void schema_acceptsAllSupportedAlgorithmCodes(AlgorithmKind kind) {
        // given
        DataAttributeV3 definition = definition(kind);

        // when
        OperationValidationResult result = validate(kind, definition);

        // then
        assertTrue(result.isValid());
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("malformedSchemas")
    void schema_rejectsInconsistentMetadataAndMixedOptions(AlgorithmKind kind, Scenario scenario) {
        // given
        DataAttributeV3 definition = malformedDefinition(kind, scenario);
        String identity = "attribute with name '" + definition.getName() + "' and UUID '" + definition.getUuid() + "'";
        String expectedMessage = switch (scenario) {
            case WRONG_VERSION, WRONG_SCHEMA_VERSION, MISSING_SCHEMA_VERSION ->
                (kind == AlgorithmKind.SIGNATURE ? "Sign" : "Cipher") + " attributes must declare exactly one v3 "
                        + identity;
            default -> "The " + identity + " must offer at least one value, and only "
                    + (kind == AlgorithmKind.SIGNATURE ? "signature" : "encryption") + " algorithm codes";
        };

        // when
        OperationValidationResult result = validate(kind, definition);

        // then
        assertFalse(result.isValid());
        assertEquals(expectedMessage, result.getCause().getMessage());
    }

    static Stream<Arguments> malformedSchemas() {
        return Arrays
                .stream(AlgorithmKind.values())
                .flatMap(kind -> Arrays
                        .stream(Scenario.values())
                        .map(scenario -> arguments(named(kind.name(), kind), named(scenario.name(), scenario))));
    }

    private static DataAttributeV3 malformedDefinition(AlgorithmKind kind, Scenario scenario) {
        DataAttributeV3 definition = definition(kind);
        String validCode = (String) definition.getContent().get(0).getData();
        StringAttributeContentV3 validOption = new StringAttributeContentV3(validCode);
        String unknownCode = "unsupported-algorithm";
        StringAttributeContentV3 unknownOption = new StringAttributeContentV3(unknownCode);
        int nonStringValue = 1;
        switch (scenario) {
            case WRONG_VERSION -> definition.setVersion(AttributeVersion.V2.getVersion());
            case WRONG_SCHEMA_VERSION -> definition.setSchemaVersion(AttributeVersion.V2);
            case MISSING_SCHEMA_VERSION -> definition.setSchemaVersion(null);
            case WRONG_OPTION_CONTENT_TYPE ->
                definition.getContent().get(0).setContentType(AttributeContentType.INTEGER);
            case MISSING_OPTION_CONTENT_TYPE -> definition.getContent().get(0).setContentType(null);
            case VALID_THEN_UNKNOWN -> definition.setContent(List.of(validOption, unknownOption));
            case UNKNOWN_THEN_VALID -> definition.setContent(List.of(unknownOption, validOption));
            case VALID_THEN_NULL -> definition.setContent(Arrays.asList(validOption, null));
            case VALID_THEN_NON_STRING ->
                definition.setContent(List.of(validOption, new IntegerAttributeContentV3(nonStringValue)));
            case VALID_THEN_MISSING_DATA -> definition.setContent(List.of(validOption, new StringAttributeContentV3()));
        }
        return definition;
    }

    private static OperationValidationResult validate(AlgorithmKind kind, DataAttributeV3 definition) {
        return kind == AlgorithmKind.SIGNATURE
                ? VALIDATOR.validateSignAttributeList(List.of(definition))
                : VALIDATOR.validateCipherAttributeList(List.of(definition));
    }

    private static DataAttributeV3 definition(AlgorithmKind kind) {
        return kind == AlgorithmKind.SIGNATURE
                ? SignatureAlgorithmAttribute.definition(Arrays.asList(SignatureAlgorithm.values()))
                : EncryptionAlgorithmAttribute.definition(Arrays.asList(EncryptionAlgorithm.values()));
    }

    enum AlgorithmKind {
        SIGNATURE,
        CIPHER
    }

    enum Scenario {
        WRONG_VERSION,
        WRONG_SCHEMA_VERSION,
        MISSING_SCHEMA_VERSION,
        WRONG_OPTION_CONTENT_TYPE,
        MISSING_OPTION_CONTENT_TYPE,
        VALID_THEN_UNKNOWN,
        UNKNOWN_THEN_VALID,
        VALID_THEN_NULL,
        VALID_THEN_NON_STRING,
        VALID_THEN_MISSING_DATA
    }
}
