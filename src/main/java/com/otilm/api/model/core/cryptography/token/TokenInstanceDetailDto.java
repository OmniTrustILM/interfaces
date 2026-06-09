package com.otilm.api.model.core.cryptography.token;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class TokenInstanceDetailDto extends NameAndUuidDto {

    @Schema(
            description = "Connector Name",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String connectorName;

    @Schema(
            description = "Connector UUID",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String connectorUuid;

    @Schema(
            description = "Connector Kind",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String kind;

    @Schema(
            description = "Status Of the Token Instance",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private TokenInstanceStatusDetailDto status;

    @Schema(
            description = "Number of Token Profiles associated",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Integer tokenProfiles;

    @Schema(
            description = "List of Token instance Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ResponseAttribute> attributes;

    @Schema(
            description = "Token instance Metadata",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<MetadataResponseDto> metadata;

    @Schema(
            description = "Custom Attributes for the Token Instance",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<ResponseAttribute> customAttributes;
}

