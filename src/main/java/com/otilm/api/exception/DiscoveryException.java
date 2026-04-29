package com.otilm.api.exception;

public class DiscoveryException  extends Exception {

    public DiscoveryException() {
        super();
    }

    public DiscoveryException(String discoveryName, String message) {
        super("Error in discovery '%s': %s".formatted(discoveryName, message));
    }

    public DiscoveryException(String discoveryName, String message, Throwable cause) {
        super("Error in discovery '%s': %s".formatted(discoveryName, message), cause);
    }
}
