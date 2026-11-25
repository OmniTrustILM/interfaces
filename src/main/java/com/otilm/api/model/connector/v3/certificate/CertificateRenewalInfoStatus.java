package com.otilm.api.model.connector.v3.certificate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(name = "CertificateRenewalInfoStatusV3", enumAsRef = true)
public enum CertificateRenewalInfoStatus implements IPlatformEnum {

    AVAILABLE("available", "Available", "The CA advises renewing inside the returned window"),
    UNKNOWN("unknown", "Unknown", "The CA does not know the certificate"),
    NOT_SUPPORTED("notSupported", "Not supported", "The CA gives no renewal advice for the certificate"),
    RETRY("retry", "Retry", "Temporary failure or not answered in time; ask again later");

    private final String code;
    private final String label;
    private final String description;

    CertificateRenewalInfoStatus(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @Override
    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Reads a code from JSON; an unknown code becomes null so one unrecognised item does not fail the response.
     */
    @JsonCreator
    static CertificateRenewalInfoStatus fromJson(String code) {
        return Arrays.stream(values()).filter(s -> s.code.equals(code)).findFirst().orElse(null);
    }

    public static CertificateRenewalInfoStatus findByCode(String code) {
        return Arrays
                .stream(values())
                .filter(s -> s.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown certificate renewal info status code {}", code)));
    }
}
