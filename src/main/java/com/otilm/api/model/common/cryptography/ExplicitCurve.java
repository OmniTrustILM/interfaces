package com.otilm.api.model.common.cryptography;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9FieldID;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

/**
 * Size bound on an EC key's explicit parameters, checked on their encoding before any curve is built. BouncyCastle
 * validates a curve at a cost that grows with its order.
 */
public final class ExplicitCurve {

    /** Field size of the largest named curve, sect571. */
    public static final int MAXIMUM_FIELD_BITS = 571;

    /** Order size Hasse's bound allows over the largest field. */
    public static final int MAXIMUM_ORDER_BITS = MAXIMUM_FIELD_BITS + 1;

    private static final int FIELD_INDEX = 1;
    private static final int ORDER_INDEX = 4;

    private ExplicitCurve() {
    }

    /**
     * Named curves, other algorithms and malformed parameters answer {@code false}. Their own checks judge them.
     *
     * @param algorithm the key's AlgorithmIdentifier
     * @return whether the explicit parameters state a field larger than {@link #MAXIMUM_FIELD_BITS} or an order larger
     * than {@link #MAXIMUM_ORDER_BITS}
     */
    public static boolean pastTheBound(AlgorithmIdentifier algorithm) {
        ASN1Encodable parameters = algorithm.getParameters();
        if (!X9ObjectIdentifiers.id_ecPublicKey.equals(algorithm.getAlgorithm()) || parameters == null
                || !(parameters.toASN1Primitive() instanceof ASN1Sequence explicit)) {
            return false;
        }
        try {
            return fieldBits(X9FieldID.getInstance(explicit.getObjectAt(FIELD_INDEX)))
                    .compareTo(BigInteger.valueOf(MAXIMUM_FIELD_BITS)) > 0
                    || ASN1Integer
                            .getInstance(explicit.getObjectAt(ORDER_INDEX))
                            .getValue()
                            .bitLength() > MAXIMUM_ORDER_BITS;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static BigInteger fieldBits(X9FieldID field) {
        if (X9ObjectIdentifiers.prime_field.equals(field.getIdentifier())) {
            return BigInteger.valueOf(ASN1Integer.getInstance(field.getParameters()).getValue().bitLength());
        }
        return ASN1Integer.getInstance(ASN1Sequence.getInstance(field.getParameters()).getObjectAt(0)).getValue();
    }
}
