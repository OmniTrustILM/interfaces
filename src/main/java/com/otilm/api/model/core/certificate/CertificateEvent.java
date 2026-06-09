package com.otilm.api.model.core.certificate;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

public enum CertificateEvent {
    ISSUE("Issue Certificate"),
    REQUEST("Request Certificate"),
    RENEW("Renew Certificate"),
    REKEY("Rekey Certificate"),
    REVOKE("Revoke Certificate"),
    DELETE("Delete Certificate"),
    APPROVAL_REQUEST("Approve action requested"),
    APPROVAL_CLOSE("Approve action closed"),
    UPDATE_STATE("Update State"),
    UPDATE_VALIDATION_STATUS("Update Validation Status"),
    UPDATE_RA_PROFILE("Update RA Profile"),
    UPDATE_ENTITY("Update Entity"),
    UPDATE_GROUP("Update Group"),
    UPDATE_OWNER("Update Owner"),
    UPLOAD("Upload Certificate"),
    DISCOVERY("Certificate Discovered"),
    UPDATE_LOCATION("Update Location"),
    ARCHIVE("Archive certificate"),
    UNARCHIVE("Unarchive certificate");

    @Schema(description = "Certificate Event",
            examples = {"Issue Certificate"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;

    CertificateEvent(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return this.code;
    }

    @JsonCreator
    public static CertificateEvent findByCode(String code) {
        return Arrays.stream(CertificateEvent.values())
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown event {}", code)));
    }
}
