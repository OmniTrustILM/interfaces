package com.otilm.api.model.core.scep;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

/**
 * According to the RFC 8894
 * <a href="https://www.rfc-editor.org/rfc/rfc8894.html#name-messagetype">messageType</a>
 * It may contain custom MessageType codes if required (but out of scope of the standard)
 */
@Schema(enumAsRef = true)
public enum MessageType {
    RESERVED(0, "Reserved", ""),
    CERT_REP(3, "CertRep", "Response to certificate or CRL request."),
    RENEWAL_REQ(17, "RenewalReq", "PKCS#10 certificate request authenticated with an existing certificate."),
    PKCS_REQ(19, "PKCSReq", "PKCS#10 certificate request authenticated with a shared secret."),
    CERT_POLL(20, "CertPoll", "Certificate polling in manual enrolment."),
    GET_CERT(21, "GetCert", "Retrieve a certificate."),
    GET_CRL(22, "GetCRL", "Retrieve a CRL.");

    private static final MessageType[] VALUES;

    static {
        VALUES = values();
    }

    private final int value;
    private final String name;
    private final String description;

    MessageType(int value, String name, String description) {
        this.value = value;
        this.name = name;
        this.description = description;
    }

    /**
     * Return the {@code MessageType} enum constant with the specified value.
     *
     * @param value the value of the enum to be returned
     * @return the enum constant with the specified value
     * @throws IllegalArgumentException if this enum has no constant for the specified value
     */
    public static MessageType valueOf(int value) {
        MessageType messageType = resolve(value);
        if (messageType == null) {
            throw new IllegalArgumentException("No matching constant for [" + value + "]");
        }
        return messageType;
    }

    /**
     * Resolve the given value to an {@code MessageType}, if possible.
     *
     * @param value the value of the algorithm
     * @return the corresponding {@code MessageType}, or {@code null} if not found
     */
    @Nullable
    public static MessageType resolve(int value) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (MessageType messageType : VALUES) {
            if (messageType.value == value) {
                return messageType;
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
