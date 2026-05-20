package com.czertainly.api.model.connector.v3.certificate;

import com.czertainly.api.model.common.attribute.common.MetadataAttribute;
import com.czertainly.api.model.connector.v3.V3AuthorityScopedRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Body for v3 /status endpoints. Carries the connector-defined meta returned in the
 * original 202 Accepted so the stateless connector can resolve the in-flight operation.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CertificateOperationStatusRequestDto extends V3AuthorityScopedRequestDto {

    @Schema(description = "Connector-defined metadata returned in the original 202 Accepted response",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> meta;
}
