package com.otilm.api.model.core.certificate;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.cryptography.key.KeyDto;
import com.otilm.api.model.core.location.LocationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class CertificateDetailDto extends CertificateDto {

    @Schema(
            description = "Extended key usages",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<String> extendedKeyUsage;

    @Schema(
            description = "Key usages",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<CertificateKeyUsage> keyUsage;

    @Schema(
            description = "Certificate subject type",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private CertificateSubjectType subjectType;

    @Schema(
            description = "Certificate metadata",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<MetadataResponseDto> metadata;

    @Schema(
            description = "Base64 encoded Certificate content",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String certificateContent;

    @Schema(
            description = "Subject alternative names",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Map<String, List<String>> subjectAlternativeNames;

    @Schema(
            description = "Locations associated to the Certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Set<LocationDto> locations;

    /**
     * @deprecated As of release 2.16.0. Replaced by {@link com.otilm.api.interfaces.core.web.v2.ComplianceController#getComplianceCheckResult(Resource, UUID)} endpoint instead that is part of compliance v2 implementation.
     */
    @Deprecated(since = "2.16.0", forRemoval = true)
    @Schema(
            deprecated = true,
            description = "Certificate compliance check result. Deprecated, use `complianceResult` property instead.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<CertificateComplianceResultDto> nonCompliantRules;

    @Schema(
            description = "List of Custom Attributes",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<ResponseAttribute> customAttributes;

    @Schema(
            description = "Key Pair of the certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private KeyDto key;

    @Schema(
            description = "Alternative Key Pair of the certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private KeyDto altKey;

    @Schema(
            description = "Certificate request data",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private CertificateRequestDto certificateRequest;

    @Schema(
            description = "Source certificate UUID"
    )
    @Deprecated(since = "2.16.0", forRemoval = true)
    /**
     * @deprecated source certificates can be retrieved by calling {@link com.otilm.api.interfaces.core.web.CertificateController#getCertificateRelations(UUID)}},
     * returned in {@link CertificateRelationsDto#predecessorCertificates}
     */
    private UUID sourceCertificateUuid;

    @Schema(description = "List of issue attributes")
    private List<ResponseAttribute> issueAttributes = new ArrayList<>();

    @Schema(description = "List of revoke attributes")
    private List<ResponseAttribute> revokeAttributes = new ArrayList<>();

    @Schema(description = "List of related certificates")
    @Deprecated(since = "2.16.0", forRemoval = true)
    /**
     * @deprecated related certificates can be retrieved by calling {@link com.otilm.api.interfaces.core.web.CertificateController#getCertificateRelations(UUID)}},
     * returned in {@link CertificateRelationsDto#successorCertificates}
     */
    private List<CertificateDto> relatedCertificates = new ArrayList<>();

    @Schema(description = "Information about protocol used to issue the certificate")
    private  CertificateProtocolDto protocolInfo;

}
