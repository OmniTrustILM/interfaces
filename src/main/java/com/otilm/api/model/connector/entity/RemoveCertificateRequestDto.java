package com.otilm.api.model.connector.entity;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RemoveCertificateRequestDto {

    @Schema(
            description = "Metadata of the Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<MetadataAttribute> certificateMetadata;

    @Schema(
            description = "List of Location Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute>locationAttributes;
}
