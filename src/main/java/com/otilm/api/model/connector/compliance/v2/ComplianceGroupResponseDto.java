package com.otilm.api.model.connector.compliance.v2;

import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

/*
List of groups information from the Compliance Provider.
 */
@Getter
@Setter
@ToString
public class ComplianceGroupResponseDto {
    @Schema(description = "UUID of the group", requiredMode = Schema.RequiredMode.REQUIRED, example = "b11c9be1-b619-4ef5-be1b-a1cd9ef265b7")
    private UUID uuid;

    @Schema(description = "Name of the group", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"RFC"})
    private String name;

    @Schema(description = "Description of the group", requiredMode = Schema.RequiredMode.NOT_REQUIRED, examples = {"Sample description of the group"})
    private String description;

    @Schema(description = "Resource of the group", requiredMode = Schema.RequiredMode.NOT_REQUIRED, examples = {Resource.Codes.CERTIFICATE})
    private Resource resource;

}
