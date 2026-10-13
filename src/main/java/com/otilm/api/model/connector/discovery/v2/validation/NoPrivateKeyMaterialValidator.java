package com.otilm.api.model.connector.discovery.v2.validation;

import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.connector.discovery.v2.DiscoveredKeyDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class NoPrivateKeyMaterialValidator implements ConstraintValidator<NoPrivateKeyMaterial, DiscoveredKeyDto> {

    private static final Set<KeyFormat> PRIVATE_KEY_FORMATS = Set.of(KeyFormat.PRKI, KeyFormat.EPRKI);

    // Declared on the annotation beside its published wording, so the two cannot drift apart.
    private static final Set<KeyType> TYPES_WITHOUT_A_PUBLIC_PART = NoPrivateKeyMaterial.TYPES_WITHOUT_A_PUBLIC_PART;

    @Override
    public boolean isValid(DiscoveredKeyDto value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean valid = true;

        if (value.getPublicKeyFormat() != null && PRIVATE_KEY_FORMATS.contains(value.getPublicKeyFormat())) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(
                            "publicKeyFormat must never be a private-key format (PRKI/EPRKI); "
                                    + "key material never traverses discovery")
                    .addPropertyNode("publicKeyFormat")
                    .addConstraintViolation();
            valid = false;
        }

        if (value.getType() != null && TYPES_WITHOUT_A_PUBLIC_PART.contains(value.getType())) {
            // One violation per offending field, so the caller is pointed at the field at fault.
            if (value.getPublicKey() != null) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                "publicKey must be absent when type is Private, Secret, or Split")
                        .addPropertyNode("publicKey")
                        .addConstraintViolation();
                valid = false;
            }

            if (value.getPublicKeyFormat() != null) {
                context.disableDefaultConstraintViolation();
                context
                        .buildConstraintViolationWithTemplate(
                                "publicKeyFormat must be absent when type is Private, Secret, or Split")
                        .addPropertyNode("publicKeyFormat")
                        .addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }
}
