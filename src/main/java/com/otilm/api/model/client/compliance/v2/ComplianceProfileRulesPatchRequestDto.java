package com.otilm.api.model.client.compliance.v2;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
public class ComplianceProfileRulesPatchRequestDto {

    @NotNull
    @Schema(description = "Indicates if removing or adding rule with UUID specified in request", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private boolean removal;

    @NotNull
    @Schema(description = "UUID of the rule", requiredMode = Schema.RequiredMode.REQUIRED, example = "20354d7a-e4fe-47af-8ff6-187bca92f3f9")
    private UUID ruleUuid;

    @Schema(description = "UUID of the Compliance Provider", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "20354d7a-e4fe-47af-8ff6-187bca92f3f9")
    private UUID connectorUuid;

    @Schema(description = "Kind of the Compliance Provider", requiredMode = Schema.RequiredMode.NOT_REQUIRED, examples = {"default"})
    private String kind;

    @Schema(description = "Attributes for the rule", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> attributes;

    @JsonIgnore
    @AssertTrue(message = "If connector UUID is specified, kind is also required and vice versa")
    public boolean isValid() {
        return (connectorUuid == null && kind == null) || (connectorUuid != null && kind != null);
    }
}
