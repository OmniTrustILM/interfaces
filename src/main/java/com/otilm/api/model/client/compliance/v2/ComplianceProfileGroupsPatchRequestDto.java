package com.otilm.api.model.client.compliance.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class ComplianceProfileGroupsPatchRequestDto {

    @NotNull
    @Schema(description = "Indicates if removing or adding group with UUID specified in request", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private boolean removal;

    @NotNull
    @Schema(description = "UUID of the group", requiredMode = Schema.RequiredMode.REQUIRED, example = "20354d7a-e4fe-47af-8ff6-187bca92f3f9")
    private UUID groupUuid;

    @NotNull
    @Schema(description = "UUID of the Compliance Provider", requiredMode = Schema.RequiredMode.REQUIRED, example = "20354d7a-e4fe-47af-8ff6-187bca92f3f9")
    private UUID connectorUuid;

    @NotNull
    @Schema(description = "Kind of the Compliance Provider", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"default"})
    private String kind;

}
