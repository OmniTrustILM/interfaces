package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadataAttribute;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPrivateKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPublicKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validSecretKeyDataResponse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class NestedKeyDataResponseValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @ParameterizedTest(name = "{0}")
    @MethodSource("validResponses")
    void validate_hasNoViolations_forValidNestedKeyDataResponse(Object response) {
        // given
        Object validResponse = response;

        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(validResponse);

        // then
        assertTrue(violations.isEmpty());
    }

    static Stream<Named<Object>> validResponses() {
        return Stream
                .of(named("secret key", validSecretKeyDataResponse()),
                        named("private key", validPrivateKeyDataResponse()),
                        named("public key", validPublicKeyDataResponse()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidResponses")
    void validate_hasExpectedViolation_forInvalidNestedKeyDataResponse(InvalidResponse invalidResponse) {
        // given
        Object response = invalidResponse.response();

        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(response);

        // then
        assertHasViolation(violations, invalidResponse.path(), invalidResponse.message());
    }

    static Stream<Named<InvalidResponse>> invalidResponses() {
        PrivateKeyDataResponseV2Dto missingPrivateData = validPrivateKeyDataResponse();
        missingPrivateData.setKeyData(null);
        PrivateKeyDataResponseV2Dto invalidPrivateDescriptor = validPrivateKeyDataResponse();
        invalidPrivateDescriptor.getKeyData().setLength(0);
        PublicKeyDataResponseV2Dto missingPublicData = validPublicKeyDataResponse();
        missingPublicData.setKeyData(null);
        PublicKeyDataResponseV2Dto nullPublicMetadataElement = validPublicKeyDataResponse();
        nullPublicMetadataElement.setKeyMeta(Collections.singletonList(null));
        PublicKeyDataResponseV2Dto invalidPublicDescriptor = validPublicKeyDataResponse();
        invalidPublicDescriptor.getKeyData().setAlgorithm(null);
        PrivateKeyDataResponseV2Dto missingPrivateMetadata = validPrivateKeyDataResponse();
        missingPrivateMetadata.setKeyMeta(null);
        PrivateKeyDataResponseV2Dto emptyPrivateMetadata = validPrivateKeyDataResponse();
        emptyPrivateMetadata.setKeyMeta(List.of());
        PrivateKeyDataResponseV2Dto nullPrivateMetadataElement = validPrivateKeyDataResponse();
        nullPrivateMetadataElement.setKeyMeta(Collections.singletonList(null));
        PrivateKeyDataResponseV2Dto invalidPrivateMetadata = validPrivateKeyDataResponse();
        MetadataAttributeV2 privateMetadataWithoutName = validMetadataAttribute();
        privateMetadataWithoutName.setName(null);
        invalidPrivateMetadata.setKeyMeta(List.of(privateMetadataWithoutName));
        PublicKeyDataResponseV2Dto missingPublicMetadata = validPublicKeyDataResponse();
        missingPublicMetadata.setKeyMeta(null);
        PublicKeyDataResponseV2Dto emptyPublicMetadata = validPublicKeyDataResponse();
        emptyPublicMetadata.setKeyMeta(List.of());
        PublicKeyDataResponseV2Dto invalidPublicMetadata = validPublicKeyDataResponse();
        MetadataAttributeV2 publicMetadataWithoutName = validMetadataAttribute();
        publicMetadataWithoutName.setName(null);
        invalidPublicMetadata.setKeyMeta(List.of(publicMetadataWithoutName));

        return Stream
                .of(named("missing private key data",
                        new InvalidResponse(missingPrivateData, "keyData", "private key data is required")),
                        named("invalid private key descriptor",
                                new InvalidResponse(invalidPrivateDescriptor, "keyData.length",
                                        "key length must be positive")),
                        named("missing public key data",
                                new InvalidResponse(missingPublicData, "keyData", "public key data is required")),
                        named("null public key metadata element",
                                new InvalidResponse(nullPublicMetadataElement, "keyMeta[0].<list element>",
                                        "must not be null")),
                        named("invalid nested public descriptor",
                                new InvalidResponse(invalidPublicDescriptor, "keyData.algorithm",
                                        "key algorithm is required")),
                        named("missing private metadata",
                                new InvalidResponse(missingPrivateMetadata, "keyMeta",
                                        "private key metadata is required and must not be empty")),
                        named("empty private metadata",
                                new InvalidResponse(emptyPrivateMetadata, "keyMeta",
                                        "private key metadata is required and must not be empty")),
                        named("null private metadata item",
                                new InvalidResponse(nullPrivateMetadataElement, "keyMeta[0].<list element>",
                                        "must not be null")),
                        named("invalid private metadata",
                                new InvalidResponse(invalidPrivateMetadata, "keyMeta[0].<list element>.name",
                                        "name must not be blank")),
                        named("missing public metadata",
                                new InvalidResponse(missingPublicMetadata, "keyMeta",
                                        "public key metadata is required and must not be empty")),
                        named("empty public metadata",
                                new InvalidResponse(emptyPublicMetadata, "keyMeta",
                                        "public key metadata is required and must not be empty")),
                        named("invalid public metadata", new InvalidResponse(invalidPublicMetadata,
                                "keyMeta[0].<list element>.name", "name must not be blank")));
    }

    private static void assertHasViolation(Set<? extends ConstraintViolation<?>> violations, String path,
            String message) {
        assertTrue(
                violations
                        .stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals(path)
                                && violation.getMessage().equals(message)),
                () -> "Expected " + path + ": " + message + ", got "
                        + violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList());
    }

    private record InvalidResponse(Object response, String path, String message) {
    }
}
