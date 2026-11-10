package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/**
 * The details only one certificate type has. The subtype is selected by the {@code certificateType} property of the
 * certificate that carries it, so a new type adds a subtype and leaves the DTOs that carry it unchanged.
 */
@JsonSubTypes({@JsonSubTypes.Type(value = X509CertificateTypeDetailsDto.class, name = CertificateType.Codes.X509)})
@Schema(description = "Details specific to the certificate type", type = "object",
        subTypes = {X509CertificateTypeDetailsDto.class})
public interface CertificateTypeDetailsDto extends Serializable {
}
