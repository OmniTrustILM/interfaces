package com.czertainly.api.model.connector.v3.certificate;

import com.czertainly.api.model.common.attribute.common.MetadataAttribute;
import com.czertainly.api.model.connector.v3.AuthorityV3ScopedRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Body for v3 /cancel endpoints. Structurally identical to StatusRequestDto today,
 * kept separate for future divergence (e.g. operator-supplied cancellation reason).
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CertificateOperationCancelRequestDtoV3 extends AuthorityV3ScopedRequestDto {

    @Schema(description = "Connector-defined metadata returned in the original 202 Accepted response",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> meta;
}
