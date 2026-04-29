package com.otilm.api.model.client.credential;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class CredentialUpdateRequestDto implements Serializable {

    @Schema(description = "List of Credential Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes;

    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("attributes", attributes)
                .append("customAttributes", customAttributes)
                .toString();
    }
}
