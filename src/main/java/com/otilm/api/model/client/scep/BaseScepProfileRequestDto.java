package com.otilm.api.model.client.scep;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.protocol.ProtocolCertificateAssociationsRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.ToString;

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

    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Challenge Password for the SCEP Request (write-only).",
            accessMode = Schema.AccessMode.WRITE_ONLY)
    private String challengePassword;

    /**
     * Tri-state toggle governing the write-only {@link #challengePassword}. The form does not prefill the
     * secret, so a blank value must never be treated as "clear".
     * <ul>
     *   <li>{@code null} (field absent) — keep the stored password unchanged; on create this means no password
     *       unless a value is supplied (legacy / opt-out behavior).</li>
     *   <li>{@code true} + non-blank {@code challengePassword} — set the new password.</li>
     *   <li>{@code true} + blank {@code challengePassword} — keep the stored password; rejected when none is stored.</li>
     *   <li>{@code false} — clear / do not set a challenge password.</li>
     * </ul>
     * MUST stay nullable and MUST mean keep-when-null. Do NOT normalize it with {@code Boolean.TRUE.equals(...)}
     * like {@code enableIntune}: that would turn an absent toggle into {@code false} and silently wipe stored
     * passwords for clients that do not send the field.
     */
    @Schema(
            description = "Challenge password protection toggle. Omit to keep the stored password unchanged; "
                    + "true to set a new password (or keep the existing one if left blank); false to remove it.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Boolean enableChallengePassword;

    @Schema(description = "Status of Intune")
    private Boolean enableIntune;

    @Schema(description = "Intune Tenant")
    private String intuneTenant;

    @Schema(description = "Intune Application ID")
    private String intuneApplicationId;

    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Intune Application Key (write-only).",
            accessMode = Schema.AccessMode.WRITE_ONLY)
    private String intuneApplicationKey;

    @Valid
    @Schema(description = "Associations to set for certificates issued by the protocol", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private ProtocolCertificateAssociationsRequestDto certificateAssociations;
}
