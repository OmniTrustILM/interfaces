package com.otilm.api.model.core.est;

import java.util.Locale;
import java.util.Set;

/**
 * Path segments of the EST wire interface (RFC 7030 §3.2.2). The optional label sits between the base path and the
 * operation, so a label must never equal an operation name or be a dot segment that clients normalize away.
 */
public final class EstPaths {

    public static final String BASE = "/v1/protocols/.well-known/est";
    public static final String CACERTS = "cacerts";
    public static final String CSRATTRS = "csrattrs";
    public static final String SIMPLE_ENROLL = "simpleenroll";
    public static final String SIMPLE_REENROLL = "simplereenroll";
    public static final String SERVER_KEYGEN = "serverkeygen";
    public static final String FULLCMC = "fullcmc";

    private static final Set<String> OPERATIONS = Set
            .of(CACERTS, CSRATTRS, SIMPLE_ENROLL, SIMPLE_REENROLL, SERVER_KEYGEN, FULLCMC);
    private static final Set<String> DOT_SEGMENTS = Set.of(".", "..");

    private EstPaths() {
    }

    /**
     * Whether the label equals an EST operation name, ignoring case, or is a dot segment.
     *
     * @param label the candidate label, may be {@code null}
     * @return {@code true} when the label cannot be used
     */
    public static boolean isReservedLabel(String label) {
        return label != null && (DOT_SEGMENTS.contains(label) || OPERATIONS.contains(label.toLowerCase(Locale.ROOT)));
    }
}
