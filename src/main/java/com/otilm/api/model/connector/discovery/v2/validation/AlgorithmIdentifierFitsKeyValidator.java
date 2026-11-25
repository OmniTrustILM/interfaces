package com.otilm.api.model.connector.discovery.v2.validation;

import com.otilm.api.model.common.cryptography.KeyAlgorithmIdentifiers;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.connector.discovery.v2.DiscoveredKeyDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Base64;
import java.util.Optional;

public class AlgorithmIdentifierFitsKeyValidator
        implements
            ConstraintValidator<AlgorithmIdentifierFitsKey, DiscoveredKeyDto> {

    private static final String BESIDE_AN_SPKI = "algorithmIdentifier must be absent beside a SubjectPublicKeyInfo "
            + "publicKey";
    private static final String ON_A_SECRET_KEY = "algorithmIdentifier must be absent for a secret key";
    private static final String NOT_BASE64 = "algorithmIdentifier must be base64-encoded";
    private static final String INVALID = "algorithmIdentifier is invalid: ";

    @Override
    public boolean isValid(DiscoveredKeyDto key, ConstraintValidatorContext context) {
        if (key == null || key.getAlgorithmIdentifier() == null) {
            return true;
        }
        Optional<String> breach = breach(key);
        if (breach.isEmpty()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(breach.get())
                .addPropertyNode("algorithmIdentifier")
                .addConstraintViolation();
        return false;
    }

    private static Optional<String> breach(DiscoveredKeyDto key) {
        if (key.hasPublicKeyInSpkiFormat()) {
            return Optional.of(BESIDE_AN_SPKI);
        }
        if (key.getType() == KeyType.SECRET_KEY) {
            return Optional.of(ON_A_SECRET_KEY);
        }
        byte[] encoded;
        try {
            encoded = Base64.getDecoder().decode(key.getAlgorithmIdentifier());
        } catch (IllegalArgumentException e) {
            return Optional.of(NOT_BASE64);
        }
        try {
            KeyAlgorithmIdentifiers.read(encoded);
        } catch (IllegalArgumentException e) {
            return Optional.of(INVALID + e.getMessage());
        }
        return Optional.empty();
    }
}
