package com.czertainly.api.model.connector.v3.certificate;

import com.czertainly.api.model.connector.v3.V3AuthorityScopedRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body for the three v3 attribute-schema-listing endpoints:
 * {@code POST /v3/authorityProvider/certificates/issue/attributes},
 * {@code POST /v3/authorityProvider/certificates/revoke/attributes}, and
 * {@code POST /v3/authorityProvider/certificates/register/attributes}.
 *
 * <p>Inherits {@code authorityAttributes} (so the stateless connector can identify and authenticate
 * to the upstream CA) and {@code raProfileAttributes} (so the connector can scope the returned
 * dynamic-attribute schema to the specific RA profile — different end-entity profiles, certificate
 * templates, or internal profiles can have different attribute sets).</p>
 *
 * <p>No additional fields — the body is the inherited authority + RA-profile context.</p>
 */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(description = "Authority + RA-profile context used by the stateless v3 connector to return "
        + "the dynamic-attribute schema for issue, revoke, or register on a specific profile.")
public class CertificateAttributeListRequestDto extends V3AuthorityScopedRequestDto {
}
