package com.czertainly.api.exception;

/**
 * Marker interface implemented by all platform domain exceptions.
 * Use {@link #safeMessage} to gate exception messages before forwarding them
 * to external callers (REST responses, protocol replies, bulk-action DTOs).
 */
public interface PlatformException {

    /**
     * Returns the exception's own message when {@code t} is a platform-shaped exception ({@code instanceof PlatformException})
     * with a non-null message; otherwise returns {@code fallback}. Use this at every wire boundary instead of raw
     * {@code t.getMessage()} so that only intentionally shaped messages reach callers.
     * <p>
     * A {@code null} {@code t} is treated as a non-platform exception and returns {@code fallback}.
     *
     * @param t        the throwable to inspect; may be {@code null}
     * @param fallback the message to return when {@code t} is not a {@link PlatformException} or carries no message;
     *                 must be non-null — a {@code null} fallback will propagate as-is to the caller
     */
    static String safeMessage(Throwable t, String fallback) {
        return (t instanceof PlatformException && t.getMessage() != null)
                ? t.getMessage()
                : fallback;
    }
}
