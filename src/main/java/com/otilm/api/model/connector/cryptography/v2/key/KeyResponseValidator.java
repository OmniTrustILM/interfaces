package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;

/**
 * Explicit validation for connector responses; response Bean Validation is not invoked by the client transports.
 */
public final class KeyResponseValidator {

    private KeyResponseValidator() {
    }

    public static void validateCompleted(SecretKeyDataResponseV2Dto response) {
        if (response == null || response.getKeyData() == null) {
            throw new IllegalArgumentException("Completed secret-key response must contain key data");
        }
        requireKeyHandle(response.getKeyMeta(), "secret key");
        response.getKeyData().validate();
    }

    public static void validateCompleted(KeyPairDataResponseV2Dto response) {
        if (response == null || response.getPrivateKeyData() == null
                || response.getPrivateKeyData().getKeyData() == null
                || response.getPublicKeyData() == null
                || response.getPublicKeyData().getKeyData() == null) {
            throw new IllegalArgumentException("Completed key-pair response must contain both key descriptors");
        }
        requireKeyHandle(response.getPrivateKeyData().getKeyMeta(), "private key");
        requireKeyHandle(response.getPublicKeyData().getKeyMeta(), "public key");
        requireKeyHandle(response.getKeyPairMeta(), "key pair");
        response.getPrivateKeyData().getKeyData().validate();
        response.getPublicKeyData().getKeyData().validate();
    }

    private static void requireKeyHandle(java.util.List<com.otilm.api.model.common.attribute.v2.MetadataAttributeV2> metadata,
                                         String subject) {
        if (!OperationResponseValidator.isUsableMetadata(metadata)) {
            throw new IllegalArgumentException("Completed " + subject + " response must contain usable metadata");
        }
    }
}
