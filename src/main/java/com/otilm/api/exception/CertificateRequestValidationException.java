package com.otilm.api.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Thrown when an externally supplied certificate request violates the resolved request-attribute set
 * under a strict RA-Profile policy.
 *
 * <p>The message and {@link #getDetails()} are <strong>platform-authored</strong> and safe to
 * expose to a client (REST error, EST {@code PKIFreeText}, ACME {@code error.detail}). They must
 * never be constructed from a raw upstream {@code Exception.getMessage()}.
 */
public class CertificateRequestValidationException extends Exception implements PlatformException {

    private final ArrayList<String> details;

    public CertificateRequestValidationException(String message) {
        this(message, null);
    }

    public CertificateRequestValidationException(String message, List<String> details) {
        super(message);
        this.details = details == null ? new ArrayList<>() : new ArrayList<>(details);
    }

    /** Per-finding shaped messages; never {@code null}. */
    public List<String> getDetails() {
        return Collections.unmodifiableList(details);
    }
}
