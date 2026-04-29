package com.otilm.api.model.connector.entity;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Getter
@Setter
public class LocationDetailResponseDto {

    @Schema(
            description = "List of Certificates in the Location",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<CertificateLocationDto> certificates;

    @Schema(
            description = "Location metadata",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<MetadataAttribute> metadata;

    @Schema(
            description = "Support for multiple Certificates in the Location",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean multipleEntries;

    @Schema(
            description = "Support for key pair management in the Location",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean supportKeyManagement;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("metadata", metadata)
                .append("multipleEntries", multipleEntries)
                .append("supportKeyManagement", supportKeyManagement)
                .toString();
    }
}
