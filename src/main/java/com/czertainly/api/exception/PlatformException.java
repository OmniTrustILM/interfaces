package com.czertainly.api.exception;

import java.util.Objects;

/**
 * Marker interface implemented by all platform domain exceptions.
 * Use {@link #safeMessage} to gate exception messages before forwarding them
 * to external callers (REST responses, protocol replies, bulk-action DTOs).
 */
public interface PlatformException {

    /**
     * Returns the exception's own message when {@code t} is a platform-shaped exception ({@code instanceof PlatformException})
     * with a non-null, non-cause-derived message; otherwise returns {@code fallback}. Use this at every wire boundary
     * instead of raw {@code t.getMessage()} so that only intentionally shaped messages reach callers.
     * <p>
     * A {@code null} {@code t} is treated as a non-platform exception and returns {@code fallback}.
     * Messages that equal {@code t.getCause().toString()} are rejected as implicitly derived from the wrapped
     * cause (the behaviour of {@code Throwable(Throwable)} constructors) and may carry raw infrastructure details.
     *
     * @param t        the throwable to inspect; may be {@code null}
     * @param fallback the message to return when {@code t} is not a {@link PlatformException} or carries no safe
     *                 message; must not be {@code null}
     * @throws NullPointerException if {@code fallback} is {@code null}
     */
    static String safeMessage(Throwable t, String fallback) {
        Objects.requireNonNull(fallback, "fallback must not be null");
        if (!(t instanceof PlatformException) || t.getMessage() == null) return fallback;
        if (t.getCause() != null && t.getMessage().equals(t.getCause().toString())) return fallback;
        return t.getMessage();
    }
}
