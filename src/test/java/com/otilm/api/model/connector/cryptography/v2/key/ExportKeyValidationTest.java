package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.material.EncryptedKeyMaterialV2Dto;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.EXPORT_PASSPHRASE;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validExportKeyRequest;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validExportKeyResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPrivateKeyData;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPublicKeyData;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validSecretKeyData;
import static com.otilm.api.testsupport.ConstraintViolationAssertions.assertHasViolation;
import static com.otilm.api.testsupport.ConstraintViolationAssertions.assertNoViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ExportKeyValidationTest {

    private static final String NAMED_ALGORITHMS_MESSAGE = "algorithms must name the algorithms the connector accepts and must not contain Unknown";

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private static final Validator VALIDATOR = VALIDATORS.validator();

    @Test
    void exportRequest_hasNoViolations_whenFullyPopulated() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();

        // when
        Set<ConstraintViolation<ExportKeyRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertNoViolations(violations);
    }

    @Test
    void exportRequest_hasNoViolations_whenTheKeyCarriesNoReference() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        request.setKeyReference(null);

        // when
        Set<ConstraintViolation<ExportKeyRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertNoViolations(violations);
    }

    @Test
    void exportRequest_requiresKeyMeta() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        request.setKeyMeta(List.of());

        // when
        Set<ConstraintViolation<ExportKeyRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "keyMeta", "keyMeta is required and must not be empty");
    }

    @Test
    void exportRequest_requiresKeyRequestType() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        request.setKeyRequestType(null);

        // when
        Set<ConstraintViolation<ExportKeyRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "keyRequestType", "keyRequestType is required");
    }

    @Test
    void exportRequest_rejectsKeyReferenceThatIsNotAUuid() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        request.setKeyReference("not-a-uuid");

        // when
        Set<ConstraintViolation<ExportKeyRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "keyReference", "keyReference must be a canonical UUID");
    }

    @Test
    void exportRequest_requiresExportKeyAttributes() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        request.setExportKeyAttributes(null);

        // when
        Set<ConstraintViolation<ExportKeyRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "exportKeyAttributes",
                "exportKeyAttributes is required (may be empty list, but must be present)");
    }

    @Test
    void exportRequest_requiresPassphrase() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        request.setPassphrase(" ");

        // when
        Set<ConstraintViolation<ExportKeyRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "passphrase", "passphrase is required");
    }

    @Test
    void exportRequest_toStringRedactsPassphrase() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        request.setPassphrase(EXPORT_PASSPHRASE);

        // when
        String rendered = request.toString();

        // then
        assertFalse(rendered.contains(EXPORT_PASSPHRASE), () -> "passphrase leaked into " + rendered);
    }

    @Test
    void exportResponse_hasNoViolations_whenFullyPopulated() {
        // given
        ExportKeyResponseV2Dto response = validExportKeyResponse();

        // when
        Set<ConstraintViolation<ExportKeyResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertNoViolations(violations);
    }

    @Test
    void exportResponse_requiresMaterial() {
        // given
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        response.setMaterial(null);

        // when
        Set<ConstraintViolation<ExportKeyResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertHasViolation(violations, "material", "material is required");
    }

    @Test
    void exportResponse_rejectsMaterialOutsideThePinnedProtectionProfile() {
        // given
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        response.setMaterial(new EncryptedKeyMaterialV2Dto());

        // when
        Set<ConstraintViolation<ExportKeyResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertHasViolation(violations, "material.encryptedPrivateKeyInfo", "encryptedPrivateKeyInfo is required");
    }

    @Test
    void exportResponse_rejectsKeyReferenceThatIsNotAUuid() {
        // given
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        response.setKeyReference("not-a-uuid");

        // when
        Set<ConstraintViolation<ExportKeyResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertHasViolation(violations, "keyReference", "keyReference must be a canonical UUID");
    }

    @Test
    void exportResponse_acceptsProviderReportedPublicLength() {
        // given
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        PublicKeyDataV2Dto publicKey = validPublicKeyData();
        publicKey.setLength(4096);
        response.setKeyData(publicKey);

        // when
        Set<ConstraintViolation<ExportKeyResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertNoViolations(violations);
    }

    @Test
    void exportResponse_requiresTheKeyDescriptor() {
        // given
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        response.setKeyData(null);

        // when
        Set<ConstraintViolation<ExportKeyResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertHasViolation(violations, "keyData", "keyData is required");
    }

    @Test
    void exportResponse_acceptsASecretKeyDescriptor() {
        // given
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        response.setKeyData(validSecretKeyData());

        // when
        Set<ConstraintViolation<ExportKeyResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertNoViolations(violations);
    }

    @Test
    void exportResponse_rejectsAPrivateKeyDescriptor() {
        // given
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        response.setKeyData(validPrivateKeyData());

        // when
        Set<ConstraintViolation<ExportKeyResponseV2Dto>> violations = VALIDATOR.validate(response);

        // then
        assertHasViolation(violations, "checkableKeyDescriptor",
                "keyData must describe either a public key or a secret key");
    }

    /**
     * A restated discriminator must not be able to make a secret-key descriptor answer for a key pair: what the
     * document says last cannot change the object that was built from what it said first.
     */
    @Test
    void exportResponse_readsTheDescriptorTypeFromTheObjectItBuilt() throws Exception {
        // given
        String descriptorWithRestatedType = """
                {"type":"Secret","algorithm":"RSA","length":2048,"type":"Public"}
                """;

        // when
        KeyDataV2Dto keyData = new ObjectMapper().readValue(descriptorWithRestatedType, KeyDataV2Dto.class);

        // then
        assertInstanceOf(SecretKeyDataV2Dto.class, keyData);
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        response.setKeyData(keyData);
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        assertFalse(new OperationResponseValidator(VALIDATOR)
                .keyTransfer()
                .validateExportKey(request, ResponseEntity.ok(response))
                .isValid(), "a secret-key descriptor must not stand for an exported key pair");
    }

    @Test
    void exportResponse_toStringRedactsTheProtectedMaterialAndThePublicKey() {
        // given
        ExportKeyResponseV2Dto response = validExportKeyResponse();

        // when
        String rendered = response.toString();

        // then
        assertFalse(rendered.contains("encryptedPrivateKeyInfo="), () -> "envelope leaked into " + rendered);
        assertFalse(rendered.contains("publicKeySpki="), () -> "public key bytes leaked into " + rendered);
    }

    @Test
    void exportableKeyType_hasNoViolations_whenFullyPopulated() {
        // given
        ExportableKeyTypeV2Dto exportableKeyType = validExportableKeyType();

        // when
        Set<ConstraintViolation<ExportableKeyTypeV2Dto>> violations = VALIDATOR.validate(exportableKeyType);

        // then
        assertNoViolations(violations);
    }

    @Test
    void exportableKeyType_requiresKeyRequestType() {
        // given
        ExportableKeyTypeV2Dto exportableKeyType = validExportableKeyType();
        exportableKeyType.setKeyRequestType(null);

        // when
        Set<ConstraintViolation<ExportableKeyTypeV2Dto>> violations = VALIDATOR.validate(exportableKeyType);

        // then
        assertHasViolation(violations, "keyRequestType", "keyRequestType is required");
    }

    @Test
    void exportableKeyType_requiresAtLeastOneAlgorithm() {
        // given
        ExportableKeyTypeV2Dto exportableKeyType = validExportableKeyType();
        exportableKeyType.setAlgorithms(Set.of());

        // when
        Set<ConstraintViolation<ExportableKeyTypeV2Dto>> violations = VALIDATOR.validate(exportableKeyType);

        // then
        assertHasViolation(violations, "algorithms", "algorithms must contain at least one algorithm");
    }

    @Test
    void exportableKeyType_rejectsAnAlgorithmTheConnectorCouldNotName() {
        // given
        ExportableKeyTypeV2Dto exportableKeyType = validExportableKeyType();
        exportableKeyType.setAlgorithms(Set.of(KeyAlgorithm.RSA, KeyAlgorithm.UNKNOWN));

        // when
        Set<ConstraintViolation<ExportableKeyTypeV2Dto>> violations = VALIDATOR.validate(exportableKeyType);

        // then
        assertHasViolation(violations, "namedAlgorithms", NAMED_ALGORITHMS_MESSAGE);
    }

    @Test
    void exportableKeyType_rejectsAnAlgorithmThatCannotProduceTheDeclaredKeyType() {
        // given
        ExportableKeyTypeV2Dto exportableKeyType = validExportableKeyType();
        exportableKeyType.setKeyRequestType(KeyRequestType.SECRET);

        // when
        Set<ConstraintViolation<ExportableKeyTypeV2Dto>> violations = VALIDATOR.validate(exportableKeyType);

        // then
        assertHasViolation(violations, "algorithmsMatchingKeyType",
                "algorithms must be able to produce the declared keyRequestType");
    }

    /**
     * Every algorithm the platform names produces a key pair, so no secret key type can be declared until a secret-key
     * algorithm exists. The rule is written against that classification rather than against today's list.
     */
    @Test
    void noSecretKeyTypeCanBeDeclaredWhileEveryAlgorithmProducesAKeyPair() {
        // given
        // when
        long secretAlgorithms = Arrays
                .stream(KeyAlgorithm.values())
                .filter(algorithm -> algorithm != KeyAlgorithm.UNKNOWN)
                .filter(algorithm -> !algorithm.isKeyPairAlgorithm())
                .count();

        // then
        assertEquals(0, secretAlgorithms,
                "when a secret-key algorithm is added, a secret key type becomes declarable with no rule change");
    }

    @Test
    void exportContract_offersNoExecutionMode() {
        // given
        // when
        boolean declaresExecutionMode = Arrays
                .stream(ExportKeyRequestV2Dto.class.getMethods())
                .anyMatch(method -> "getExecutionMode".equals(method.getName()));

        // then
        assertFalse(declaresExecutionMode, "export is synchronous only and must not offer an execution mode");
    }

    @Test
    void exportContract_cannotRepresentCertificates() {
        // given
        Class<?>[] exportDtos = {ExportKeyRequestV2Dto.class, ExportKeyResponseV2Dto.class};

        // when
        boolean mentionsCertificates = Arrays
                .stream(exportDtos)
                .flatMap(dto -> Arrays.stream(dto.getDeclaredFields()))
                .anyMatch(field -> field.getName().toLowerCase(Locale.ROOT).contains("certificate"));

        // then
        assertFalse(mentionsCertificates, "certificates must not be representable in the export contract");
    }

    private static ExportableKeyTypeV2Dto validExportableKeyType() {
        ExportableKeyTypeV2Dto exportableKeyType = new ExportableKeyTypeV2Dto();
        exportableKeyType.setKeyRequestType(KeyRequestType.KEY_PAIR);
        exportableKeyType.setAlgorithms(Set.of(KeyAlgorithm.RSA, KeyAlgorithm.ECDSA));
        return exportableKeyType;
    }
}
