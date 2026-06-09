package com.otilm.api.model.connector.compliance.v2;

import com.otilm.api.model.core.compliance.ComplianceStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/*
Contains the list of parameters returned by the Compliance Provider after
checking the certificate for compliance. This response will be received
by the Core from the Connector once the compliance check is completed.
 */
@Getter
@Setter
@Schema(name = "ComplianceResponseDtoV2", description = "Response of Compliance Check V2")
public class ComplianceResponseDto {

    @Schema(description = "Status of the compliance check", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = ComplianceStatus.Codes.OK)
    private ComplianceStatus status;

    @Schema(description = "List of rules applied and their status", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ComplianceResponseRuleDto> rules = new ArrayList<>();

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("status", status)
                .append("rules", rules)
                .toString();
    }
}
