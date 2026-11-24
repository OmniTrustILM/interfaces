package com.otilm.api.model.connector.discovery.v2;

import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.connector.discovery.v2.validation.NoPrivateKeyMaterial;
import com.otilm.api.testsupport.ValidatorFixture;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Proves the normative rule stated in {@link DiscoveredKeyDto}'s javadoc — key material never traverses discovery — is
 * actually enforced by bean validation, not just documented.
 */
class DiscoveredKeyDtoValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private DiscoveredKeyDto validBase() {
        DiscoveredKeyDto dto = new DiscoveredKeyDto();
        dto.setType(KeyType.PUBLIC_KEY);
        dto.setAlgorithm(KeyAlgorithm.RSA);
        return dto;
    }

    @Test
    void privateKeyInfoFormatIsRejectedRegardlessOfType() {
        DiscoveredKeyDto dto = validBase();
        dto.setPublicKeyFormat(KeyFormat.PRKI);
        dto.setPublicKey("MIIBOgIBAAJBAK...");

        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(dto);
        assertFalse(violations.isEmpty(), "PRKI must never be an accepted publicKeyFormat");
    }

    @Test
    void encryptedPrivateKeyInfoFormatIsRejected() {
        DiscoveredKeyDto dto = validBase();
        dto.setPublicKeyFormat(KeyFormat.EPRKI);
        dto.setPublicKey("MIIBOgIBAAJBAK...");

        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(dto);
        assertFalse(violations.isEmpty(), "EPRKI must never be an accepted publicKeyFormat");
    }

    @Test
    void privateKeyTypeCarryingPublicKeyBytesIsRejected() {
        // The shape under test: a private-type item that still carries public-key material — here
        // a PrivateKeyInfo publicKeyFormat and public key bytes.
        DiscoveredKeyDto dto = new DiscoveredKeyDto();
        dto.setType(KeyType.PRIVATE_KEY);
        dto.setAlgorithm(KeyAlgorithm.RSA);
        dto.setPublicKeyFormat(KeyFormat.PRKI);
        dto.setPublicKey("MIIBOgIBAAJBAK...");

        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(dto);
        assertFalse(violations.isEmpty(), "a PRIVATE_KEY item must never carry key bytes");
    }

    @Test
    void secretKeyTypeCarryingPublicKeyFieldIsRejected() {
        DiscoveredKeyDto dto = new DiscoveredKeyDto();
        dto.setType(KeyType.SECRET_KEY);
        dto.setAlgorithm(KeyAlgorithm.RSA);
        // A secret key has no "public" part at all, so even a well-formed SPKI value here is forbidden.
        dto.setPublicKeyFormat(KeyFormat.SPKI);
        dto.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A");

        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(dto);
        assertFalse(violations.isEmpty(), "a SECRET_KEY item must never carry publicKey/publicKeyFormat");
    }

    @Test
    void splitKeyTypeCarryingPublicKeyIsRejected() {
        DiscoveredKeyDto dto = new DiscoveredKeyDto();
        dto.setType(KeyType.SPLIT_KEY);
        dto.setAlgorithm(KeyAlgorithm.RSA);
        dto.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A");
        // publicKeyFormat left unset: publicKey alone must still be enough to trip the rule.

        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(dto);
        assertFalse(violations.isEmpty(), "a SPLIT_KEY item must never carry publicKey material");
    }

    @Test
    void typeWithoutPublicPartCarryingOnlyPublicKeyIsRejectedOnPublicKeyPathOnly() {
        DiscoveredKeyDto dto = new DiscoveredKeyDto();
        dto.setType(KeyType.PRIVATE_KEY);
        dto.setAlgorithm(KeyAlgorithm.RSA);
        dto.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A");
        // publicKeyFormat intentionally left unset.

        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(dto);
        assertEquals(Set.of("publicKey"), propertyPaths(violations),
                "only publicKey is populated, so only publicKey should be named");
    }

    @Test
    void typeWithoutPublicPartCarryingOnlyPublicKeyFormatIsRejectedOnPublicKeyFormatPathOnly() {
        DiscoveredKeyDto dto = new DiscoveredKeyDto();
        dto.setType(KeyType.PRIVATE_KEY);
        dto.setAlgorithm(KeyAlgorithm.RSA);
        dto.setLength(2048);
        // A non-private format on its own, so only the "no public part for this type" rule trips
        // (not the separate PRKI/EPRKI-format rule) — isolates the property-path behavior under test.
        dto.setPublicKeyFormat(KeyFormat.SPKI);
        // publicKey intentionally left unset.

        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(dto);
        assertEquals(Set.of("publicKeyFormat"), propertyPaths(violations),
                "only publicKeyFormat is populated, so only publicKeyFormat should be named — "
                        + "not publicKey, which was never set");
    }

    @Test
    void typeWithoutPublicPartCarryingBothFieldsIsRejectedOnBothPaths() {
        DiscoveredKeyDto dto = new DiscoveredKeyDto();
        dto.setType(KeyType.PRIVATE_KEY);
        dto.setAlgorithm(KeyAlgorithm.RSA);
        dto.setPublicKeyFormat(KeyFormat.SPKI);
        dto.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A");

        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(dto);
        assertEquals(Set.of("publicKey", "publicKeyFormat"), propertyPaths(violations),
                "both fields are populated, so both should be named");
    }

    private static Set<String> propertyPaths(Set<ConstraintViolation<DiscoveredKeyDto>> violations) {
        return violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());
    }

    /**
     * A connector author reads only the generated document, so the published prose has to name every key type the
     * validator actually covers — no more and no fewer. Both descriptions draw their list from
     * {@link NoPrivateKeyMaterial#TYPES_WITHOUT_A_PUBLIC_PART_NAMES}, and this pins that single list against
     * {@link NoPrivateKeyMaterial#TYPES_WITHOUT_A_PUBLIC_PART}, the set the validator applies.
     */
    @Test
    void publishedProseNamesExactlyTheKeyTypesTheRuleCovers() throws NoSuchFieldException {
        String classProse = DiscoveredKeyDto.class.getAnnotation(Schema.class).description();
        String publicKeyProse = DiscoveredKeyDto.class
                .getDeclaredField("publicKey")
                .getAnnotation(Schema.class)
                .description();

        for (KeyType type : KeyType.values()) {
            boolean covered = NoPrivateKeyMaterial.TYPES_WITHOUT_A_PUBLIC_PART.contains(type);
            assertEquals(covered, classProse.contains(type.name()),
                    covered
                            ? "the class-level rule must name " + type.name() + ": " + classProse
                            : "the class-level rule must not name " + type.name() + ", which it does not " + "cover: "
                                    + classProse);
            assertEquals(covered, publicKeyProse.contains(type.name()),
                    covered
                            ? "publicKey's description must name " + type.name() + ": " + publicKeyProse
                            : "publicKey's description must not name " + type.name() + ", which the rule "
                                    + "does not cover: " + publicKeyProse);
        }
    }

    @Test
    void privateKeyExistenceOnlyReportIsValid() {
        DiscoveredKeyDto dto = new DiscoveredKeyDto();
        dto.setType(KeyType.PRIVATE_KEY);
        dto.setAlgorithm(KeyAlgorithm.RSA);
        dto.setLength(2048);
        dto.setFingerprint("9c1a2b3d4e5f60718293a4b5c6d7e8f90123456789abcdef0123456789abcde");
        // publicKey and publicKeyFormat intentionally left unset.

        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.isEmpty(), "an existence-only private-key report must pass validation cleanly");
    }

    @Test
    void legitimatePublicKeyReportIsValid() {
        DiscoveredKeyDto dto = validBase();
        dto.setPublicKeyFormat(KeyFormat.SPKI);
        dto.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A");

        Set<ConstraintViolation<DiscoveredKeyDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.isEmpty(), "a PUBLIC_KEY report with SPKI-formatted public key bytes must be valid");
    }
}
