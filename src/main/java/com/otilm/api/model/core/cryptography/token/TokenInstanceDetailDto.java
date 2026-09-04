package com.otilm.api.model.core.cryptography.token;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.cryptography.key.KeyTransferAvailabilityDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class TokenInstanceDetailDto extends NameAndUuidDto {

    @Schema(description = "Connector Name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String connectorName;

    @Schema(description = "Connector UUID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String connectorUuid;

    @Schema(description = "Connector kind. Required for connectors based on version 1 of the connector framework;"
            + " omitted for connectors based on version 2 (NG) of the connector framework.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String kind;

    @Schema(description = "Status Of the Token Instance", requiredMode = Schema.RequiredMode.REQUIRED)
    private TokenInstanceStatusDetailDto status;

    @Schema(description = "Number of Token Profiles associated", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer tokenProfiles;

    @Schema(description = "List of Token instance Attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> attributes;

    @Schema(description = "Token instance Metadata", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataResponseDto> metadata;

    @Schema(description = "Custom Attributes for the Token Instance", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes;

    @Schema(description = "Whether key material can be imported into or exported from this token, meaning it is "
            + "available on at least one of its token profiles. The key types each profile accepts are reported on "
            + "that profile.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private KeyTransferAvailabilityDto keyTransfer;

    /**
     * The signature this class carried before {@code keyTransfer} was added, so a caller that constructs it
     * positionally still compiles. Key transfer is then unreported rather than reported as unavailable.
     */
    public TokenInstanceDetailDto(String connectorName, String connectorUuid, String kind,
            TokenInstanceStatusDetailDto status, Integer tokenProfiles, List<ResponseAttribute> attributes,
            List<MetadataResponseDto> metadata, List<ResponseAttribute> customAttributes) {
        this(connectorName, connectorUuid, kind, status, tokenProfiles, attributes, metadata, customAttributes, null);
    }
}
