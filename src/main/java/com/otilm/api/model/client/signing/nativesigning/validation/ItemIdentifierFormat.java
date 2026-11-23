package com.otilm.api.model.client.signing.nativesigning.validation;

/**
 * Bound on the identifier a caller gives each item of a native signing request. The platform writes identifiers into
 * log lines and signing-record names, where a control character could forge a log line, and a NUL fails the record
 * after the key has already signed.
 */
public final class ItemIdentifierFormat {

    public static final int MAX_LENGTH = 255;

    /** Unicode general category Cc, spelled as ranges so that clients in any regex dialect can apply it. */
    public static final String REGEX = "^[^\\x00-\\x1F\\x7F-\\x9F]*$";

    public static final String LENGTH_MESSAGE = "identifier must be at most " + MAX_LENGTH + " characters";
    public static final String MESSAGE = "identifier must not contain control characters";

    private ItemIdentifierFormat() {
    }
}
