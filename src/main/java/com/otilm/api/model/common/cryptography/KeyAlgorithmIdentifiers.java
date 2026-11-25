package com.otilm.api.model.common.cryptography;

import java.io.IOException;
import java.util.Arrays;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/**
 * Reading of the X.509 AlgorithmIdentifier naming a key's parameters, as SubjectPublicKeyInfo, PKCS#8 and the
 * private-key descriptor carry it.
 */
public final class KeyAlgorithmIdentifiers {

    static final String MALFORMED = "AlgorithmIdentifier must be canonical DER";

    private KeyAlgorithmIdentifiers() {
    }

    /**
     * Any OID and parameters are read. The parameters stay undecoded.
     *
     * @param encoded the DER encoding
     * @return the AlgorithmIdentifier
     * @throws IllegalArgumentException for an encoding other than canonical DER
     */
    public static AlgorithmIdentifier read(byte[] encoded) {
        try {
            AlgorithmIdentifier identifier = AlgorithmIdentifier.getInstance(ASN1Primitive.fromByteArray(encoded));
            if (identifier != null && Arrays.equals(encoded, identifier.getEncoded(ASN1Encoding.DER))) {
                return identifier;
            }
        } catch (IOException | RuntimeException e) {
            throw new IllegalArgumentException(MALFORMED, e);
        }
        throw new IllegalArgumentException(MALFORMED);
    }
}
