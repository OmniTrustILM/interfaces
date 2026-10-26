package com.otilm.api.model.connector.cryptography.v2.lifecycle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(name = "OperationEventType", enumAsRef = true,
        description = "Reported step of a signature activation, from a closed list")
public enum OperationEventType implements IPlatformEnum {

    OPERATION_ACCEPTED("operationAccepted", "Operation accepted",
            "The signature activation module holds a request for the operation"),
    AUTHORIZATION_REQUESTED("authorizationRequested", "Authorization requested",
            "The module's authorization material for the operation is in flight"),
    AUTHORIZATION_PROGRESS("authorizationProgress", "Authorization progress",
            "Part of a multi-party authorization completed. The step may repeat"),
    AUTHORIZATION_GRANTED("authorizationGranted", "Authorization granted",
            "The module realized the sole-control decision inside the signature creation device"),
    SIGNATURE_CREATED("signatureCreated", "Signature created",
            "The module created the signature over one item or the whole batch"),
    REJECTED("rejected", "Rejected", "The module refused the authorization"),
    EXPIRED("expired", "Expired", "The authorization lapsed before the signature was created"),
    FAILED("failed", "Failed", "The operation failed for a technical reason");

    private final String code;
    private final String label;
    private final String description;

    OperationEventType(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static OperationEventType findByCode(String code) {
        return Arrays
                .stream(values())
                .filter(type -> type.code.equals(code))
                .findFirst()
                .orElseThrow(
                        () -> new ValidationException(ValidationError.create("Unknown operation event type {}", code)));
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
}
