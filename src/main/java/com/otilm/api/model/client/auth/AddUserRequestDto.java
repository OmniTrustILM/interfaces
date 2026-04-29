package com.otilm.api.model.client.auth;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.Named;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

@Data
public class AddUserRequestDto implements Named {

    @Schema(description = "Username of the user", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"user1"})
    private String username;

    @Schema(description = "Description of the user")
    private String description;

    @Schema(description = "First name of the user")
    private String firstName;

    @Schema(description = "Last name of the user")
    private String lastName;

    @Schema(description = "Email of the user")
    private String email;

    @Schema(description = "Groups UUIDs of the user")
    private List<String> groupUuids = new ArrayList<>();

    @Schema(description = "Status of the user. True = Enabled, False = Disabled")
    private Boolean enabled;

    @Schema(
            description = "Base64 Content of the user certificate"
    )
    private String certificateData;

    @Schema(
            description = "UUID of the existing certificate in the Inventory"
    )
    private String certificateUuid;

    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;

    @Override
    public String getName() {
        return username;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("username", username)
                .append("description", description)
                .append("groupUuids", groupUuids)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("email", email)
                .append("certificateUuid", certificateUuid)
                .append("enabled", enabled)
                .append("certificateData", certificateData)
                .append("customAttributes", customAttributes)
                .toString();
    }
}
