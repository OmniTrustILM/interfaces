package com.otilm.api.model.core.cryptography.key;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.core.certificate.group.GroupDto;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import com.otilm.api.model.core.search.AttributeProjectable;
import com.otilm.api.model.core.search.FilterFieldSource;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Public key-item summary, preserving an absent provider length as null.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class KeyItemDto extends NameAndUuidDto implements AttributeProjectable {

    @Schema(description = "Description of the Key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Creation time of the Key. If the key is discovered from the connector, then it will be returned",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime creationTime;

    @Schema(description = "UUID of the wrapper object", requiredMode = Schema.RequiredMode.REQUIRED)
    private String keyWrapperUuid;

    @Schema(description = "UUID of the Token Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String tokenProfileUuid;

    @Schema(description = "Name of the Token Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String tokenProfileName;

    @Schema(description = "Token Instance UUID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String tokenInstanceUuid;

    @Schema(description = "Token Instance Name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String tokenInstanceName;

    @Schema(description = "Owner of the Key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String owner;

    @Schema(description = "UUID of the owner of the Key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String ownerUuid;

    @Schema(description = "Groups associated to the Key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<GroupDto> groups;

    @Schema(description = "Number of associated objects", requiredMode = Schema.RequiredMode.REQUIRED)
    private int associations;

    @Schema(description = "UUID of the key item in the Connector", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keyReferenceUuid;

    @Schema(description = "Type of the Key", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyType type;

    @Schema(description = "Key Algorithm", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyAlgorithm keyAlgorithm;

    @Schema(description = "Key Format", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyFormat format;

    @Schema(description = "Key length in bits. Required for RSA, ECDSA and AES; optional otherwise.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, nullable = true, minimum = "1")
    private Integer length;

    @Schema(description = "Key Usages", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<KeyUsage> usage;

    @Schema(description = "Boolean describing if the key is enabled or not",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;

    @Schema(description = "Key State", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyState state;

    @Schema(description = "Key compliance status", requiredMode = Schema.RequiredMode.REQUIRED)
    private ComplianceStatus complianceStatus;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = AttributeProjectable.ATTRIBUTE_VALUES_DESCRIPTION,
            example = AttributeProjectable.ATTRIBUTE_VALUES_EXAMPLE, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Map<FilterFieldSource, Map<String, List<BaseAttributeContentV3<?>>>> attributeValues;

    /**
     * Retains the constructor accepting a primitive length for existing callers.
     *
     * @param description wrapper description
     * @param creationTime wrapper creation time
     * @param keyWrapperUuid wrapper identity
     * @param tokenProfileUuid profile identity
     * @param tokenProfileName profile name
     * @param tokenInstanceUuid token identity
     * @param tokenInstanceName token name
     * @param owner owner name
     * @param ownerUuid owner identity
     * @param groups assigned groups
     * @param associations associated object count
     * @param keyReferenceUuid connector key identity
     * @param type key kind
     * @param keyAlgorithm key algorithm
     * @param format material encoding
     * @param length key length in bits
     * @param usage permitted operations
     * @param enabled whether operations are enabled
     * @param state lifecycle state
     * @param complianceStatus compliance outcome
     * @param attributeValues projected attributes
     */
    @SuppressWarnings("java:S107")
    public KeyItemDto(String description, OffsetDateTime creationTime, String keyWrapperUuid, String tokenProfileUuid,
            String tokenProfileName, String tokenInstanceUuid, String tokenInstanceName, String owner, String ownerUuid,
            List<GroupDto> groups, int associations, String keyReferenceUuid, KeyType type, KeyAlgorithm keyAlgorithm,
            KeyFormat format, int length, List<KeyUsage> usage, boolean enabled, KeyState state,
            ComplianceStatus complianceStatus,
            Map<FilterFieldSource, Map<String, List<BaseAttributeContentV3<?>>>> attributeValues) {
        this(description, creationTime, keyWrapperUuid, tokenProfileUuid, tokenProfileName, tokenInstanceUuid,
                tokenInstanceName, owner, ownerUuid, groups, associations, keyReferenceUuid, type, keyAlgorithm, format,
                Integer.valueOf(length), usage, enabled, state, complianceStatus, attributeValues);
    }

    /**
     * Compatibility constructor for callers that construct this DTO positionally, so that adding the optional
     * {@code attributeValues} field stays source- and binary-compatible. Leaves {@code attributeValues} unset; use the
     * setter or the generated all-arguments constructor to populate it.
     *
     * @deprecated retained only for compatibility with callers compiled against a release before
     * {@code attributeValues} existed; new code sets the field explicitly.
     */
    @Deprecated(since = "2.20.0")
    @SuppressWarnings("java:S107")
    public KeyItemDto(String description, OffsetDateTime creationTime, String keyWrapperUuid, String tokenProfileUuid,
            String tokenProfileName, String tokenInstanceUuid, String tokenInstanceName, String owner, String ownerUuid,
            List<GroupDto> groups, int associations, String keyReferenceUuid, KeyType type, KeyAlgorithm keyAlgorithm,
            KeyFormat format, int length, List<KeyUsage> usage, boolean enabled, KeyState state,
            ComplianceStatus complianceStatus) {
        this(description, creationTime, keyWrapperUuid, tokenProfileUuid, tokenProfileName, tokenInstanceUuid,
                tokenInstanceName, owner, ownerUuid, groups, associations, keyReferenceUuid, type, keyAlgorithm, format,
                length, usage, enabled, state, complianceStatus, null);
    }
}
