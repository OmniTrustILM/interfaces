package com.otilm.api.model.client.certificate;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class CertificateComplianceCheckDto {
    @Schema(description = "List of UUIDs of the Certificates")
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
