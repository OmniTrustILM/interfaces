package com.czertainly.api.model.core.signing;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * Authentication methods a TSP Profile may accept on the TSP protocol endpoints.
 *
 * <p><strong>Naming convention: {@code <TRANSPORT>_<SECRET_TYPE>}.</strong>
 * Each constant pairs <em>how</em> the caller connects (the wire signal the {@code TSP Authentication Filter} detects)
 * with <em>what</em> secret is carried on that transport. The halves are coupled by design: the transport that
 * selects a method also fixes its secret type, so detection is unambiguous and each constant is self-describing.
 *
 * <ul>
 *   <li>{@link #CLIENT_CERTIFICATE} — transport: mutual-TLS client cert ({@code ssl-client-cert} header); secret: an X.509 certificate.</li>
 *   <li>{@link #BEARER_TOKEN} — transport: {@code Authorization: Bearer}; secret: a JWT.</li>
 *   <li>{@link #BASIC_PASSWORD} — transport: {@code Authorization: Basic}; secret: a username/password.</li>
 * </ul>
 *
 * New methods MUST keep the {@code <TRANSPORT>_<SECRET_TYPE>} form so each transport maps 1:1 to one constant.
 */
@Schema(enumAsRef = true)
public enum TspAuthenticationMethod implements IPlatformEnum {

    CLIENT_CERTIFICATE(Codes.CLIENT_CERTIFICATE, "Client Certificate", "Mutual-TLS client certificate (ssl-client-cert header)"),
    BEARER_TOKEN(Codes.BEARER_TOKEN, "Bearer Token", "JWT carried in the Authorization: Bearer header"),
    BASIC_PASSWORD(Codes.BASIC_PASSWORD, "Basic Password", "Username/password carried in the Authorization: Basic header"),
    ;

    private static final TspAuthenticationMethod[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    TspAuthenticationMethod(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static TspAuthenticationMethod findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown TSP authentication method code")));
    }

    @Override
    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public static class Codes {
        public static final String CLIENT_CERTIFICATE = "clientCertificate";
        public static final String BEARER_TOKEN = "bearerToken";
        public static final String BASIC_PASSWORD = "basicPassword";

        private Codes() {
        }
    }
}
