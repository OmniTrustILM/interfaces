package com.czertainly.api.model.core.secret;

import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum SecretState implements IPlatformEnum {

    INACTIVE("inactive", "Inactive", "Secret is managed but not active and is not available to use"),
    ACTIVE("active", "Active", "Secret is active and available for use"),
    EXPIRED("expired", "Expired", "Secret has expired and is no longer valid"),
    REVOKED("revoked", "Revoked", "Secret has been revoked and is no longer valid"),
    PENDING_APPROVAL("pendingApproval", "Pending Approval", "Secret is pending approval and is not available for use"),
    REJECTED("rejected", "Rejected", "Secret has been rejected and is not available for use"),
    ;

    private final String code;
    private final String label;
    private final String description;

    SecretState(String code, String label, String description) {
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
}
