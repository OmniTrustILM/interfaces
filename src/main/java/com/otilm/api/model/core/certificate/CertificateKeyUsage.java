package com.otilm.api.model.core.certificate;

import com.otilm.api.model.common.enums.BitMaskEnum;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

@Schema(enumAsRef = true)
public enum CertificateKeyUsage implements IPlatformEnum, BitMaskEnum {

    DIGITAL_SIGNATURE("digitalSignature", "Digital Signature", 0),
    NON_REPUDIATION("nonRepudiation", "Non Repudiation", 1),
    KEY_ENCIPHERMENT("keyEncipherment", "Key Encipherment", 2),
    DATA_ENCIPHERMENT("dataEncipherment", "Data Encipherment", 3),
    KEY_AGREEMENT("keyAgreement", "Key Agreement", 4),
    KEY_CERT_SIGN("keyCertSign", "Key Cert Sign", 5),
    CRL_SIGN("cRLSign", "CRL Sign", 6),
    ENCIPHER_ONLY("encipherOnly", "Encipher Only", 7),
    DECIPHER_ONLY("decipherOnly", "Decipher Only", 8);

    private final String code;
    private final String label;
    @Getter
    private final int index;

    CertificateKeyUsage(String code, String label, int index) {
        this.code = code;
        this.label = label;
        this.index = index;
    }

    @Override
    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() {
        return "";
    }

    public static CertificateKeyUsage fromIndex(int index) {
        return Arrays.stream(values())
                .filter(k -> k.index == index)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown index: " + index));
    }

    public static CertificateKeyUsage fromCode(String code) {
        return Arrays.stream(values())
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown code: " + code));
    }


    public static EnumSet<CertificateKeyUsage> convertBitMaskToSet(int bitmask) {
        return BitMaskEnum.convertBitMaskToSet(bitmask, CertificateKeyUsage.class);
    }

    @Override
    public int getBit() {
        return 1 << getIndex();
    }
}
