package com.otilm.api.model.common.cryptography;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.math.ec.ECCurve;

/** Explicit curve parameters, made up or taken from the named curves. */
public final class ExplicitCurveFixtures {

    private static final int FIELD_ID_INDEX = 1;

    private ExplicitCurveFixtures() {
    }

    /**
     * A curve through a random point over a prime field, with the given field size, order size and cofactor. Its order
     * is arbitrary.
     */
    public static X9ECParameters madeUp(int fieldBits, int orderBits, BigInteger cofactor) {
        SecureRandom random = new SecureRandom();
        BigInteger prime = BigInteger.probablePrime(fieldBits, random);
        BigInteger a = new BigInteger(fieldBits - 1, random);
        BigInteger x = new BigInteger(fieldBits - 1, random);
        BigInteger y = new BigInteger(fieldBits - 1, random);
        BigInteger b = y.pow(2).subtract(x.pow(3)).subtract(a.multiply(x)).mod(prime);
        BigInteger order = BigInteger.ONE.shiftLeft(orderBits - 1).add(BigInteger.ONE);
        ECCurve curve = new ECCurve.Fp(prime, a, b, order, cofactor);
        return new X9ECParameters(curve, new X9ECPoint(curve.createPoint(x, y), false), order, cofactor);
    }

    /** The named curve's domain, stated as explicit parameters. */
    public static X9ECParameters explicit(String curveName) {
        return ECNamedCurveTable.getByName(curveName);
    }

    public static ASN1Sequence explicitReplacing(String curveName, int index, ASN1Encodable element) {
        ASN1Encodable[] elements = ((ASN1Sequence) explicit(curveName).toASN1Primitive()).toArray();
        elements[index] = element;
        return new DERSequence(elements);
    }

    public static ASN1Sequence explicitWithField(String curveName, ASN1Encodable fieldId) {
        return explicitReplacing(curveName, FIELD_ID_INDEX, fieldId);
    }

    public static AlgorithmIdentifier ecIdentifier(ASN1Encodable parameters) {
        return new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, parameters);
    }

    /** An EC key's AlgorithmIdentifier carrying the given domain as explicit parameters. */
    public static AlgorithmIdentifier ecIdentifier(X9ECParameters domain) {
        return ecIdentifier(new X962Parameters(domain));
    }
}
