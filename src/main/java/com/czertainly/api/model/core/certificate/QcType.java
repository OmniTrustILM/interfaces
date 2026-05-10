package com.czertainly.api.model.core.certificate;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * Closed set of qualified certificate types from ETSI EN 319 412-5 §4.3.4.
 */
@Schema(enumAsRef = true)
public enum QcType implements IPlatformEnum {

    ESIGN(Codes.ESIGN, "Qualified Signature", "id-etsi-qct-esign (0.4.0.1862.1.6.1) — natural person signature"),
    ESEAL(Codes.ESEAL, "Qualified Seal", "id-etsi-qct-eseal (0.4.0.1862.1.6.2) — legal entity seal"),
    WEB(Codes.WEB, "Qualified Website Authentication", "id-etsi-qct-web (0.4.0.1862.1.6.3) — website authentication"),
    ;

    private static final QcType[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    QcType(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static QcType findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown QcType {}", code)));
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
        return this.description;
    }

    public static class Codes {
        public static final String ESIGN = "esign";
        public static final String ESEAL = "eseal";
        public static final String WEB = "web";

        private Codes() {
        }
    }
}
