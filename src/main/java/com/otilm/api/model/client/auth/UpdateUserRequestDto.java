package com.otilm.api.model.client.auth;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Data
public class UpdateUserRequestDto {

    @Schema(description = "Description of the user")
    private String description;

    @Schema(description = "First name of the user")
    private String firstName;

    @Schema(description = "Last name of the user")
    private String lastName;

    @Schema(description = "Email of the user")
    @Email
    @Size(min = 1)
    private String email;

    @Schema(description = "Groups UUIDs of the user (set to empty list to remove certificate from all groups)")
    private List<String> groupUuids;

    @Schema(
            description = "Base64 Content of the admin certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String certificateData;

    @Schema(
            description = "UUID of the existing certificate in the Inventory. Mandatory if certificate is not provided",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String certificateUuid;

    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("firstName", firstName)
                .append("description", description)
                .append("lastName", lastName)
                .append("groupUuids", groupUuids)
                .append("email", email)
                .append("certificateUuid", certificateUuid)
                .append("certificateData", certificateData)
                .append("customAttributes", customAttributes)
                .toString();
    }
}
