package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * One certificate of a v3 /renewalInfo batch.
 */
@Getter
@Setter
@ToString
public class CertificateRenewalInfoRequestItemDtoV3 {

    @Schema(description = "Opaque reference chosen by Core, unique within the request and echoed back in the result",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "reference is required")
    private String reference;

    @Schema(description = "Base64 of the DER-encoded certificate to look up at the upstream CA", format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "certificate is required")
    private String certificate;

    @Schema(description = "Connector-defined metadata Core stored for the certificate, when it has any",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> meta;
}
