package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.connector.common.v2.OperationStatus;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPrivateKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPublicKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validSecretKeyDataResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class KeyOperationStatusResponseValidationTest {

    private static final String FAILURE_REASON = "Operation did not complete";

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @ParameterizedTest(name = "{0}")
    @MethodSource("secretKeyStatusMatrix")
    void validate_matchesStateContract_forSecretKeyCreation(CreationStatusCase statusCase) {
        // given
        SecretKeyOperationStatusResponseV2Dto response = new SecretKeyOperationStatusResponseV2Dto();
        response.setStatus(statusCase.status());
        response.setResult(statusCase.resultPresent() ? validSecretKeyDataResponse() : null);
        response.setReason(statusCase.reasonPresent() ? FAILURE_REASON : null);

        // when
        Set<ConstraintViolation<SecretKeyOperationStatusResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertValidationOutcome(statusCase.valid(), violations);
    }

    static Stream<Named<CreationStatusCase>> secretKeyStatusMatrix() {
        return creationStatusMatrix();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("keyPairStatusMatrix")
    void validate_matchesStateContract_forKeyPairCreation(CreationStatusCase statusCase) {
        // given
        KeyPairOperationStatusResponseV2Dto response = new KeyPairOperationStatusResponseV2Dto();
        response.setStatus(statusCase.status());
        response.setResult(statusCase.resultPresent() ? validKeyPairResult() : null);
        response.setReason(statusCase.reasonPresent() ? FAILURE_REASON : null);

        // when
        Set<ConstraintViolation<KeyPairOperationStatusResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertValidationOutcome(statusCase.valid(), violations);
    }

    static Stream<Named<CreationStatusCase>> keyPairStatusMatrix() {
        return creationStatusMatrix();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("destructionStatusMatrix")
    void validate_matchesStateContract_forKeyDestruction(DestructionStatusCase statusCase) {
        // given
        KeyDestructionStatusResponseV2Dto response = new KeyDestructionStatusResponseV2Dto();
        response.setStatus(statusCase.status());
        response.setReason(statusCase.reasonPresent() ? FAILURE_REASON : null);

        // when
        Set<ConstraintViolation<KeyDestructionStatusResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertValidationOutcome(statusCase.valid(), violations);
    }

    static Stream<Named<DestructionStatusCase>> destructionStatusMatrix() {
        return Stream.of(OperationStatus.values()).flatMap(status -> Stream.of(false, true).map(reasonPresent -> {
            boolean valid = isReasonConsistentWithStatus(status, reasonPresent);
            String name = status + ", reason " + presence(reasonPresent);
            return named(name, new DestructionStatusCase(status, reasonPresent, valid));
        }));
    }

    @Test
    void validate_requiresStatus_forOperationStatusResponse() {
        // given
        KeyDestructionStatusResponseV2Dto responseWithoutStatus = new KeyDestructionStatusResponseV2Dto();

        // when
        Set<ConstraintViolation<KeyDestructionStatusResponseV2Dto>> violations = VALIDATOR
                .validate(responseWithoutStatus);

        // then
        assertHasViolation(violations, "status", "status is required");
    }

    @Test
    void validate_rejectsBlankReason_forFailedStatus() {
        // given
        String blankReason = "   ";
        KeyDestructionStatusResponseV2Dto response = new KeyDestructionStatusResponseV2Dto();
        response.setStatus(OperationStatus.FAILED);
        response.setReason(blankReason);

        // when
        Set<ConstraintViolation<KeyDestructionStatusResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertHasViolation(violations, "reasonConsistentWithStatus",
                "reason is required when status is failed or cancelled and must be absent otherwise");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("incompleteCompletedStatuses")
    void validate_hasExpectedViolation_forIncompleteCompletedCreationResult(InvalidStatus invalidStatus) {
        // given
        KeyCreationStatusResponseV2Dto completedResponse = invalidStatus.response();

        // when
        Set<ConstraintViolation<KeyCreationStatusResponseV2Dto>> violations = VALIDATOR.validate(completedResponse);

        // then
        assertHasViolation(violations, invalidStatus.path(), invalidStatus.message());
    }

    static Stream<Named<InvalidStatus>> incompleteCompletedStatuses() {
        SecretKeyOperationStatusResponseV2Dto secretWithoutKeyMetadata = validCompletedSecretStatus();
        secretWithoutKeyMetadata.getResult().setKeyMeta(List.of());
        SecretKeyOperationStatusResponseV2Dto secretWithoutKeyData = validCompletedSecretStatus();
        secretWithoutKeyData.getResult().setKeyData(null);
        KeyPairOperationStatusResponseV2Dto pairWithoutPairMetadata = validCompletedKeyPairStatus();
        pairWithoutPairMetadata.getResult().setKeyPairMeta(List.of());
        KeyPairOperationStatusResponseV2Dto pairWithoutPublicData = validCompletedKeyPairStatus();
        pairWithoutPublicData.getResult().setPublicKeyData(null);
        KeyPairOperationStatusResponseV2Dto pairWithoutPrivateData = validCompletedKeyPairStatus();
        pairWithoutPrivateData.getResult().setPrivateKeyData(null);

        return Stream
                .of(named("secret missing key metadata",
                        new InvalidStatus(secretWithoutKeyMetadata, "result.keyMeta",
                                "keyMeta must contain at least one item for synchronous execution")),
                        named("secret missing key data",
                                new InvalidStatus(secretWithoutKeyData, "result.keyData",
                                        "keyData is required for synchronous execution")),
                        named("key pair missing pair metadata",
                                new InvalidStatus(pairWithoutPairMetadata, "result.keyPairMeta",
                                        "keyPairMeta must contain at least one item for synchronous execution")),
                        named("key pair missing public data",
                                new InvalidStatus(pairWithoutPublicData, "result.publicKeyData",
                                        "publicKeyData is required for synchronous execution")),
                        named("key pair missing private data", new InvalidStatus(pairWithoutPrivateData,
                                "result.privateKeyData", "privateKeyData is required for synchronous execution")));
    }

    @Test
    void validate_rejectsOperationMetadata_inCompletedResult() {
        // given
        SecretKeyOperationStatusResponseV2Dto response = validCompletedSecretStatus();
        response.getResult().setOperationMeta(validMetadata());

        // when
        Set<ConstraintViolation<SecretKeyOperationStatusResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertHasViolation(violations, "result.operationMeta",
                "operationMeta must be absent for synchronous execution");
    }

    @Test
    void validate_rejectsMismatchedAlgorithms_forCompletedKeyPairResult() {
        // given
        KeyPairOperationStatusResponseV2Dto response = validCompletedKeyPairStatus();
        response.getResult().getPrivateKeyData().getKeyData().setAlgorithm(KeyAlgorithm.ECDSA);

        // when
        Set<ConstraintViolation<KeyPairOperationStatusResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertHasViolation(violations, "result.keyAlgorithmsMatching", "public and private key algorithms must match");
    }

    @Test
    void validate_acceptsDistinctComponentLengths_forCompletedKeyPairResult() {
        // given
        int mismatchedPrivateKeyLength = 4096;
        KeyPairOperationStatusResponseV2Dto response = validCompletedKeyPairStatus();
        response.getResult().getPrivateKeyData().getKeyData().setLength(mismatchedPrivateKeyLength);

        // when
        Set<ConstraintViolation<KeyPairOperationStatusResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertTrue(violations.isEmpty(), violations::toString);
    }

    @Test
    void validate_reportsNestedPath_forInvalidCompletedSecretKeyData() {
        // given
        int invalidKeyLength = 0;
        SecretKeyOperationStatusResponseV2Dto response = validCompletedSecretStatus();
        response.getResult().getKeyData().setLength(invalidKeyLength);

        // when
        Set<ConstraintViolation<SecretKeyOperationStatusResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertHasViolation(violations, "result.keyData.length", "key length must be positive");
    }

    @Test
    void validate_reportsNestedPath_forInvalidCompletedPublicKeyData() {
        // given
        int invalidKeyLength = -1;
        KeyPairOperationStatusResponseV2Dto response = validCompletedKeyPairStatus();
        response.getResult().getPublicKeyData().getKeyData().setLength(invalidKeyLength);

        // when
        Set<ConstraintViolation<KeyPairOperationStatusResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertHasViolation(violations, "result.publicKeyData.keyData.length", "key length must be positive");
    }

    private static Stream<Named<CreationStatusCase>> creationStatusMatrix() {
        return Stream
                .of(OperationStatus.values())
                .flatMap(status -> Stream
                        .of(false, true)
                        .flatMap(resultPresent -> Stream.of(false, true).map(reasonPresent -> {
                            boolean valid = isCreationStateValid(status, resultPresent, reasonPresent);
                            String name = status + ", result " + presence(resultPresent) + ", reason "
                                    + presence(reasonPresent);
                            return named(name, new CreationStatusCase(status, resultPresent, reasonPresent, valid));
                        })));
    }

    private static boolean isCreationStateValid(OperationStatus status, boolean resultPresent, boolean reasonPresent) {
        boolean resultValid = status == OperationStatus.COMPLETED ? resultPresent : !resultPresent;
        return resultValid && isReasonConsistentWithStatus(status, reasonPresent);
    }

    private static boolean isReasonConsistentWithStatus(OperationStatus status, boolean reasonPresent) {
        return switch (status) {
            case FAILED, CANCELLED -> reasonPresent;
            case IN_PROGRESS, COMPLETED -> !reasonPresent;
        };
    }

    private static String presence(boolean present) {
        return present ? "present" : "absent";
    }

    private static SecretKeyOperationStatusResponseV2Dto validCompletedSecretStatus() {
        SecretKeyOperationStatusResponseV2Dto response = new SecretKeyOperationStatusResponseV2Dto();
        response.setStatus(OperationStatus.COMPLETED);
        response.setResult(validSecretKeyDataResponse());
        return response;
    }

    private static KeyPairOperationStatusResponseV2Dto validCompletedKeyPairStatus() {
        KeyPairOperationStatusResponseV2Dto response = new KeyPairOperationStatusResponseV2Dto();
        response.setStatus(OperationStatus.COMPLETED);
        response.setResult(validKeyPairResult());
        return response;
    }

    private static KeyPairDataResponseV2Dto validKeyPairResult() {
        KeyPairDataResponseV2Dto result = new KeyPairDataResponseV2Dto();
        result.setPublicKeyData(validPublicKeyDataResponse());
        result.setPrivateKeyData(validPrivateKeyDataResponse());
        result.setKeyPairMeta(validMetadata());
        return result;
    }

    private static void assertValidationOutcome(boolean expectedValid,
            Set<? extends ConstraintViolation<?>> violations) {
        assertEquals(expectedValid, violations.isEmpty(), () -> "Expected valid=" + expectedValid + ", got "
                + violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList());
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

    private record CreationStatusCase(OperationStatus status, boolean resultPresent, boolean reasonPresent,
            boolean valid) {
    }

    private record DestructionStatusCase(OperationStatus status, boolean reasonPresent, boolean valid) {
    }

    private record InvalidStatus(KeyCreationStatusResponseV2Dto response, String path, String message) {
    }
}
