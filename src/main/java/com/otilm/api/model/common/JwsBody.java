package com.otilm.api.model.common;

import io.swagger.v3.oas.annotations.media.Schema;

public class JwsBody {

    @Schema(
            description = "Protected field of JWS",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String protectedField;
    @Schema(
            description = "JWS payload",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String payload;
    @Schema(
            description = "JWS signature",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String signature;

    public String getProtected() {
        return protectedField;
    }

    public void setProtected(String protectedField) {
        this.protectedField = protectedField;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
