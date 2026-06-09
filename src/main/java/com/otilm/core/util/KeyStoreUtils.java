package com.otilm.core.util;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;

public class KeyStoreUtils {

    public static KeyStore bytes2KeyStore(byte[] keyStoreBytes, String password, String keyStoreType) {
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(new ByteArrayInputStream(keyStoreBytes), password.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new IllegalStateException("Error during converting Key Store.", e);
        }
    }
}
