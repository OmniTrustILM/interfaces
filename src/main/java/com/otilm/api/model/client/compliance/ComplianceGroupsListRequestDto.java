package com.otilm.api.model.client.compliance;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ComplianceGroupsListRequestDto {
    @Schema(description = "UUID of the Compliance Provider. If not provided, groups from all the Providers is returned",
            examples = {"c35bc88c-d0ef-11ec-9d64-0242ac120003"},
            defaultValue = "null")
    private String uuid;

    @Schema(description = "Kind of the Compliance Provider. If not provided, groups from all the kinds is returned",
            examples = {"Kind1"},
            defaultValue = "null")
    private String kind;

    //Default getters and setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("kind", kind)
                .toString();
    }
}
