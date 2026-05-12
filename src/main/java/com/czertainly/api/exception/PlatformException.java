package com.czertainly.api.exception;

/**
 * Marker interface implemented by all platform domain exceptions.
 * Use {@code instanceof PlatformException} to distinguish platform errors from
 * unexpected runtime failures before forwarding messages to external callers.
 */
public interface PlatformException {}
