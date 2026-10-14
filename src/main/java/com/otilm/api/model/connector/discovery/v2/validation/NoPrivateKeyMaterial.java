package com.otilm.api.model.connector.discovery.v2.validation;

import com.otilm.api.model.common.enums.cryptography.KeyType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

/**
 * Keeps key material out of discovery: {@code publicKeyFormat} is never a private-key format ({@code PRKI},
 * {@code EPRKI}), and a key with no public part ({@code PRIVATE_KEY}, {@code SECRET_KEY}, {@code SPLIT_KEY}) carries
 * neither {@code publicKey} nor {@code publicKeyFormat}.
 */
@Constraint(validatedBy = NoPrivateKeyMaterialValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoPrivateKeyMaterial {

    /**
     * The key types with no reportable public part. An annotation value cannot be computed, so the published wording is
     * {@link #TYPES_WITHOUT_A_PUBLIC_PART_NAMES}; {@code DiscoveredKeyDtoValidationTest} pins that both name the same
     * types.
     */
    Set<KeyType> TYPES_WITHOUT_A_PUBLIC_PART = Set.of(KeyType.PRIVATE_KEY, KeyType.SECRET_KEY, KeyType.SPLIT_KEY);

    /** {@link #TYPES_WITHOUT_A_PUBLIC_PART} spelled for contract prose. Keep the two in step. */
    String TYPES_WITHOUT_A_PUBLIC_PART_NAMES = "PRIVATE_KEY, SECRET_KEY or SPLIT_KEY";

    String message() default "key material must never traverse discovery: publicKey/publicKeyFormat "
            + "must be absent for this key type, and publicKeyFormat must never be a private-key format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
