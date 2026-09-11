package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.connector.common.v2.validation.AsynchronousResponse;
import com.otilm.api.model.connector.common.v2.validation.SynchronousResponse;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadataAttribute;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPrivateKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPublicKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validSecretKeyData;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class KeyCreationResponseValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @Test
    void validate_hasNoViolations_forSynchronousOperationResponseWithoutOperationMetadata() {
        // given
        KeyOperationResponseV2Dto synchronousResponse = new KeyOperationResponseV2Dto();

        // when
        Set<ConstraintViolation<KeyOperationResponseV2Dto>> violations = VALIDATOR
                .validate(synchronousResponse, SynchronousResponse.class);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_requiresOperationMetadata_forAsynchronousOperationResponse() {
        // given
        KeyOperationResponseV2Dto asynchronousResponseWithoutMetadata = new KeyOperationResponseV2Dto();

        // when
        Set<ConstraintViolation<KeyOperationResponseV2Dto>> violations = VALIDATOR
                .validate(asynchronousResponseWithoutMetadata, AsynchronousResponse.class);

        // then
        assertHasViolation(violations, "operationMeta",
                "operationMeta must contain at least one item for asynchronous execution");
    }

    @Test
    void validate_rejectsOperationMetadata_forSynchronousOperationResponse() {
        // given
        KeyOperationResponseV2Dto synchronousResponseWithMetadata = new KeyOperationResponseV2Dto();
        synchronousResponseWithMetadata.setOperationMeta(validMetadata());

        // when
        Set<ConstraintViolation<KeyOperationResponseV2Dto>> violations = VALIDATOR
                .validate(synchronousResponseWithMetadata, SynchronousResponse.class);

        // then
        assertHasViolation(violations, "operationMeta", "operationMeta must be absent for synchronous execution");
    }

    @Test
    void validate_hasNoViolations_forAsynchronousOperationResponseWithValidMetadata() {
        // given
        KeyOperationResponseV2Dto asynchronousResponse = new KeyOperationResponseV2Dto();
        asynchronousResponse.setOperationMeta(validMetadata());

        // when
        Set<ConstraintViolation<KeyOperationResponseV2Dto>> violations = VALIDATOR
                .validate(asynchronousResponse, AsynchronousResponse.class);

        // then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_rejectsNullOperationMetadataItem_forAsynchronousOperationResponse() {
        // given
        KeyOperationResponseV2Dto response = new KeyOperationResponseV2Dto();
        response.setOperationMeta(Collections.singletonList(null));

        // when
        Set<ConstraintViolation<KeyOperationResponseV2Dto>> violations = VALIDATOR
                .validate(response, AsynchronousResponse.class);

        // then
        assertHasViolation(violations, "operationMeta[0].<list element>", "operationMeta must not contain null items");
    }

    @Test
    void validate_cascadesInvalidOperationMetadata_forAsynchronousOperationResponse() {
        // given
        MetadataAttributeV2 metadataWithoutName = validMetadataAttribute();
        metadataWithoutName.setName(null);
        KeyOperationResponseV2Dto response = new KeyOperationResponseV2Dto();
        response.setOperationMeta(List.of(metadataWithoutName));

        // when
        Set<ConstraintViolation<KeyOperationResponseV2Dto>> violations = VALIDATOR
                .validate(response, AsynchronousResponse.class);

        // then
        assertHasViolation(violations, "operationMeta[0].<list element>.name", "name must not be blank");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validCreationResponses")
    void validate_hasNoViolations_forValidCreationResponse(GroupedResponse groupedResponse) {
        // given
        Object validResponse = groupedResponse.response();

        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(validResponse, groupedResponse.group());

        // then
        assertTrue(violations.isEmpty());
    }

    static Stream<Named<GroupedResponse>> validCreationResponses() {
        return Stream
                .of(named("synchronous secret",
                        new GroupedResponse(validSynchronousSecretResponse(), SynchronousResponse.class)),
                        named("asynchronous secret",
                                new GroupedResponse(validAsynchronousSecretResponse(), AsynchronousResponse.class)),
                        named("synchronous key pair",
                                new GroupedResponse(validSynchronousKeyPairResponse(), SynchronousResponse.class)),
                        named("asynchronous key pair",
                                new GroupedResponse(validAsynchronousKeyPairResponse(), AsynchronousResponse.class)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidExecutionModeResponses")
    void validate_hasExpectedViolation_forResponseThatContradictsExecutionMode(InvalidGroupedResponse invalid) {
        // given
        Object response = invalid.response();

        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(response, invalid.group());

        // then
        assertHasViolation(violations, invalid.path(), invalid.message());
    }

    static Stream<Named<InvalidGroupedResponse>> invalidExecutionModeResponses() {
        SecretKeyDataResponseV2Dto synchronousSecretWithoutKeyData = validSynchronousSecretResponse();
        synchronousSecretWithoutKeyData.setKeyData(null);
        SecretKeyDataResponseV2Dto synchronousSecretWithoutKeyMetadata = validSynchronousSecretResponse();
        synchronousSecretWithoutKeyMetadata.setKeyMeta(List.of());
        SecretKeyDataResponseV2Dto asynchronousSecretWithKeyData = validAsynchronousSecretResponse();
        asynchronousSecretWithKeyData.setKeyData(validSecretKeyData());
        SecretKeyDataResponseV2Dto asynchronousSecretWithKeyMetadata = validAsynchronousSecretResponse();
        asynchronousSecretWithKeyMetadata.setKeyMeta(validMetadata());
        SecretKeyDataResponseV2Dto synchronousSecretWithOperationMetadata = validSynchronousSecretResponse();
        synchronousSecretWithOperationMetadata.setOperationMeta(validMetadata());
        SecretKeyDataResponseV2Dto synchronousSecretWithNullMetadataElement = validSynchronousSecretResponse();
        synchronousSecretWithNullMetadataElement.setKeyMeta(Collections.singletonList(null));
        SecretKeyDataResponseV2Dto synchronousSecretWithInvalidMetadata = validSynchronousSecretResponse();
        MetadataAttributeV2 secretMetadataWithoutName = validMetadataAttribute();
        secretMetadataWithoutName.setName(null);
        synchronousSecretWithInvalidMetadata.setKeyMeta(List.of(secretMetadataWithoutName));
        KeyPairDataResponseV2Dto synchronousPairWithoutPublicKey = validSynchronousKeyPairResponse();
        synchronousPairWithoutPublicKey.setPublicKeyData(null);
        KeyPairDataResponseV2Dto synchronousPairWithoutPrivateKey = validSynchronousKeyPairResponse();
        synchronousPairWithoutPrivateKey.setPrivateKeyData(null);
        KeyPairDataResponseV2Dto synchronousPairWithoutPairMetadata = validSynchronousKeyPairResponse();
        synchronousPairWithoutPairMetadata.setKeyPairMeta(List.of());
        KeyPairDataResponseV2Dto synchronousPairWithOperationMetadata = validSynchronousKeyPairResponse();
        synchronousPairWithOperationMetadata.setOperationMeta(validMetadata());
        KeyPairDataResponseV2Dto synchronousPairWithNullMetadataElement = validSynchronousKeyPairResponse();
        synchronousPairWithNullMetadataElement.setKeyPairMeta(Collections.singletonList(null));
        KeyPairDataResponseV2Dto synchronousPairWithInvalidPairMetadata = validSynchronousKeyPairResponse();
        MetadataAttributeV2 pairMetadataWithoutName = validMetadataAttribute();
        pairMetadataWithoutName.setName(null);
        synchronousPairWithInvalidPairMetadata.setKeyPairMeta(List.of(pairMetadataWithoutName));
        KeyPairDataResponseV2Dto asynchronousPairWithPublicKey = validAsynchronousKeyPairResponse();
        asynchronousPairWithPublicKey.setPublicKeyData(validPublicKeyDataResponse());
        KeyPairDataResponseV2Dto asynchronousPairWithPrivateKey = validAsynchronousKeyPairResponse();
        asynchronousPairWithPrivateKey.setPrivateKeyData(validPrivateKeyDataResponse());
        KeyPairDataResponseV2Dto asynchronousPairWithPairMetadata = validAsynchronousKeyPairResponse();
        asynchronousPairWithPairMetadata.setKeyPairMeta(validMetadata());
        KeyPairDataResponseV2Dto synchronousPairWithInvalidPublicMetadata = validSynchronousKeyPairResponse();
        MetadataAttributeV2 publicMetadataWithoutName = validMetadataAttribute();
        publicMetadataWithoutName.setName(null);
        synchronousPairWithInvalidPublicMetadata.getPublicKeyData().setKeyMeta(List.of(publicMetadataWithoutName));
        KeyPairDataResponseV2Dto synchronousPairWithInvalidPrivateDescriptor = validSynchronousKeyPairResponse();
        synchronousPairWithInvalidPrivateDescriptor.getPrivateKeyData().getKeyData().setLength(0);

        return Stream
                .of(named("sync secret missing key data",
                        new InvalidGroupedResponse(synchronousSecretWithoutKeyData, SynchronousResponse.class,
                                "keyData", "keyData is required for synchronous execution")),
                        named("sync secret missing key metadata",
                                new InvalidGroupedResponse(synchronousSecretWithoutKeyMetadata,
                                        SynchronousResponse.class, "keyMeta",
                                        "keyMeta must contain at least one item for synchronous execution")),
                        named("async secret contains key data",
                                new InvalidGroupedResponse(asynchronousSecretWithKeyData, AsynchronousResponse.class,
                                        "keyData", "keyData must be absent for asynchronous execution")),
                        named("async secret contains key metadata",
                                new InvalidGroupedResponse(asynchronousSecretWithKeyMetadata,
                                        AsynchronousResponse.class, "keyMeta",
                                        "keyMeta must be absent for asynchronous execution")),
                        named("sync secret contains operation metadata",
                                new InvalidGroupedResponse(synchronousSecretWithOperationMetadata,
                                        SynchronousResponse.class, "operationMeta",
                                        "operationMeta must be absent for synchronous execution")),
                        named("sync secret contains null metadata element",
                                new InvalidGroupedResponse(synchronousSecretWithNullMetadataElement,
                                        SynchronousResponse.class, "keyMeta[0].<list element>",
                                        "keyMeta must not contain null items")),
                        named("sync secret contains invalid metadata",
                                new InvalidGroupedResponse(synchronousSecretWithInvalidMetadata,
                                        SynchronousResponse.class, "keyMeta[0].<list element>.name",
                                        "name must not be blank")),
                        named("sync key pair missing public key",
                                new InvalidGroupedResponse(synchronousPairWithoutPublicKey, SynchronousResponse.class,
                                        "publicKeyData", "publicKeyData is required for synchronous execution")),
                        named("sync key pair missing private key",
                                new InvalidGroupedResponse(synchronousPairWithoutPrivateKey, SynchronousResponse.class,
                                        "privateKeyData", "privateKeyData is required for synchronous execution")),
                        named("sync key pair missing pair metadata",
                                new InvalidGroupedResponse(synchronousPairWithoutPairMetadata,
                                        SynchronousResponse.class, "keyPairMeta",
                                        "keyPairMeta must contain at least one item for synchronous execution")),
                        named("sync key pair contains operation metadata",
                                new InvalidGroupedResponse(synchronousPairWithOperationMetadata,
                                        SynchronousResponse.class, "operationMeta",
                                        "operationMeta must be absent for synchronous execution")),
                        named("sync key pair contains null metadata element",
                                new InvalidGroupedResponse(synchronousPairWithNullMetadataElement,
                                        SynchronousResponse.class, "keyPairMeta[0].<list element>",
                                        "keyPairMeta must not contain null items")),
                        named("sync key pair contains invalid pair metadata",
                                new InvalidGroupedResponse(synchronousPairWithInvalidPairMetadata,
                                        SynchronousResponse.class, "keyPairMeta[0].<list element>.name",
                                        "name must not be blank")),
                        named("async key pair contains public key",
                                new InvalidGroupedResponse(asynchronousPairWithPublicKey, AsynchronousResponse.class,
                                        "publicKeyData", "publicKeyData must be absent for asynchronous execution")),
                        named("async key pair contains private key",
                                new InvalidGroupedResponse(asynchronousPairWithPrivateKey, AsynchronousResponse.class,
                                        "privateKeyData", "privateKeyData must be absent for asynchronous execution")),
                        named("async key pair contains pair metadata",
                                new InvalidGroupedResponse(asynchronousPairWithPairMetadata, AsynchronousResponse.class,
                                        "keyPairMeta", "keyPairMeta must be absent for asynchronous execution")),
                        named("sync key pair invalid public metadata",
                                new InvalidGroupedResponse(synchronousPairWithInvalidPublicMetadata,
                                        SynchronousResponse.class, "publicKeyData.keyMeta[0].<list element>.name",
                                        "name must not be blank")),
                        named("sync key pair invalid private descriptor",
                                new InvalidGroupedResponse(synchronousPairWithInvalidPrivateDescriptor,
                                        SynchronousResponse.class, "privateKeyData.keyData.length",
                                        "key length must be positive")));
    }

    @Test
    void validate_rejectsMismatchedAlgorithms_forKeyPairResponse() {
        // given
        KeyPairDataResponseV2Dto response = validSynchronousKeyPairResponse();
        response.getPrivateKeyData().getKeyData().setAlgorithm(KeyAlgorithm.ECDSA);

        // when
        Set<ConstraintViolation<KeyPairDataResponseV2Dto>> violations = VALIDATOR
                .validate(response, SynchronousResponse.class);

        // then
        assertHasViolation(violations, "keyAlgorithmsMatching", "public and private key algorithms must match");
    }

    @Test
    void validate_rejectsMismatchedLengths_forKeyPairResponse() {
        // given
        int mismatchedPrivateKeyLength = 4096;
        KeyPairDataResponseV2Dto response = validSynchronousKeyPairResponse();
        response.getPrivateKeyData().getKeyData().setLength(mismatchedPrivateKeyLength);

        // when
        Set<ConstraintViolation<KeyPairDataResponseV2Dto>> violations = VALIDATOR
                .validate(response, SynchronousResponse.class);

        // then
        assertHasViolation(violations, "keyLengthsMatching", "public and private key lengths must match");
    }

    @Test
    void validate_reportsNestedPath_forInvalidSynchronousKeyData() {
        // given
        int invalidKeyLength = 0;
        SecretKeyDataResponseV2Dto response = validSynchronousSecretResponse();
        response.getKeyData().setLength(invalidKeyLength);

        // when
        Set<ConstraintViolation<SecretKeyDataResponseV2Dto>> violations = VALIDATOR
                .validate(response, SynchronousResponse.class);

        // then
        assertHasViolation(violations, "keyData.length", "key length must be positive");
    }

    private static SecretKeyDataResponseV2Dto validSynchronousSecretResponse() {
        SecretKeyDataResponseV2Dto response = new SecretKeyDataResponseV2Dto();
        response.setKeyData(validSecretKeyData());
        response.setKeyMeta(validMetadata());
        return response;
    }

    private static SecretKeyDataResponseV2Dto validAsynchronousSecretResponse() {
        SecretKeyDataResponseV2Dto response = new SecretKeyDataResponseV2Dto();
        response.setOperationMeta(validMetadata());
        return response;
    }

    private static KeyPairDataResponseV2Dto validSynchronousKeyPairResponse() {
        KeyPairDataResponseV2Dto response = new KeyPairDataResponseV2Dto();
        response.setPublicKeyData(validPublicKeyDataResponse());
        response.setPrivateKeyData(validPrivateKeyDataResponse());
        response.setKeyPairMeta(validMetadata());
        return response;
    }

    private static KeyPairDataResponseV2Dto validAsynchronousKeyPairResponse() {
        KeyPairDataResponseV2Dto response = new KeyPairDataResponseV2Dto();
        response.setOperationMeta(validMetadata());
        return response;
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

    private record GroupedResponse(Object response, Class<?> group) {
    }

    private record InvalidGroupedResponse(Object response, Class<?> group, String path, String message) {
    }
}
