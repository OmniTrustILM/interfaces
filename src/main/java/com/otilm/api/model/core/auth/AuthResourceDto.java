package com.otilm.api.model.core.auth;

import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthResourceDto extends NameAndUuidDto {
    @Schema(description = "Resource Name",
            examples = {"Name"},
            requiredMode = Schema.RequiredMode.REQUIRED,
            implementation = Resource.class)
    private String name;

    @Schema(description = "Resource label", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayName;

    @Schema(description = "Listing Endpoint")
    private String listObjectsEndpoint;

    @Schema(description = "If resource has Object access permissions. True = Yes, False = No", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean objectAccess;

    @Schema(description = "List of Actions for the Resource", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<AuthActionDto> actions;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("displayName", displayName)
                .append("listingEndPoint", listObjectsEndpoint)
                .append("objectAccess", objectAccess)
                .append("actions", actions)
                .toString();
    }
}
