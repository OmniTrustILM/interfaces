package com.otilm.api.model.core.est;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.protocol.ProtocolCertificateAssociationsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EstProfileDetailDto extends EstProfileDto {

    @Schema(description = "Basic-auth Secrets RAs authenticate with. A Secret counts only when its source vault "
            + "profile is enabled")
    private List<NameAndUuidDto> basicCredentialSecrets;

    @Schema(description = "CA certificates that client certificates must chain to")
    private List<NameAndUuidDto> trustedCaCertificates;

    @Schema(description = "Whether reenrollment with the current key is allowed",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean allowRenewal;

    @Schema(description = "Whether reenrollment with a new key is allowed", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean allowRekey;

    @Schema(description = "Days before expiry from which reenrollment is allowed. Empty or 0 allows it from half of the certificate validity")
    private Integer renewalThreshold;

    @Schema(description = "Token profile that generates and keeps recoverable keys. It was enabled, export-capable and "
            + "allowed signing when the EST Profile was saved, and cannot be deleted or disabled while the EST Profile "
            + "uses it")
    private NameAndUuidDto keyRecoveryTokenProfile;

    @Schema(description = "Key creation attributes for the key recovery token profile, validated against its "
            + "connector when the EST Profile was saved")
    private List<ResponseAttribute> keyRecoveryKeyAttributes;

    @Schema(description = "Seconds that enroll and reenroll wait for the certificate before answering 202",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int issueWaitTimeout;

    @Schema(description = "Seconds a client is told to wait, as Retry-After, before it asks again after a 202",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int retryAfter;

    @Schema(description = "Seconds that /serverkeygen waits for the certificate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int serverKeyGenerationTimeout;

    @Schema(description = "Attributes to issue certificates through the EST Profile's own RA Profile")
    private List<ResponseAttribute> issueCertificateAttributes;

    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes;

    @Schema(description = "Owner, groups and custom attributes applied to certificates issued through the EST Profile")
    private ProtocolCertificateAssociationsDto certificateAssociations;
}
