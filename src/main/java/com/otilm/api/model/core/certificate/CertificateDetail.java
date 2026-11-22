package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.cryptography.key.KeyDto;
import com.otilm.api.model.core.location.LocationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "certificateType",
        visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = X509CertificateDetail.class, name = CertificateType.Codes.X509)})
@Schema(implementation = CertificateDetailDto.class)
public abstract class CertificateDetail extends CertificateDto implements CertificateDetailDto {

    @Schema(description = "Certificate metadata", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataResponseDto> metadata;

    @Schema(description = "Base64 encoded Certificate content", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String certificateContent;

    @Schema(description = "Locations associated to the Certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<LocationDto> locations;

    @Schema(description = "Pre-registration authorization status; present only for certificates that were pre-registered",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateRegistrationDetailDto registration;

    /**
     * @deprecated As of release 2.16.0. Replaced by
     * {@link com.otilm.api.interfaces.core.web.v2.ComplianceController#getComplianceCheckResult(Resource, UUID)}
     * endpoint instead that is part of compliance v2 implementation.
     */
    @Deprecated(since = "2.16.0", forRemoval = true)
    @Schema(deprecated = true,
            description = "Certificate compliance check result. Deprecated, use `complianceResult` property instead.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<CertificateComplianceResultDto> nonCompliantRules;

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes;

    @Schema(description = "Key Pair of the certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private KeyDto key;

    @Schema(description = "Whether the certificate can be downloaded with its private key as PKCS#12",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean keystoreAvailable;

    @Schema(description = "Certificate request data", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateRequestDetail certificateRequest;

    @Schema(description = "Source certificate UUID")
    @Deprecated(since = "2.16.0", forRemoval = true)
    /**
     * @deprecated source certificates can be retrieved by calling
     * {@link com.otilm.api.interfaces.core.web.CertificateController#getCertificateRelations(UUID)}}, returned in
     * {@link CertificateRelationsDto#predecessorCertificates}
     */
    private UUID sourceCertificateUuid;

    @Schema(description = "List of issue attributes")
    private List<ResponseAttribute> issueAttributes = new ArrayList<>();

    @Schema(description = "List of revoke attributes")
    private List<ResponseAttribute> revokeAttributes = new ArrayList<>();

    @Schema(description = "List of register attributes: the connector's register-operation attributes submitted when the certificate was pre-registered")
    private List<ResponseAttribute> registerAttributes = new ArrayList<>();

    @Schema(description = "List of renew attributes: the connector's renew-operation attributes submitted when this certificate was created by renewing or rekeying its predecessor")
    private List<ResponseAttribute> renewAttributes = new ArrayList<>();

    @Schema(description = "List of identify attributes: the connector's identify-operation attributes submitted when the certificate was identified at its authority")
    private List<ResponseAttribute> identifyAttributes = new ArrayList<>();

    @Schema(description = "List of request attributes submitted at registration: the operator-supplied request-attribute values that shaped the pre-registered identity")
    private List<ResponseAttribute> registrationRequestAttributes = new ArrayList<>();

    @Schema(description = "List of related certificates")
    @Deprecated(since = "2.16.0", forRemoval = true)
    /**
     * @deprecated related certificates can be retrieved by calling
     * {@link com.otilm.api.interfaces.core.web.CertificateController#getCertificateRelations(UUID)}}, returned in
     * {@link CertificateRelationsDto#successorCertificates}
     */
    private List<CertificateDto> relatedCertificates = new ArrayList<>();

    @Schema(description = "Information about protocol used to issue the certificate")
    private CertificateProtocolDto protocolInfo;

    @Schema(description = "Operations the certificate supports: what its type supports combined with what its "
            + "authority supports", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<CertificateOperation> supportedOperations = new ArrayList<>();
}
