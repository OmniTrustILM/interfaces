package com.otilm.api.model.client.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CertificateComplianceCheckDto {
    @NotEmpty
    @Schema(description = "List of UUIDs of the Certificates", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> certificateUuids;

    public List<String> getCertificateUuids() {
        return certificateUuids;
    }

    public void setCertificateUuids(List<String> certificateUuids) {
        this.certificateUuids = certificateUuids;
    }

    @Override
    public String toString() {
        return "CertificateComplianceCheckDto{" +
                "certificateUuids=" + certificateUuids +
                '}';
    }
}
