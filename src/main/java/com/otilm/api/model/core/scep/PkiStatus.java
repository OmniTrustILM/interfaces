package com.otilm.api.model.core.scep;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

/**
 * According to the RFC 8894
 * <a href="https://www.rfc-editor.org/rfc/rfc8894.html#name-pkistatus">pkiStatus</a>
 * It may contain custom PkiStatus codes if required (but out of scope of the standard)
 */
@Schema(enumAsRef = true)
public enum PkiStatus {
    SUCCESS(0, "SUCCESS", "Request granted."),
    FAILURE(2, "FAILURE", "Request rejected."),
    PENDING(3, "PENDING", "Request pending for manual approval.");

    private static final PkiStatus[] VALUES;

    static {
        VALUES = values();
    }

    private final int value;
    private final String name;
    private final String description;

    PkiStatus(int value, String name, String description) {
        this.value = value;
        this.name = name;
        this.description = description;
    }

    /**
     * Return the {@code PkiStatus} enum constant with the specified value.
     *
     * @param value the value of the enum to be returned
     * @return the enum constant with the specified value
     * @throws IllegalArgumentException if this enum has no constant for the specified value
     */
    public static PkiStatus valueOf(int value) {
        PkiStatus pkiStatus = resolve(value);
        if (pkiStatus == null) {
            throw new IllegalArgumentException("No matching constant for [" + value + "]");
        }
        return pkiStatus;
    }

    /**
     * Resolve the given value to an {@code PkiStatus}, if possible.
     *
     * @param value the value of the algorithm
     * @return the corresponding {@code PkiStatus}, or {@code null} if not found
     */
    @Nullable
    public static PkiStatus resolve(int value) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (PkiStatus pkiStatus : VALUES) {
            if (pkiStatus.value == value) {
                return pkiStatus;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name + " ("+String.valueOf(value)+")";
    }
}
