package com.otilm.api.model.connector.compliance;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/*
Contains the description for rules and its validation results. This object is part of
the ComplianceResponseDto and describes in detail the list of rules applied and their
individual status.
 */
@Getter
@Setter
public class ComplianceRequestRulesDto {
    @Schema(description = "UUID of the rule",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "b11c9be1-b619-4ef5-be1b-a1cd9ef265b7")
    private String uuid;

    @Schema(description = "Attributes for the rule")
    private List<RequestAttribute> attributes;

    //Default getters and setters

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("attributes", attributes)
                .toString();
    }
}
