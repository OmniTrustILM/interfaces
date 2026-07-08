package com.otilm.api.model.client.auth;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.validation.NullableNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
    @NullableNotBlank(message = "Email cannot be blank if provided")
    private String email;

    @Schema(description = "Groups UUIDs of the user. For creation, omit or provide an empty list for no group membership. " +
            "For updates, omit to leave the current membership unchanged; set to an empty list to remove the user from all groups.")
    private List<String> groupUuids;

    @Schema(
            description = "Base64 content of the user certificate",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String certificateData;

    @Schema(
            description = "UUID of the existing certificate in the inventory. Mandatory if certificate data is not provided",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String certificateUuid;

    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;

    @Schema(description = "List of Custom Attributes set for the user certificate, if a new certificate is uploaded. Ignored if the certificate already exists in the inventory (matched by UUID or fingerprint).",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> certificateCustomAttributes;

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
                .append("certificateCustomAttributes", certificateCustomAttributes)
                .toString();
    }
}
