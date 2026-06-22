package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.core.certificate.CertificateType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "certificateType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = X509RequestContent.class, name = CertificateType.Codes.X509)
})
@Schema(
        description = "Typed certificate request content carrying the decoded identity intent; " +
                "coexists with the raw CSR which remains authoritative for the public key and proof of possession",
        subTypes = {X509RequestContent.class}
)
public abstract class CertificateRequestContent {

    @Schema(description = "Certificate type, determines the concrete subtype", requiredMode = Schema.RequiredMode.REQUIRED)
    private CertificateType certificateType;
}
