package com.otilm.util;

import com.otilm.api.model.common.enums.BitMaskEnum;
import com.otilm.api.model.core.certificate.CertificateKeyUsage;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;

class BitMaskEnumTest {

    @Test
    void testKeyUsage() {
        EnumSet<KeyUsage> keyUsages = EnumSet.of(KeyUsage.SIGN, KeyUsage.WRAP, KeyUsage.UNWRAP);
        int bitMask = BitMaskEnum.convertSetToBitMask(keyUsages);
        Assertions.assertEquals(keyUsages, KeyUsage.convertBitMaskToSet(bitMask));
        keyUsages = EnumSet.noneOf(KeyUsage.class);
        bitMask = BitMaskEnum.convertSetToBitMask(keyUsages);
        Assertions.assertEquals(keyUsages, KeyUsage.convertBitMaskToSet(bitMask));
    }

    @Test
    void testCertificateKeyUsage() {
        EnumSet<CertificateKeyUsage> keyUsages = EnumSet.of(CertificateKeyUsage.KEY_AGREEMENT, CertificateKeyUsage.KEY_ENCIPHERMENT, CertificateKeyUsage.KEY_CERT_SIGN);
        int bitMask = BitMaskEnum.convertSetToBitMask(keyUsages);
        Assertions.assertEquals(keyUsages, CertificateKeyUsage.convertBitMaskToSet(bitMask));
    }

}
