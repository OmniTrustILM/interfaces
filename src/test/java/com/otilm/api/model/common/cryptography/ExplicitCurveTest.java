package com.otilm.api.model.common.cryptography;

import java.math.BigInteger;
import java.time.Duration;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9FieldID;
import org.junit.jupiter.api.Test;

import static com.otilm.api.model.common.cryptography.ExplicitCurveFixtures.ecIdentifier;
import static com.otilm.api.model.common.cryptography.ExplicitCurveFixtures.explicit;
import static com.otilm.api.model.common.cryptography.ExplicitCurveFixtures.explicitWithField;
import static com.otilm.api.model.common.cryptography.ExplicitCurveFixtures.madeUp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExplicitCurveTest {

    @Test
    void boundsTheFieldByTheLargestNamedCurveAndTheOrderOneBitPastIt() {
        // given
        int largestNamedField = explicit("sect571r1").getCurve().getFieldSize();

        // when
        int fieldBound = ExplicitCurve.MAXIMUM_FIELD_BITS;
        int orderBound = ExplicitCurve.MAXIMUM_ORDER_BITS;

        // then
        assertEquals(largestNamedField, fieldBound);
        assertEquals(fieldBound + 1, orderBound);
    }

    @Test
    void acceptsANamedCurve() {
        // given
        AlgorithmIdentifier named = ecIdentifier(new X962Parameters(SECObjectIdentifiers.secp521r1));

        // when
        boolean past = ExplicitCurve.pastTheBound(named);

        // then
        assertFalse(past);
    }

    @Test
    void acceptsExplicitParametersOfTheLargestNamedPrimeAndBinaryCurves() {
        // given
        AlgorithmIdentifier prime = ecIdentifier(explicit("secp521r1"));
        AlgorithmIdentifier binary = ecIdentifier(explicit("sect571r1"));

        // when
        boolean primePast = ExplicitCurve.pastTheBound(prime);
        boolean binaryPast = ExplicitCurve.pastTheBound(binary);

        // then
        assertFalse(primePast);
        assertFalse(binaryPast);
    }

    @Test
    void acceptsAMadeUpPrimeFieldAtTheBound() {
        // given
        AlgorithmIdentifier atTheBound = ecIdentifier(
                madeUp(ExplicitCurve.MAXIMUM_FIELD_BITS, ExplicitCurve.MAXIMUM_ORDER_BITS, BigInteger.ONE));

        // when
        boolean past = ExplicitCurve.pastTheBound(atTheBound);

        // then
        assertFalse(past);
    }

    @Test
    void rejectsAPrimeFieldPastTheBound() {
        // given
        AlgorithmIdentifier pastTheBound = ecIdentifier(
                madeUp(ExplicitCurve.MAXIMUM_FIELD_BITS + 1, ExplicitCurve.MAXIMUM_FIELD_BITS, BigInteger.ONE));

        // when
        boolean past = ExplicitCurve.pastTheBound(pastTheBound);

        // then
        assertTrue(past);
    }

    @Test
    void rejectsABinaryFieldPastTheBound() {
        // given
        X9FieldID pastTheBound = new X9FieldID(ExplicitCurve.MAXIMUM_FIELD_BITS + 2, 1);
        AlgorithmIdentifier binary = ecIdentifier(explicitWithField("sect571r1", pastTheBound));

        // when
        boolean past = ExplicitCurve.pastTheBound(binary);

        // then
        assertTrue(past);
    }

    @Test
    void rejectsAnOrderPastTheBoundBeforeBuildingTheCurve() {
        // given
        AlgorithmIdentifier longOrder = ecIdentifier(madeUp(256, 400_000, BigInteger.TWO));

        // when
        boolean past = assertTimeoutPreemptively(Duration.ofMillis(200), () -> ExplicitCurve.pastTheBound(longOrder));

        // then
        assertTrue(past);
    }

    @Test
    void leavesImplicitParametersAndOtherAlgorithmsToTheirOwnChecks() {
        // given
        AlgorithmIdentifier implicit = ecIdentifier(DERNull.INSTANCE);
        AlgorithmIdentifier malformed = ecIdentifier(new DEROctetString(new byte[]{1, 2, 3}));
        AlgorithmIdentifier rsa = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);

        // when
        boolean implicitPast = ExplicitCurve.pastTheBound(implicit);
        boolean malformedPast = ExplicitCurve.pastTheBound(malformed);
        boolean rsaPast = ExplicitCurve.pastTheBound(rsa);

        // then
        assertFalse(implicitPast);
        assertFalse(malformedPast);
        assertFalse(rsaPast);
    }
}
