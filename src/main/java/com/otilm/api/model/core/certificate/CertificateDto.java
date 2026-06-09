package com.otilm.api.model.core.certificate;

import com.otilm.api.model.client.raprofile.SimplifiedRaProfileDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.certificate.group.GroupDto;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CertificateDto implements Loggable {
    @Schema(
            description = "UUID of the Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String uuid;

    @Schema(
            description = "Certificate common name",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String commonName;

    @Schema(
            description = "Certificate serial number",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String serialNumber;

    @Schema(
            description = "Certificate issuer common name",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String issuerCommonName;

    @Schema(
            description = "Issuer DN of the Certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String issuerDn;

    @Schema(
            description = "Subject DN of the Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String subjectDn;

    @Schema(
            description = "Certificate validity start date",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Date notBefore;

    @Schema(
            description = "Certificate expiration date",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Date notAfter;

    @Schema(
            description = "Public key algorithm",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String publicKeyAlgorithm;

    @Schema(
            description = "Alternative Public key algorithm",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String altPublicKeyAlgorithm;

    @Schema(
            description = "Certificate signature algorithm",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String signatureAlgorithm;

    @Schema(
            description = "Certificate alternative signature algorithm",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String altSignatureAlgorithm;

    @Schema(
            description = "Indicator whether the certificate is hybrid",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean hybridCertificate;

    @Schema(
            description = "Certificate key size",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer keySize;

    @Schema(
            description = "Certificate key size of the alternative key",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Integer altKeySize;

    @Schema(
            description = "State of the Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CertificateState state;

    @Schema(
            description = "Current validation status of the certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CertificateValidationStatus validationStatus;

    @Schema(
            description = "RA Profile associated to the Certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private SimplifiedRaProfileDto raProfile;

    @Schema(
            description = "SHA256 fingerprint of the Certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String fingerprint;

    @Schema(
            description = "Groups associated to the Certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<GroupDto> groups;

    @Schema(
            description = "Certificate Owner",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String owner;

    @Schema(
            description = "Certificate Owner UUID",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String ownerUuid;

    @Schema(
            description = "Certificate type",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CertificateType certificateType;

    @Schema(
            description = "Serial number of the issuer",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String issuerSerialNumber;

    @Schema(
            description = "Certificate compliance status",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ComplianceStatus complianceStatus;

    @Schema(
            description = "UUID of the issuer certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String issuerCertificateUuid;

    @Schema(
            description = "Private Key Availability",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean privateKeyAvailability;

    @Schema(
            description = "Indicator whether CA is marked as trusted, set to null if certificate is not CA",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Boolean trustedCa;

    @Schema(
            description = "Certificate is archived",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            defaultValue = "false"
    )
    private boolean archived;

    public CertificateDto(UUID uuid, String commonName, String serialNumber, String issuerCommonName, String issuerDn,
                          String subjectDn, Date notBefore, Date notAfter, String publicKeyAlgorithm, String altPublicKeyAlgorithm,
                          String signatureAlgorithm, String altSignatureAlgorithm, boolean hybridCertificate,
                          Integer keySize, Integer altKeySize, CertificateState state, CertificateValidationStatus validationStatus,
                          UUID raProfileUuid, String raProfileName, Boolean raProfileEnabled, UUID raProfileAuthorityInstanceUuid,
                          String fingerprint, String owner, UUID ownerUuid, CertificateType certificateType,
                          String issuerSerialNumber, ComplianceStatus complianceStatus, UUID issuerCertificateUuid,
                          boolean privateKeyAvailability, Boolean trustedCa, boolean archived) {
        this.uuid = uuid != null ? uuid.toString() : null;
        this.commonName = commonName;
        this.serialNumber = serialNumber;
        this.issuerCommonName = issuerCommonName;
        this.issuerDn = issuerDn;
        this.subjectDn = subjectDn;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
        this.publicKeyAlgorithm = publicKeyAlgorithm;
        this.altPublicKeyAlgorithm = altPublicKeyAlgorithm;
        this.signatureAlgorithm = signatureAlgorithm;
        this.altSignatureAlgorithm = altSignatureAlgorithm;
        this.hybridCertificate = hybridCertificate;
        this.keySize = keySize;
        this.altKeySize = altKeySize;
        this.state = state;
        this.validationStatus = validationStatus;
        if (raProfileUuid != null) {
            this.raProfile = new SimplifiedRaProfileDto(raProfileUuid, raProfileName, raProfileEnabled, raProfileAuthorityInstanceUuid);
        }
        this.fingerprint = fingerprint;
        this.owner = owner;
        this.ownerUuid = ownerUuid != null ? ownerUuid.toString() : null;
        this.certificateType = certificateType;
        this.issuerSerialNumber = issuerSerialNumber;
        this.complianceStatus = complianceStatus;
        this.issuerCertificateUuid = issuerCertificateUuid != null ? issuerCertificateUuid.toString() : null;
        this.privateKeyAvailability = privateKeyAvailability;
        this.trustedCa = trustedCa;
        this.archived = archived;
    }

    @Override
    public Serializable toLogData() {
        return new NameAndUuidDto(this.uuid, this.subjectDn);
    }

    @Override
    public List<String> toLogResourceObjectsNames() {
        return List.of(serialNumber);
    }

    @Override
    public List<UUID> toLogResourceObjectsUuids() {
        return List.of(UUID.fromString(uuid));
    }
}
