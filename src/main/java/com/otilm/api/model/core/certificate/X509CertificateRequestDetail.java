package com.otilm.api.model.core.certificate;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class X509CertificateRequestDetail extends CertificateRequestDetail {

    @Schema(description = "Subject DN of the Certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String subjectDn;

    @Schema(description = "Subject alternative names", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<String, List<String>> subjectAlternativeNames;

    @Schema(description = "Certificate alternative signature algorithm",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String altSignatureAlgorithm;

    @Schema(description = "Alternative public key algorithm of a hybrid request",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String altPublicKeyAlgorithm;

    @Schema(description = "Alternative Signature Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> altSignatureAttributes;

    @Schema(description = "UUID of the Alternative Key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String altKeyUuid;
}
