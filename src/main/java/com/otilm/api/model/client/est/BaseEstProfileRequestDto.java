package com.otilm.api.model.client.est;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.est.EstAuthenticationPolicy;
import com.otilm.api.model.core.protocol.ProtocolCertificateAssociationsRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class BaseEstProfileRequestDto {

    @Schema(description = "Description of the EST Profile", examples = {"Devices enrolling with vendor certificates"})
    private String description;

    @Schema(description = "UUID of the RA Profile that serves requests sent to the EST Profile's own label",
            examples = {"6b55de1c-844f-11ec-a8a3-0242ac120002"})
    private UUID raProfileUuid;

    @Schema(description = "Serve requests sent without a label. At most one EST Profile is the system default",
            defaultValue = "false")
    private boolean systemDefault;

    @NotNull
    @Schema(description = "How clients authenticate to enroll or to receive a server-generated key",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private EstAuthenticationPolicy authenticationPolicy;

    @Schema(description = "UUIDs of basic-auth Secrets RAs authenticate with. Only for the raBasic policy. A Secret "
            + "counts only when its source vault profile is enabled")
    private List<@NotNull UUID> basicCredentialSecretUuids = new ArrayList<>();

    @Schema(description = "UUIDs of CA certificates that client certificates must chain to. Only for the vendorMtls and raMtls policies")
    private List<@NotNull UUID> trustedCaCertificateUuids = new ArrayList<>();

    @Schema(description = "Allow reenrollment with the current key", defaultValue = "true")
    private boolean allowRenewal = true;

    @Schema(description = "Allow reenrollment with a new key", defaultValue = "true")
    private boolean allowRekey = true;

    @Min(0)
    @Schema(description = "Days before expiry from which reenrollment is allowed. Empty or 0 allows it from half of the certificate validity",
            minimum = "0")
    private Integer renewalThreshold;

    @Schema(description = "Allow server-side key generation through /serverkeygen. Without keyRecovery the key is "
            + "generated in memory and returned to the client; it is not kept", defaultValue = "false")
    private boolean serverKeyGeneration;

    @Schema(description = "Generate server-side keys on the key recovery token profile and keep them there, linked to "
            + "the issued certificate, for recovery. Requires serverKeyGeneration. Saving checks the token profile "
            + "(see keyRecoveryTokenProfileUuid)", defaultValue = "false")
    private boolean keyRecovery;

    @Schema(description = "UUID of the token profile that generates and keeps recoverable keys. Saving requires "
            + "that the token profile is enabled, its connector supports key export, a key pair can be exported, a "
            + "v2 token profile allows the SIGN usage, and keyRecoveryKeyAttributes are valid for the connector. "
            + "A token profile used for key recovery cannot be deleted or disabled",
            examples = {"6b55de1c-844f-11ec-a8a3-0242ac120002"})
    private UUID keyRecoveryTokenProfileUuid;

    @Schema(description = "Key creation attributes for the key recovery token profile. Saving validates them "
            + "against the connector and refuses secret-typed content")
    private List<RequestAttribute> keyRecoveryKeyAttributes = new ArrayList<>();

    @Min(0)
    @Schema(description = "Seconds that enroll and reenroll wait for the certificate before answering 202. 0 answers "
            + "at once. The wait holds the HTTP request, so it must stay below the timeout of the gateway in front of "
            + "the platform", minimum = "0", defaultValue = "10")
    private int issueWaitTimeout = 10;

    @Min(1)
    @Schema(description = "Seconds a client is told to wait, as Retry-After, before it asks again after a 202",
            minimum = "1", defaultValue = "30")
    private int retryAfter = 30;

    @Min(1)
    @Schema(description = "Seconds that /serverkeygen waits for the certificate before answering that it could not "
            + "be issued in time. The wait holds the HTTP request, so it must stay below the timeout of the gateway "
            + "in front of the platform", minimum = "1", defaultValue = "30")
    private int serverKeyGenerationTimeout = 30;

    @Schema(description = "Attributes to issue certificates through the EST Profile's own RA Profile")
    private List<RequestAttribute> issueCertificateAttributes = new ArrayList<>();

    @Schema(description = "List of Custom Attributes for EST Profile")
    private List<RequestAttribute> customAttributes = new ArrayList<>();

    @Valid
    @Schema(description = "Owner, groups and custom attributes applied to certificates issued through the EST Profile")
    private ProtocolCertificateAssociationsRequestDto certificateAssociations;

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(
            message = "Basic credential secrets are required for the raBasic policy and not allowed for other policies")
    public boolean isBasicCredentialSecretsConsistent() {
        return authenticationPolicy == null
                || (authenticationPolicy == EstAuthenticationPolicy.RA_BASIC) == hasElements(
                        basicCredentialSecretUuids);
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(
            message = "Trusted CA certificates are required for the vendorMtls and raMtls policies and not allowed for other policies")
    public boolean isTrustedCaCertificatesConsistent() {
        boolean certificatePolicy = authenticationPolicy == EstAuthenticationPolicy.VENDOR_MTLS
                || authenticationPolicy == EstAuthenticationPolicy.RA_MTLS;
        return authenticationPolicy == null || certificatePolicy == hasElements(trustedCaCertificateUuids);
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(
            message = "Key recovery requires server key generation and a token profile, and its settings are not allowed without key recovery")
    public boolean isKeyRecoveryConsistent() {
        if (keyRecovery) {
            return serverKeyGeneration && keyRecoveryTokenProfileUuid != null && keyRecoveryKeyAttributes != null;
        }
        return keyRecoveryTokenProfileUuid == null && !hasElements(keyRecoveryKeyAttributes);
    }

    private static boolean hasElements(List<?> list) {
        return list != null && !list.isEmpty();
    }
}
