package com.otilm.api.model.core.scep;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

/**
 * According to the RFC 8894
 * <a href="https://www.rfc-editor.org/rfc/rfc8894.html#name-failinfo-and-failinfotext">failInfo and failInfoText</a>
 * It may contain custom FailInfo codes if required (but out of scope of the standard)
 */
@Schema(enumAsRef = true)
public enum FailInfo {
    BAD_ALG(0, "badAlg", "Unrecognised or unsupported algorithm."),
    BAD_MESSAGE_CHECK(1, "badMessageCheck", "Integrity check (meaning signature verification of the CMS message) failed."),
    BAD_REQUEST(2, "badRequest", "Transaction not permitted or supported."),
    BAD_TIME(3, "badTime", "The signingTime attribute from the CMS authenticatedAttributes was not sufficiently close to the system time."),
    BAD_CERT_ID(4, "badCertId", "No certificate could be identified matching the provided criteria.");

    private static final FailInfo[] VALUES;

    static {
        VALUES = values();
    }

    private final int value;
    private final String name;
    private final String description;

    FailInfo(int value, String name, String description) {
        this.value = value;
        this.name = name;
        this.description = description;
    }

    /**
     * Return the {@code FailInfo} enum constant with the specified value.
     *
     * @param value the value of the enum to be returned
     * @return the enum constant with the specified value
     * @throws IllegalArgumentException if this enum has no constant for the specified value
     */
    public static FailInfo valueOf(int value) {
        FailInfo failInfo = resolve(value);
        if (failInfo == null) {
            throw new IllegalArgumentException("No matching constant for [" + value + "]");
        }
        return failInfo;
    }

    /**
     * Resolve the given value to an {@code FailInfo}, if possible.
     *
     * @param value the value of the algorithm
     * @return the corresponding {@code FailInfo}, or {@code null} if not found
     */
    @Nullable
    public static FailInfo resolve(int value) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (FailInfo failInfo : VALUES) {
            if (failInfo.value == value) {
                return failInfo;
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
