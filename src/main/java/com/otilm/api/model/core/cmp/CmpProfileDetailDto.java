package com.otilm.api.model.core.cmp;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.core.auth.ObjectActionsAware;
import com.otilm.api.model.core.certificate.CertificateDto;
import com.otilm.api.model.core.protocol.ProtocolCertificateAssociationsDto;
import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CmpProfileDetailDto extends CmpProfileDto implements ObjectActionsAware {

    @Schema(description = "List of Attributes to issue a Certificate for the associated RA Profile")
    private List<ResponseAttribute> issueCertificateAttributes;

    @Schema(description = "List of Attributes to revoke a Certificate for the associated RA Profile")
    private List<ResponseAttribute> revokeCertificateAttributes;

    @Schema(description = "List of Custom Attributes for CMP Profile")
    private List<ResponseAttribute> customAttributes;

    @Schema(description = "Protection Method for the CMP Request", requiredMode = Schema.RequiredMode.REQUIRED)
    private ProtectionMethod requestProtectionMethod;

    @Schema(description = "Protection Method for the CMP Response", requiredMode = Schema.RequiredMode.REQUIRED)
    private ProtectionMethod responseProtectionMethod;

    @Schema(description = "Signing certificate for the CMP responses")
    private CertificateDto signingCertificate;

    @Valid
    @Schema(description = "Associations to set for certificates issued by the protocol",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ProtocolCertificateAssociationsDto certificateAssociations;

    @Schema(description = ObjectActionsAware.ALLOWED_ACTIONS_DESCRIPTION)
    private List<ResourceAction> allowedActions;

}
