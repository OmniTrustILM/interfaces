package com.otilm.api.model.client.compliance;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class RaProfileAssociationRequestDto {
    @Schema(description = "List of UUIDs of RA Profiles to be associated",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "[\"18324af0-e95c-11ec-8fea-0242ac120002\",\"18324c94-e95c-11ec-8fea-0242ac120002\"]")
    private List<String> raProfileUuids;

    public List<String> getRaProfileUuids() {
        return raProfileUuids;
    }

    public void setRaProfileUuids(List<String> raProfileUuids) {
        this.raProfileUuids = raProfileUuids;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("raProfileUuids", raProfileUuids)
                .toString();
    }
}
