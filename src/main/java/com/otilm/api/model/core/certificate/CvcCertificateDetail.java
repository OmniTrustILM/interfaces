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

    @Schema(description = "Role of the certificate holder as the certificate states it: cvca, dv-domestic, "
            + "dv-foreign or terminal", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            allowableValues = {"cvca", "dv-domestic", "dv-foreign", "terminal"})
    private String role;

    @Schema(description = "Object identifier of the terminal type that the access rights are defined for: "
            + "0.4.0.127.0.7.3.1.2.1 inspection system, 0.4.0.127.0.7.3.1.2.2 authentication terminal, "
            + "0.4.0.127.0.7.3.1.2.3 signature terminal. The bits of the access rights mean different things "
            + "for each", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String terminalType;

    @Schema(description = "Access rights of the certificate holder as the certificate states them, hexadecimal",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String accessRights;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Schema(description = "First day the certificate is valid, a UTC date in format yyyy-MM-dd. "
            + "The inherited notBefore is midnight UTC at the start of this day",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", format = "date")
    private LocalDate effectiveDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @Schema(description = "Last day the certificate is valid, a UTC date in format yyyy-MM-dd; the "
            + "certificate is valid through the whole day. The inherited notAfter is midnight UTC at the start of "
            + "the following day", requiredMode = Schema.RequiredMode.NOT_REQUIRED, type = "string", format = "date")
    private LocalDate expirationDate;

    @Override
    @Schema(description = "Certificate type", requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = CertificateType.Codes.CVC)
    public CertificateType getCertificateType() {
        return super.getCertificateType();
    }
}
