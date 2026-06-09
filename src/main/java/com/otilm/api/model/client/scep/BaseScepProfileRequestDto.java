package com.otilm.api.model.client.scep;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.protocol.ProtocolCertificateAssociationsRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class BaseScepProfileRequestDto {
    @Schema(
            description = "Description of the SCEP Profile",
            examples = {"Sample description"}
    )
    private String description;

    @Schema(
            description = "RA Profile UUID",
            examples = {"6b55de1c-844f-11ec-a8a3-0242ac120002"}
    )
    private String raProfileUuid;

    @Schema(
            description = "List of Attributes to issue Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> issueCertificateAttributes;

    @Schema(
            description = "UUID of the Certificate to be used as CA Certificate for SCEP Requests",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String caCertificateUuid;

    @Schema(
            description = "List of Custom Attributes"
    )
    private List<RequestAttribute> customAttributes;

    @Schema(
            description = "Minimum expiry days to allow renewal of certificate. Empty or the value '0' will be " +
                    "considered as null and half life of the certificate validity will be considered for the protocol. Default value is half of certificate validity."
    )
    private Integer renewalThreshold;

    @Schema(
            description = "Include CA Certificate in the SCEP Message response",
            defaultValue = "False"
    )
    private boolean includeCaCertificate;

    @Schema(
            description = "Include CA Certificate Chain in the SCEP Message response",
            defaultValue = "False"
    )
    private boolean includeCaCertificateChain;

    @Schema(description = "Challenge Password for the SCEP Request")
    private String challengePassword;

    @Schema(description = "Status of Intune")
    private Boolean enableIntune;

    @Schema(description = "Intune Tenant")
    private String intuneTenant;

    @Schema(description = "Intune Application ID")
    private String intuneApplicationId;

    @Schema(description = "Intune Application Key")
    private String intuneApplicationKey;

    @Valid
    @Schema(description = "Associations to set for certificates issued by the protocol", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ProtocolCertificateAssociationsRequestDto certificateAssociations;
}
