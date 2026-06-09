package com.otilm.api.model.connector.compliance.v2;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceRuleRequestDto {

    @NotNull
    @Schema(description = "UUID of the rule",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "20354d7a-e4fe-47af-8ff6-187bca92f3f9")
    private UUID uuid;

    @Schema(description = "Attributes for the rule", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> attributes;

}
