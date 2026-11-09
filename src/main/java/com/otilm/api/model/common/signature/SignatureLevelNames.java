package com.otilm.api.model.common.signature;

/**
 * A family's vocabulary for the signature levels. A family outside ETSI can name them its own way.
 */
@FunctionalInterface
public interface SignatureLevelNames {

    String nameOf(SignatureLevel level);

    /**
     * ETSI baseline names, such as {@code PAdES-B-LT} for {@link SignatureLevel#LONG_TERM} under the {@code PAdES}
     * prefix.
     */
    static SignatureLevelNames etsiBaseline(String familyPrefix) {
        String baselineProfile = familyPrefix + "-B";
        return level -> baselineProfile + "-" + switch (level) {
            case SIGNED -> "B";
            case TIMESTAMPED -> "T";
            case LONG_TERM -> "LT";
            case ARCHIVAL -> "LTA";
        };
    }
}
