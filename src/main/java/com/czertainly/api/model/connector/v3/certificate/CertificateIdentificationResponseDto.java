package com.czertainly.api.model.connector.v3.certificate;

import com.czertainly.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CertificateIdentificationResponseDto {

    @Schema(description = "Connector-defined metadata describing the cert as known to the upstream CA "
                  + "(end-entity name, status flags, profile binding, etc.)",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MetadataAttribute> meta;
}
