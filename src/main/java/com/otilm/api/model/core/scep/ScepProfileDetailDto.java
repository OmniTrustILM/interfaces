package com.otilm.api.model.core.scep;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.raprofile.SimplifiedRaProfileDto;
import com.otilm.api.model.core.certificate.CertificateDto;
import com.otilm.api.model.core.protocol.ProtocolCertificateAssociationsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ScepProfileDetailDto extends ScepProfileDto {
    @Schema(description = "RA Profile")
    private SimplifiedRaProfileDto raProfile;
    @Schema(description = "List of Attributes to issue a Certificate")
    private List<ResponseAttribute> issueCertificateAttributes;
    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes;
    @Schema(description = "CA Certificate for the SCEP Profile")
    private CertificateDto caCertificate;
    @Schema(description = "Intune tenant")
    private String intuneTenant;
    @Schema(description = "Intune application ID")
    private String intuneApplicationId;

    @Valid
    @Schema(description = "Associations to set for certificates issued by the protocol", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ProtocolCertificateAssociationsDto certificateAssociations;
}
