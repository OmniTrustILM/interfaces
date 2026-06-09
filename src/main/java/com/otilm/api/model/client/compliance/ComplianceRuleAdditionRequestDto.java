package com.otilm.api.model.client.compliance;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Setter
@Getter
public class ComplianceRuleAdditionRequestDto {

    @Schema(description = "UUID of the Compliance Provider", requiredMode = Schema.RequiredMode.REQUIRED, example = "20354d7a-e4fe-47af-8ff6-187bca92f3f9")
    private String connectorUuid;

    @Schema(description = "Kind of the Compliance Provider", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"default"})
    private String kind;

    @Schema(description = "UUID of the rule", requiredMode = Schema.RequiredMode.REQUIRED, example = "20354d7a-e4fe-47af-8ff6-187bca92f3f9")
    private String ruleUuid;

    @Schema(description = "Attributes for the rule")
    private List<RequestAttribute> attributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("connectorUuid", connectorUuid)
                .append("connectorKind", kind)
                .append("ruleUuid", ruleUuid)
                .append("attributes", attributes)
                .toString();
    }
}
