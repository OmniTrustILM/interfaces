package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.connector.v3.AuthorityV3ScopedRequestDto;
import com.otilm.api.model.core.enums.CertificateRequestFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Body for v3 /issue. Supports fresh issuance and register-bound issuance via
 * the optional meta field (= the meta returned by a prior /register call).
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CertificateSignRequestDtoV3 extends AuthorityV3ScopedRequestDto {

    @Schema(description = "Certificate signing request, Base64-encoded",
            format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "CSR (request) is required for issue")
    private String request;

    @Schema(description = "CSR format",
            defaultValue = "pkcs10",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateRequestFormat format;

    @Schema(description = "Issue-specific dynamic attributes (from shared /issue/attributes schema endpoint)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> attributes;

    @Schema(description = "Connector-defined metadata. On issue against a prior registration, "
                  + "this carries the meta returned by the /register response (replayed so the "
                  + "stateless connector can resolve the upstream end-entity). "
                  + "Null/empty = fresh issuance.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> meta;
}
