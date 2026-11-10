package com.otilm.api.model.core.certificate;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class X509CertificateRequestTypeDetailsDto implements CertificateRequestTypeDetailsDto {

    @Schema(description = "Common name of the requested certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String commonName;

    @Schema(description = "Subject DN of the requested certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String subjectDn;

    @Schema(description = "Subject alternative names", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, List<String>> subjectAlternativeNames;

    @Schema(description = "Alternative signature algorithm of a hybrid request",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String altSignatureAlgorithm;

    @Schema(description = "UUID of the Alternative Key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String altKeyUuid;

    @Schema(description = "Alternative Signature Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> altSignatureAttributes;

    @Schema(description = "Alternative public key algorithm of a hybrid request",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String altPublicKeyAlgorithm;
}
