package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * The content only one certificate type's requests have. The subtype is selected by the {@code certificateType}
 * property of the request that carries it.
 */
@JsonSubTypes({
        @JsonSubTypes.Type(value = X509CertificateRequestTypeDetailsDto.class, name = CertificateType.Codes.X509)})
@Schema(description = "Request content specific to the certificate type", type = "object",
        subTypes = {X509CertificateRequestTypeDetailsDto.class})
public interface CertificateRequestTypeDetailsDto extends Serializable {
}
