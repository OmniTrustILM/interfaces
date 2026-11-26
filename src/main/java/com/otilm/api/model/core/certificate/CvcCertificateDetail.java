package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CvcCertificateDetail extends CertificateDetail {

    @Schema(description = "Holder reference of the certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String holderReference;

    @Schema(description = "Authority reference: the holder reference of the certificate that signed this one",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String authorityReference;

    @Schema(description = "Role of the certificate holder as the certificate states it",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String role;

    @Schema(description = "Access rights of the certificate holder as the certificate states them, hexadecimal",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String accessRights;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Schema(description = "First day the certificate is valid, in format yyyy-MM-dd",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", format = "date")
    private LocalDate effectiveDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Schema(description = "Last day the certificate is valid, in format yyyy-MM-dd; the certificate is valid "
            + "through the whole day", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string",
            format = "date")
    private LocalDate expirationDate;
}
