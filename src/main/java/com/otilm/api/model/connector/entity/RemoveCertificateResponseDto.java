package com.otilm.api.model.connector.entity;

import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class RemoveCertificateResponseDto {

    @Schema(
            description = "Metadata of the Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<MetadataAttributeV2> certificateMetadata;

    public List<MetadataAttributeV2> getCertificateMetadata() {
        return certificateMetadata;
    }

    public void setCertificateMetadata(List<MetadataAttributeV2> certificateMetadata) {
        this.certificateMetadata = certificateMetadata;
    }
}
