package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.connector.v3.AuthorityV3ScopedRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Body for the v3 issue/revoke/register attribute-schema-listing endpoints. */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(description = "Authority + RA-profile context for listing the dynamic-attribute schema.")
public class CertificateAttributeListRequestDtoV3 extends AuthorityV3ScopedRequestDto {
}
